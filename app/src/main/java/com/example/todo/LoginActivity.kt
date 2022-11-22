package com.example.todo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {
    lateinit var txtEmail:EditText
    lateinit var txtPassword:EditText
    lateinit var buttonLogin:Button
    lateinit var buttonRegister:Button

    lateinit var email:String
    lateinit var password:String

    private var mAuth: FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    //data reference
    private lateinit var databaseReference: DatabaseReference

    //view binding
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    //constant
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure the Google SighIn
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //1st build = red
            .requestEmail() //only need email from google account
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //init firebase auth
        mAuth = FirebaseAuth.getInstance()
        checkUser()
        // Google SignIn Button, Click to begin Google SignIn
        binding.googleSignBtn.setOnClickListener{
            //begin Google SignIn
            Log.d(TAG,"onCreate: begin Google SignIn")


            val intent = googleSignInClient.signInIntent

            //caller old
            //startActivityForResult(intent, RC_SIGN_IN)
            //caller
            resultLauncher.launch(intent)
        }

        txtEmail = findViewById<EditText>(R.id.txtEmailCreate)
        txtPassword = findViewById<EditText>(R.id.txtPasswordCreate)
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonRegister = findViewById<Button>(R.id.buttonSubmit)

        database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        //var test = database.getReference("SayHi-Login")
        //test.setValue("Hello")*/

        //Change page to Register
        buttonRegister!!.setOnClickListener{
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

        buttonLogin!!.setOnClickListener{
            email = txtEmail!!.text.toString()
            password = txtPassword!!.text.toString()
            if(email.isEmpty()||password.isEmpty()){
                Toast.makeText(this@LoginActivity,"Please fill",Toast.LENGTH_SHORT).show()
            }
            else{
                loginEmail()
                buttonLogin.isEnabled = false
            }
        }
    }

    private fun checkUser() {
        val firebaseUser = mAuth!!.currentUser
        if (firebaseUser != null){
            val intentSession = Intent(this, MainActivity::class.java)
            startActivity(intentSession)
            finish()
        }
    }
    //receiver old
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: google signIn intent")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e:Exception){
                //failed SignIn
                Log.d(TAG,"onActivityResult: failed ${e.message}")
            }
        }
    }*/
    //receiver
    var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data

            Log.d(TAG, "onActivityResult: google signIn intent")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e:Exception){
                //failed SignIn
                Log.d(TAG,"onActivityResult: failed ${e.message}")
            }

        }
    }


    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG,"firebaseAuthWithGoogleAccount: begin")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        mAuth!!.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG,"firebaseAuthWithGoogleAccount: LoggedIn")
                val firebaseUser = mAuth!!.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email
                Log.d(TAG,"firebaseAuthWithGoogleAccount: Email: $email")

                databaseReference = database.getReference("users/" + firebaseUser!!.uid)
                databaseReference.child("email").setValue(firebaseUser!!.email)

                //check user new/exist
                if(authResult.additionalUserInfo!!.isNewUser){
                    // new
                    Log.d(TAG,"firebaseAuthWithGoogleAccount: AccountCreate...\n$email")
                    Toast.makeText(this@LoginActivity,"AccountCreate...\n$email",Toast.LENGTH_SHORT).show()
                }
                else{
                    //exist
                    Log.d(TAG,"firebaseAuthWithGoogleAccount: Existing user...\n$email")
                    Toast.makeText(this@LoginActivity,"LoggedIn...\n$email",Toast.LENGTH_SHORT).show()
                }
                //start profile activity
                val intentSession = Intent(this, MainActivity::class.java)
                startActivity(intentSession)
                finish()
            }
            .addOnFailureListener{e ->
                //login failed
                Log.d(TAG,"firebaseAuthWithGoogleAccount: Login failed due to ${e.message}")
                Toast.makeText(this@LoginActivity,"Login failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun  onStart(){
        super.onStart()
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun loginEmail() {
        val email = txtEmail!!.text.toString()
        val password = txtPassword!!.text.toString()
        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) {
                task -> if(task.isSuccessful){
            Log.d("MyApp","CreateNewUserSuccess!")
            val user = mAuth!!.currentUser

            databaseReference = database.getReference("users/" + user!!.uid)
            //databaseReference.child("uid").setValue(user!!.uid)
            databaseReference.child("email").setValue(user!!.email)

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
            val intentSession = Intent(this, MainActivity::class.java)
            startActivity(intentSession)
        }
    }
}