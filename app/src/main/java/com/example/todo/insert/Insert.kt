package com.example.todo.insert

import android.os.Build
import android.os.Bundle
import android.text.TextUtils.isEmpty
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todo.DataBase.Task
import com.example.todo.DataBase.UserViewModel
import com.example.todo.List.ListRecycler
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
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

        val adapter = ListRecycler()
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)
            val idSize = user.size
            view.viewInsertBtn.setOnClickListener{
                insertDataToDataBase(idSize)
            }
        })
        return view
    }

    private fun insertDataToDataBase(x:Int) :Int{
        val title = insertTitle.text.toString()
        val sub = insertSub.text.toString()
        val date = date()

        if(!inputCheck(title,sub,date)){
            //add data to firebase
            val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = database.getReference("users/$userId/getData")
            val id = databaseReference.push().key
            //val random = abs((0..999999999999).random().toInt())
            System.out.println("Check Number of ID = $x")
            val taskInput = Task(x,title,sub,date, check = false)
            databaseReference.child(id!!).setValue(taskInput)
            //add to room
            //mUserViewModel.addUser(userInput)

            //add to Reposotory
            Toast.makeText(requireContext(),"Success full", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_insert_to_list)
        }else{
            Toast.makeText(requireContext(),"Fail to add task", Toast.LENGTH_SHORT).show()
        }
        return x
    }

    private fun inputCheck(title: String, sub: String, date: String): Boolean {
    return (isEmpty(title) && isEmpty(sub))
    }

    private fun date(): String {
        val current = LocalDateTime.now()
        return current.format(DateTimeFormatter.ofPattern("dd/mm/yy")).toString()

        //1,2,3... day ago?
    }
}