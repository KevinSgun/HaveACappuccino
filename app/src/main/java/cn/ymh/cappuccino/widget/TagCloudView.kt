package cn.ymh.cappuccino.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import cn.ymh.cappuccino.R
import cn.ymh.cappuccino.dp2px
import java.util.*
import cn.ymh.cappuccino.App

/**
 * Created by minHeng on 2020-07-30 15:43
 * mail:minhengyan@gmail.com
 *
 * 参考 https://github.com/kingideayou/TagCloudView
 * 增加了向右对齐、随机标签背景色、多选功能
 * 将测量过程和布局过程分离
 */
class TagCloudView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var mTagResId = -1
    private val mChooseTextColor: Int
    private val mIsSingleSelect: Boolean
    private val mIsMultiSelect: Boolean
    private val mBackgroundRandom: Boolean
    private val mChooseBackground: Int
    private var tags: MutableList<String>? = null
    private var onTagClickListener: OnTagClickListener? = null
    private var sizeWidth = 0
    private val mTagSize: Float
    private val mTagColor: Int
    private val mBackground: Int

    /**
     * 每个标签与左边的间距
     */
    private val mTextLeftMargin: Int

    /**
     * 行间距
     */
    private val mTagLineSpacing: Int
    private var mCanTagClick: Boolean = false

    /**
     * 左右内间距
     */
    private val mTextPaddingLR: Int

    /**
     * 上下内间距
     */
    private val mTextPaddingTB: Int
    private val mRowNum: Int

    /**
     * 向右对齐
     */
    private val isStayRight: Boolean

    //	private int[] backRoundResList = {R.drawable.bg_green_round_corner_02e1c3_r80,R.drawable.bg_yellow_round_corner_ff9b05_r80
