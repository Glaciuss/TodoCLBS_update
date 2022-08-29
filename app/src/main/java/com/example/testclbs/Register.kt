package com.example.testclbs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class Register : AppCompatActivity() {
    lateinit var txtEmailCreate: EditText
    lateinit var txtPasswordCreate:EditText
    lateinit var buttonSubmit: Button

    lateinit var email:String
    lateinit var password:String

    private var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        txtEmailCreate = findViewById<EditText>(R.id.txtEmailCreate)
        txtPasswordCreate = findViewById<EditText>(R.id.txtPasswordCreate)
        buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        var test = database.getReference("SayHi-Regis")
        test.setValue("Hello")

        mAuth = FirebaseAuth.getInstance()
        buttonSubmit!!.setOnClickListener{
            createAccount()
        }
    }

    override fun  onStart(){
        super.onStart()
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun createAccount() {
        email = txtEmailCreate!!.text.toString()
        password = txtPasswordCreate!!.text.toString()
        mAuth!!.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) {
                task -> if(task.isSuccessful){
            Log.d("MyApp","CreateNewUserSuccess!")
            val user = mAuth!!.currentUser

            val databaseReference = database.reference.child("users").push()
            databaseReference.child("uid").setValue(user!!.uid)
            databaseReference.child("email").setValue(user.email)

            updateUI(user)

        } else{
            Log.w("MyApp","FailProcess", task.exception)
            Toast.makeText(this@Register, "Authentication Fail", Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val uid = user.uid
            val email = user.email
            Toast.makeText(this@Register, "Welcome: $email your id is: $uid", Toast.LENGTH_SHORT).show()
            val intentSession = Intent(this, TodoMain::class.java)
            startActivity(intentSession)
        }
    }
}