package cn.ymh.cappuccino

import android.content.Context
import android.util.TypedValue

/**

 *Create Time:2020/7/30 15:44

 *Author:yhm

 *Description:

 */
fun dp2px(context: Context, dp: Float): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
}

