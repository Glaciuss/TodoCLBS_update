package com.example.todo.List

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.DataBase.Task
import com.example.todo.R
import kotlinx.android.synthetic.main.recycler_layout.view.*

class ListRecycler:RecyclerView.Adapter<ListRecycler.MyViewHolder>() {

    private var taskList = emptyList<Task>()

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.itemView.recyclerTitle.text = currentItem.title.toString()
        holder.itemView.recyclerDate.text = currentItem.date.toString()
        holder.itemView.recyclerDetail.text = currentItem.subTitle.toString()
        holder.itemView.cbDone.isChecked = currentItem.check
        holder.itemView.recyclerClick.setOnClickListener{
            val action = ListViewDirections.actionListToUpdate(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
        holder.itemView.cbDone.setOnClickListener{
            //if check
            if(currentItem.check){
                val action = ListViewDirections.actionListToUncheck(currentItem)
                holder.itemView.findNavController().navigate(action)
            }
            //if uncheck
            else if(!currentItem.check){
                val action = ListViewDirections.actionListToCheck(currentItem)
                holder.itemView.findNavController().navigate(action)
            }
        }
        holder.itemView.btDelete.setOnClickListener{
            val action = ListViewDirections.actionListToView(currentItem)
            holder.itemView.findNavController().navigate(action)
            //System.out.println("Click delete")
        }
    }

    fun setData(task: List<Task>){
        this.taskList = task
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
