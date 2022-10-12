package com.example.todo.List

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.DataBase.TaskStore
import com.example.todo.DataBase.User
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.recycler_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TodoAdapter(
    private val todos: ArrayList<TaskStore>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var userList = emptyList<TaskStore>()


    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        var database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = database.getReference("users/$userId/getData")

        val currentItem = userList[position]
        //holder.itemView.recyclerid.text = (position+1).toString()
        holder.itemView.recyclerTitle.text = currentItem.title.toString()
        holder.itemView.recyclerDate.text = currentItem.date.toString()
        holder.itemView.recyclerDetail.text = currentItem.subTitle.toString()
        /*holder.itemView.cbDone.isChecked = currentItem.check
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
        }*/

    }

    fun setData(user: List<TaskStore>){
        this.userList = user
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return todos.size
    }
}
