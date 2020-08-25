package cn.ymh.cappuccino.model

/**

 *Create Time:2020/7/30 16:25

 *Author:yhm

 *Description:

 */
sealed class ActionType(open val value:String) {
    data class TagCloud(override val value:String = "标签云"):ActionType(value)
}