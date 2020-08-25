package cn.ymh.cappuccino

import android.app.Application

/**

 *Create Time:2020/7/30 15:51

 *Author:yhm

 *Description:

 */
class App:Application() {
    companion object{
        lateinit var mInstance:App
        fun getInstance():App = mInstance
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }
}