package com.example.testclbs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_todo_adapter.*
import kotlinx.android.synthetic.main.activity_todo_main.*

class TodoMain : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    //database
    private lateinit var database: FirebaseDatabase
    //data reference
    private lateinit var databaseReference: DatabaseReference
    //storage
    private lateinit var firebaseStorage: FirebaseStorage
    //Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //Data Array List
    private var todoArrayList =  ArrayList<Todo>()
    //recycle view
    private lateinit var todoRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_main)
        todoAdapter = TodoAdapter(arrayListOf())

        todoRecyclerView = findViewById(R.id.rvTodoItems)

        // Write a message to the database
        database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        databaseReference = database.getReference("TodoStorage1")
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        //Bind firebase
        onBindingFirebase()

        btnAddTodo.setOnClickListener {
            val todoTitle = etTodoTitle.text.toString()
            val todoDetail = etTodoDetail.text.toString()
            if(todoTitle.isNotEmpty()) {

                //not storage
                //val todo = Todo(todoTitle, details =  todoDetail)
                //todoAdapter.addTodo(todo)

                //Add to storage
                var model = Todo(todoTitle,todoDetail)
                var id = databaseReference.push().key
                databaseReference.child(id!!).setValue(model)
                Toast.makeText(this@TodoMain,"SaveTodo",Toast.LENGTH_SHORT).show()

                etTodoTitle.text.clear()
                etTodoDetail.text.clear()
            }
        }
        btnDeleteDoneTodos.setOnClickListener {
            //todoAdapter.deleteDoneTodos()
            deleteTodoData()
        }
        updateText()
    }

    private fun onBindingFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    todoArrayList.clear()
                    for(userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(Todo::class.java)
                        todoArrayList.add(user as Todo)
                    }
                    if(todoArrayList.size>0){
                        val adapter = TodoAdapter(todoArrayList)
                        rvTodoItems.adapter = adapter
                    }
                }
            }
            override fun onCancelled(snapshot: DatabaseError) {
                Log.e("cancel", snapshot.toString())
            }
        })


    }

    private fun updateText(){
        btnHideKeypad.setOnClickListener{
            closeKeyboard(etTodoTitle)
        }
    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun deleteTodoData(){
        
        var query = databaseReference.orderByChild("checked").equalTo(false)
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildKey: String?) {
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
        //databaseReference.removeValue()
        //databaseReference.child("-NA_KskzvsgSZMyGAhp8").removeValue()
        //Toast.makeText(this@TodoMain,"DeleteTodo",Toast.LENGTH_SHORT).show()
    }
}