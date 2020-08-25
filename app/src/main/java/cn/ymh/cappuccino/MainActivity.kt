package cn.ymh.cappuccino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ymh.cappuccino.actionview.TagCloudDisplay
import cn.ymh.cappuccino.model.ActionType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        val mAdapter = MainAdapter(listOf(ActionType.TagCloud()),this)
        main_rv.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onItemClick(item:ActionType,position: Int) {
        when(item){
            ActionType.TagCloud() ->{
                startActivity<TagCloudDisplay>()
            }
        }
    }
}