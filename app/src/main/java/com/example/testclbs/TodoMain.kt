package com.example.testclbs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.example.testclbs.data.AppDatabase
import com.example.testclbs.data.User
import com.example.testclbs.databinding.ActivityTodoMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_todo_adapter.*
import kotlinx.android.synthetic.main.activity_todo_main.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    //binding
    private lateinit var binding: ActivityTodoMainBinding
    //add bt
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var btnSignOut: Button
    //app Database
    private lateinit var appDb : AppDatabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_todo_main)
        todoAdapter = TodoAdapter(arrayListOf())

        todoRecyclerView = findViewById(R.id.rvTodoItems)
        addsBtn = findViewById(R.id.btnAdd)
        btnSignOut = findViewById(R.id.btnSignOut)
        //set dialog
        addsBtn.setOnClickListener{addInfo()}
        btnSignOut.setOnClickListener {logoutFirebase()}

        appDb = AppDatabase.getDatabase(this)

        // Write a message to the database
        database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        databaseReference = database.getReference("users/getData")
        System.out.println("key? = " + userId)
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //val userId = FirebaseAuth.getInstance().currentUser!!.uid

        //rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        //Bind firebase
        onBindingFirebase()

        updateRoom()

        /*btnAddTodo.setOnClickListener {

        }*/
        /*btnDeleteDoneTodos.setOnClickListener {
            updateText()
        }*/
    }

    private fun logoutFirebase() {
        FirebaseAuth.getInstance().signOut()
        val intentSession = Intent(this, LoginActivity::class.java)
        startActivity(intentSession)
        finish()
        Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO){
            val list = appDb.userDao().getAll()
            binding.rvTodoItems.apply {
                layoutManager = LinearLayoutManager(this@TodoMain)
                /*adapter = TodoAdapter().apply {
                    setOnAc
                }*/
            }
        }
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item,null)
        //set view
        val todoTitle = v.findViewById<EditText>(R.id.TodoTitle)
        val todoDetail = v.findViewById<EditText>(R.id.TodoDetail)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
            dialog,_->
            val title = todoTitle.text.toString()
            val detail = todoDetail.text.toString()
            if(title.isNotEmpty() && detail.isNotEmpty()) {

                //Create User object
                val user = User(null,title,detail)

                //Add Data to database
                GlobalScope.launch(Dispatchers.IO){
                    appDb.userDao().insert(user)
                }
                Toast.makeText(this,"successfully added",Toast.LENGTH_SHORT).show()

                //Add to storage
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                databaseReference = database.getReference("users/$userId/getData")
                val id = databaseReference.push().key
                databaseReference.child(id!!).setValue(user)
                Toast.makeText(this,"Adding todo information success",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Please fill",Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
            dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun checkUser() {
        //get current
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            val email = firebaseUser.email
        }
    }

    private fun onBindingFirebase() {
        //clear data in list and add current list in firebase to list
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = database.getReference("users/$userId/getData")
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    todoArrayList.clear()
                    for(userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(Todo::class.java)
                        todoArrayList.add(user as Todo)
                    }
                    //show list that add in firebase
                    if(todoArrayList.size>=1){
                        val adapter = TodoAdapter(todoArrayList)
                        rvTodoItems.adapter = adapter
                    }
                }
                else{
                    //show 0 list
                    todoArrayList.clear()
                    val adapter = TodoAdapter(todoArrayList)
                    rvTodoItems.adapter = adapter
                }
            }
            override fun onCancelled(snapshot: DatabaseError) {
                Log.e("cancel", snapshot.toString())
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateText(){
        /*btnHideKeypad.setOnClickListener{
            closeKeyboard(etTodoTitle)

            //Add Delete to database
            GlobalScope.launch(Dispatchers.IO){
                appDb.userDao().deleteAll()
            }
        }*/
    }

    private fun updateRoom(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = database.getReference("users/$userId/getData")
        //delete todo in firebase
        databaseReference.removeValue()

        lifecycleScope.launch(Dispatchers.IO){
            val list = appDb.userDao().getAll()
            //appDb.userDao().delete("1552")
            System.out.println("List size = " + list.size + " List data = " + list)

            //add room data to firebase
            for (item in list){
                print("Item in List = $item")
                val id = databaseReference.push().key
                databaseReference.child(id!!).setValue(item)
            }
        }
    }

    private fun closeKeyboard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*private fun deleteTodoData(){

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
    }*/
}