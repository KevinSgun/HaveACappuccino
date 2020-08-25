package cn.ymh.cappuccino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ymh.cappuccino.model.ActionType

/**

 *Create Time:2020/7/30 16:24

 *Author:yhm

 *Description:

 */
class MainAdapter(private val dataList:List<ActionType>,val onItemClickListener: OnItemClickListener):RecyclerView.Adapter<MainAdapter.ContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_main,parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.render(dataList[position])
    }

    inner class ContentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val itemTv:TextView  = itemView.findViewById(R.id.item_tv)
        fun render(item:ActionType){
            itemTv.text = item.value
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(item,adapterPosition)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(item:ActionType,position: Int)
    }
}