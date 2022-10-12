package com.example.todo.List

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.DataBase.TaskStore
import com.example.todo.DataBase.User
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import kotlinx.android.synthetic.main.fragment_view.view.*
import kotlinx.android.synthetic.main.recycler_layout.view.*

class ListRecycler:RecyclerView.Adapter<ListRecycler.MyViewHolder>() {

    private var userList = emptyList<User>()

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        //holder.itemView.recyclerid.text = (position+1).toString()
        holder.itemView.recyclerTitle.text = currentItem.title.toString()
        holder.itemView.recyclerDate.text = currentItem.date.toString()
        holder.itemView.recyclerDetail.text = currentItem.subTitle.toString()
        holder.itemView.cbDone.isChecked = currentItem.check
        holder.itemView.recyclerClick.setOnClickListener{
            val action = ListViewDirections.actionListToView(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        holder.itemView.cbDone.setOnClickListener{
            val action = ListViewDirections.actionListToView(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        if(currentItem.check){
            System.out.println("asdp[qwnasdma;lda[ekiwqpkda]pwoe[q")
        }
    }

    fun setData(user: List<User>){
        this.userList = user
        notifyDataSetChanged()

    }

    fun clearData(){
        userList = emptyList()
    }

    fun returnList():List<User>{
        return userList
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
