package com.ekcmn.kotlinstoragedeneme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ekcmn.kotlinstoragedeneme.R
import kotlinx.android.synthetic.main.list_items.view.*

class RecyclerViewAdapter(
    private val itemList: ArrayList<String>,
    private val listener: OnItemClickListener
                          ) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_items, parent, false)
        return MyViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.fileName.text = itemList[position]
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateImageList(updatedList: List<String>) {
        itemList.clear()
        itemList.addAll(updatedList)
        notifyDataSetChanged()
    }
    fun getFileName(position:Int):String{
        return itemList[position]
    }
   inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val fileName: TextView = view.object_name

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                if (view!=null){
                    listener.onItemClick(position, view.download_icon,
                        view.remove_icon,view.info_icon)
                }
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int,downloadIcon:View,removeIcon:View,infoIcon:View)
    }


}


