package com.example.todo.insert

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.TextUtils.isEmpty
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todo.DataBase.User
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_insert.*
import kotlinx.android.synthetic.main.fragment_insert.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class Insert : Fragment() {

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_insert, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        view.viewInsertBtn.setOnClickListener{
            insertDataToDataBase()
        }
        return view
    }

    private fun insertDataToDataBase() {
        val title = insertTitle.text.toString()
        val sub = insertSub.text.toString()
        val date = date()
        if(!inputCheck(title,sub,date)){
            val userInput = User(0,title,sub,date,check = false)
            mUserViewModel.addUser(userInput)

            //add data to firebase
            val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = database.getReference("users/$userId/getData")
            val id = databaseReference.push().key
            databaseReference.child(id!!).setValue(userInput)

            Toast.makeText(requireContext(),"Success full", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_insert_to_list)
        }else{
            Toast.makeText(requireContext(),"Fail to add task", Toast.LENGTH_SHORT).show()
        }

    }

    private fun inputCheck(title: String, sub: String, date: String): Boolean {
    return (TextUtils.isEmpty(title) && TextUtils.isEmpty(sub))
    }

    private fun date(): String {
        val current = LocalDateTime.now()
        return current.format(DateTimeFormatter.ofPattern("dd/MM/yyy")).toString()
    }
}