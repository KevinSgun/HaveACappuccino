package cn.ymh.cappuccino.actionview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.ymh.cappuccino.R
import cn.ymh.cappuccino.widget.TagCloudView
import kotlinx.android.synthetic.main.activity_tagcloud_display.*

/**

 *Create Time:2020/7/30 16:52

 *Author:yhm

 *Description:

 */
class TagCloudDisplay:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tagcloud_display)

        normal_tag.setTags(mutableListOf("流星","眼泪","说谎","世界以痛吻我","要我","报之以歌","生如夏花","灿烂"))
        right_style_tag.setTags(mutableListOf("生","老","病","死","怨憎会","爱离别","求不得","一曲肝肠断","行到水穷处","坐看云起时"))
        random_style_tag.setRandomBackground(
            R.drawable.bg_theme_corner60,
            R.drawable.bg_fuchsia_corner60,
            R.drawable.bg_gold_corner60,
            R.drawable.bg_lightpink_corner60,
            R.drawable.bg_peachpuff_corner60,
            R.drawable.bg_tomato_corner60,
            R.drawable.bg_greenyellow_corner60,
            R.drawable.bg_paleturquoise_corner60,
            R.drawable.bg_saddlebrown_corner60,
            R.drawable.bg_slategray_corner60)
        random_style_tag.setTags(mutableListOf("生","老","病","死","怨憎会","爱离别","求不得","一曲肝肠断","行到水穷处","坐看云起时","流星","眼泪","说谎","世界以痛吻我","要我","报之以歌","生如夏花","灿烂"))

        single_click_tag.setTags(mutableListOf("生","老","病","死","怨憎会","爱离别","求不得","一曲肝肠断","行到水穷处","坐看云起时"))
        single_click_tag.setOnTagClickListener(object :TagCloudView.OnTagClickListener{
            override fun onTagClick(position: Int, attrNameString: String?, isChoose: Boolean) {
                Toast.makeText(this@TagCloudDisplay, "点击了:${attrNameString}", Toast.LENGTH_SHORT).show()
            }
        })

        multiply_choose_tag.setTags(mutableListOf("生","老","病","死","怨憎会","爱离别","求不得","一曲肝肠断","行到水穷处","坐看云起时"))
        multiply_choose_tag.setOnTagClickListener(object :TagCloudView.OnTagClickListener{
            override fun onTagClick(position: Int, attrNameString: String?, isChoose: Boolean) {
                Toast.makeText(this@TagCloudDisplay, "点击了:${attrNameString}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}