//	,R.drawable.bg_yellow_round_corner_fcda17_r80,R.drawable.bg_purple_round_corner_f06aff_r80};
    protected var chooseList = SparseArray<TextView>()
    private var mChoosed: TextView? = null
    private var backRoundResList: IntArray? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return !mCanTagClick || super.onInterceptTouchEvent(ev)
    }

    /**
     * 计算 ChildView 宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /*
         * 计算 ViewGroup 上级容器为其推荐的宽高
         */
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        //计算 childView 宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val totalHeight = getMultiTotalHeight()
        /*
         * 高度根据设置改变
         * 如果为 MATCH_PARENT 则充满父窗体，否则根据内容自定义高度
         */setMeasuredDimension(
            sizeWidth,
            if (heightMode == MeasureSpec.EXACTLY) sizeHeight else totalHeight
        )
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        layoutChildView(l, r)
    }

    /**
     * 为 multiLine 模式布局，并计算视图高度
     */
    private fun getMultiTotalHeight(): Int {
        val leftPadding = paddingLeft
        val rightPadding = paddingRight
        var localTotalHeight = paddingTop
        var localTotalWidth = 0
        var childWidth: Int
        var childHeight: Int
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            childWidth = child.measuredWidth
            childHeight = child.measuredHeight
            if (i == 0) {
                localTotalWidth += leftPadding
                localTotalHeight += childHeight
            }
            val localTextLeftMargin = if (i > 0) mTextLeftMargin else 0
            localTotalWidth += childWidth + localTextLeftMargin
            if (localTotalHeight - childHeight >= (childHeight + mTagLineSpacing) * (mRowNum - 1)) {
                return localTotalHeight
            }
            // 保证最右侧与 ViewGroup 右边距有边界
            if (localTotalWidth + rightPadding > sizeWidth) {
                localTotalHeight += (childHeight + mTagLineSpacing)
                localTotalWidth = childWidth + leftPadding
            }
        }
        return localTotalHeight + paddingBottom
    }

    private fun layoutChildView(
        left: Int,
        right: Int
    ) {
        val isRtl =
            ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        val leftPadding = if (isStayRight || isRtl) 0 else paddingLeft
        val maxChildRight = right - left - paddingRight
        var childLeft = leftPadding
        var childRight: Int
        var childTop = paddingTop
        var childBottom: Int

        var childWidth: Int
        var childHeight: Int
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            var localTextLeftMargin = if (i > 0) mTextLeftMargin else 0
            childWidth = child.measuredWidth
            childHeight = child.measuredHeight
            childRight = childLeft + localTextLeftMargin + childWidth

            childBottom = childTop + childHeight
            if (childRight > maxChildRight) {
                localTextLeftMargin = 0
                childLeft = leftPadding
                childTop = childBottom + mTagLineSpacing
            }

            childRight = childLeft + localTextLeftMargin + childWidth
            childBottom = childTop + childHeight
            if (isStayRight || isRtl) {
                child.layout(
                    maxChildRight - childRight,
                    childTop,
                    maxChildRight - childLeft - localTextLeftMargin,
                    childBottom
                )
            } else {
                child.layout(childLeft + localTextLeftMargin, childTop, childRight, childBottom)
            }

            childLeft += localTextLeftMargin + childWidth
        }
    }

    /**
     * @param preventRequestLayout 该参数设置为true时会避免立即重新布局，如果需要设置为true，请自行确保布局流程最终能完成
     */
    fun setTags(tagList: MutableList<String>, preventRequestLayout: Boolean = false) {
        if (tags === tagList) return
        if (tags != null && tags!!.size > 0) {
            tags!!.clear()
            removeAllViews()
        }
        tags = tagList
        if (tags != null && tags!!.size > 0) {
            for (i in tags!!.indices) {
                val tagView = TextView(context)
                tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTagSize)
                tagView.setTextColor(mTagColor)
                val layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                tagView.setPadding(mTextPaddingLR, mTextPaddingTB, mTextPaddingLR, mTextPaddingTB)
                val attrNameString = tags!![i]
                tagView.text = attrNameString
                tagView.tag = TYPE_TEXT_NORMAL
                when {
                    mCanTagClick -> {
                        tagView.setBackgroundResource(mBackground)
                        tagView.setTextColor(mTagColor)
                        tagView.setOnClickListener { v ->
                            if (mIsSingleSelect) markSingleChooseView(
                                v as TextView,
                                i,
                                attrNameString
                            ) else if (mIsMultiSelect) markChooseView(
                                v as TextView,
                                i,
                                attrNameString
                            )
                        }
                    }
                    mBackgroundRandom -> {
                        tagView.setBackgroundResource(randomBackRoundRes())
                        tagView.setTextColor(Color.WHITE)
                    }
                    else -> {
                        tagView.setBackgroundResource(mBackground)
                        tagView.setTextColor(mTagColor)
                    }
                }
                addViewInLayout(tagView, i, layoutParams, true)
            }
            if (!preventRequestLayout) {
                requestLayout()
            }
        }
    }

    fun markSingleChooseView(position: Int, needCallBack: Boolean = false) {
        val child = getChildAt(position) as TextView
        markSingleChooseView(child, position, child.text.toString(), needCallBack)
    }

    private fun markChooseView(
        current: TextView,
        position: Int,
        attrNameString: String?
    ) {
        val choosed = chooseList[position]
        if (current !== choosed) {//选中一个
            current.setBackgroundResource(randomBackRoundRes())
            current.setTextColor(Color.WHITE)
            chooseList.put(position, current)
            if (onTagClickListener != null) onTagClickListener!!.onTagClick(
                position,
                attrNameString,
                true
            )
        } else { //取消选择
            chooseList.remove(position)
            current.setBackgroundResource(mBackground)
            current.setTextColor(mTagColor)
            if (onTagClickListener != null) onTagClickListener!!.onTagClick(
                position,
                attrNameString,
                false
            )
        }
    }

    private fun markSingleChooseView(
        current: TextView,
        position: Int,
        attrNameString: String?,
        needCallBack: Boolean = true
    ) {
        val choosed = chooseList[position]
        if (mChoosed != null && mChoosed !== choosed) {
            mChoosed!!.setBackgroundResource(mBackground)
            mChoosed!!.setTextColor(mTagColor)
        }
        if ((mChoosed == null || mChoosed !== choosed) && needCallBack) {
            if (onTagClickListener != null) onTagClickListener!!.onTagClick(
                position,
                attrNameString,
                true
            )
        }
        mChoosed = current
        current.setBackgroundResource(randomBackRoundRes())
        current.setTextColor(mChooseTextColor)
        chooseList.put(position, current)
    }

    fun getCurrentSingleChosenName(): String {
        if (mChoosed != null)
            return mChoosed!!.text.toString()
        return ""
    }

    fun randomBackRoundRes(): Int {
        val random = Random()
        return if (backRoundResList != null && backRoundResList!!.isNotEmpty()) backRoundResList!![random.nextInt(
            backRoundResList!!.size
        )] else mChooseBackground
    }

    fun setOnTagClickListener(onTagClickListener: OnTagClickListener?) {
        this.onTagClickListener = onTagClickListener
    }

    interface OnTagClickListener {
        /**
         * @param position       选择标签的下标位置
         * @param attrNameString 标签所代表的字符串
         * @param isChoose       是选择该标签还是取消
         */
        fun onTagClick(
            position: Int,
            attrNameString: String?,
            isChoose: Boolean
        )
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    override fun generateLayoutParams(
        attrs: AttributeSet
    ): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(
        p: LayoutParams
    ): LayoutParams {
        return LayoutParams(p)
    }

    fun setRandomBackground(vararg randomBackgrounds: Int) {
        backRoundResList = randomBackgrounds
    }

    companion object {
        private const val TYPE_TEXT_NORMAL = 1
        private val DEFAULT_TEXT_SIZE: Int = dp2px(App.getInstance(), 12F).toInt()
        private const val DEFAULT_TEXT_BACKGROUND = R.drawable.bg_gray_corner60
        private const val DEFAULT_CHOOSE_BACKGROUND = R.drawable.bg_theme_corner60
        private val DEFAULT_TEXT_LEFT_MARGIN: Int = dp2px(App.getInstance(), 8F).toInt()
        private val DEFAULT_TEXT_BORDER_VERTICAL: Int = dp2px(App.getInstance(), 10F).toInt()
        private val DEFAULT_TEXT_PADDING: Int = dp2px(App.getInstance(), 2F).toInt()
        private const val DEFAULT_ROW_NUM = 10086
        private const val DEFAULT_STAY_RIGHT = false
        private const val DEFAULT_SINGLE_SELECT = false
        private const val DEFAULT_BACKGROUND_STATUS = false
        private const val DEFAULT_MULTI_SELECT = false
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagCloudView,
            defStyleAttr,
            defStyleAttr
        )
        val defaultTextColor = ContextCompat.getColor(context, R.color.colorTextGray)
        mChooseTextColor = ContextCompat.getColor(context, R.color.white)
        mTagSize = a.getDimensionPixelSize(
            R.styleable.TagCloudView_tcvTextSize,
            DEFAULT_TEXT_SIZE
        ).toFloat()
        mTagColor = a.getColor(R.styleable.TagCloudView_tcvTextColor, defaultTextColor)
        mBackground = a.getResourceId(
            R.styleable.TagCloudView_tcvBackground,
            DEFAULT_TEXT_BACKGROUND
        )
        mChooseBackground = a.getResourceId(
            R.styleable.TagCloudView_tcvChooseBackground,
            DEFAULT_CHOOSE_BACKGROUND
        )
        mTextLeftMargin = a.getDimensionPixelSize(
            R.styleable.TagCloudView_tcvTextLeftMargin,
            DEFAULT_TEXT_LEFT_MARGIN
        )
        mTagLineSpacing = a.getDimensionPixelSize(
            R.styleable.TagCloudView_tcvItemBorderVertical,
            DEFAULT_TEXT_BORDER_VERTICAL
        )

        mIsSingleSelect = a.getBoolean(
            R.styleable.TagCloudView_tcvIsSingleSelect,
            DEFAULT_SINGLE_SELECT
        )
        mIsMultiSelect = a.getBoolean(
            R.styleable.TagCloudView_tcvIsMultiSelect,
            DEFAULT_MULTI_SELECT
        )
        mCanTagClick = (mIsSingleSelect||mIsMultiSelect)

        mTextPaddingLR = a.getDimensionPixelSize(
            R.styleable.TagCloudView_tcvTagTextLRPadding,
            DEFAULT_TEXT_PADDING * 3
        )
        mTextPaddingTB = a.getDimensionPixelSize(
            R.styleable.TagCloudView_tcvTagTextTBPadding,
            DEFAULT_TEXT_PADDING
        )
        mRowNum =
            a.getInteger(R.styleable.TagCloudView_tcvRowNum, DEFAULT_ROW_NUM)
        isStayRight = a.getBoolean(
            R.styleable.TagCloudView_tcvIsStayRight,
            DEFAULT_STAY_RIGHT
        )
        mBackgroundRandom = a.getBoolean(
            R.styleable.TagCloudView_tcvBackgroundRandom,
            DEFAULT_BACKGROUND_STATUS
        )
        if (a.hasValue(R.styleable.TagCloudView_tcvResId)) {
            mTagResId = a.getResourceId(R.styleable.TagCloudView_tcvResId, -1)
        }
        a.recycle()
    }
}