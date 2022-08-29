package com.example.testclbs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.testclbs.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.sql.DatabaseMetaData

class LoginActivity : AppCompatActivity() {
    lateinit var txtEmail:EditText
    lateinit var txtPassword:EditText
    lateinit var buttonLogin:Button
    lateinit var buttonRegister:Button

    lateinit var email:String
    lateinit var password:String

    private var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase

    //view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_main)
        txtEmail = findViewById<EditText>(R.id.txtEmailCreate)
        txtPassword = findViewById<EditText>(R.id.txtPasswordCreate)
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonRegister = findViewById<Button>(R.id.buttonSubmit)

        mAuth = FirebaseAuth.getInstance()

        /*database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        var test = database.getReference("SayHi-Login")
        test.setValue("Hello")*/

        //Change page to Register
        buttonRegister!!.setOnClickListener{
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

        buttonLogin!!.setOnClickListener{
            loginEmail()
        }

    }

    override fun  onStart(){
        super.onStart()
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun loginEmail() {
        email = txtEmail!!.text.toString()
        password = txtPassword!!.text.toString()
        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
                task -> if(task.isSuccessful){
            Log.d("MyApp","CreateNewUserSuccess!")
            val user = mAuth!!.currentUser

            val databaseReference = database.reference.child("users").push()
            databaseReference.child("uid").setValue(user!!.uid)
            databaseReference.child("email").setValue(user.email)

            updateUI(user)
        } else{
            Log.w("MyApp","FailProcess", task.exception)
            Toast.makeText(this@LoginActivity, "Authentication Fail", Toast.LENGTH_SHORT).show()
            updateUI(null)
        }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val uid = user.uid
            val email = user.email
            Toast.makeText(this@LoginActivity, "Welcome: $email your id is: $uid", Toast.LENGTH_SHORT).show()
            val intentSession = Intent(this, TodoMain::class.java)
            startActivity(intentSession)
        }
    }
}