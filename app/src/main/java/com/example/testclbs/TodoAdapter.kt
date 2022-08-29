package com.example.testclbs

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_todo_adapter.view.*

class TodoAdapter(
    private val todos: ArrayList<Todo>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.activity_todo_adapter,
                parent,
                false
            )
        )
    }

    /*fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }*/

    fun deleteDoneTodos() {
        todos.removeAll { todo ->
            todo.isChecked
        }
        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tvTodoTitle: TextView, isChecked: Boolean) {
        if(isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        var database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        var databaseReference = database.getReference("TodoStorage1")

        val curTodo = todos[position]
        holder.itemView.apply {
            tvTodoTitle.text = curTodo.title
            tvTodoDetail.text = curTodo.details
            cbDone.isChecked = curTodo.isChecked
            ic_delete.setOnClickListener(){
                val titletext: String? = tvTodoTitle.text as String?
                var query = databaseReference.orderByChild("title").equalTo(titletext)
                query.addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        System.out.println(snapshot.key)
                        val keyTodo : String? =  snapshot.key
                        if (keyTodo != null) {
                            databaseReference.child(keyTodo).removeValue()
                        }
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            toggleStrikeThrough(tvTodoTitle, curTodo.isChecked)
            cbDone.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoTitle, isChecked)
                curTodo.isChecked = !curTodo.isChecked
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}
