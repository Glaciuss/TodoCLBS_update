package com.example.todo.update

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.DataBase.TaskStore
import com.example.todo.DataBase.User
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_insert.*
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.android.synthetic.main.fragment_update.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
class Update : Fragment() {
    private val argsUpdate by navArgs<UpdateArgs>()
    private lateinit var mUserViewModel:UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        view.updateTitle.setText(argsUpdate.parcelUpdate.title.toString())
        view.updateSubtitle.setText(argsUpdate.parcelUpdate.subTitle.toString())
        //view.up
        view.updateBtn.setOnClickListener{
            updateDataToDataBase()
        }
        return view
    }
    private fun updateDataToDataBase() {
        val updateT = updateTitle.text.toString()
        val updateSub = updateSubtitle.text.toString()
        val updateDate = date()
        if(!inputCheck(updateT,updateSub,updateDate)){
            val userUpdate = User(argsUpdate.parcelUpdate.id,updateT,updateSub,updateDate,argsUpdate.parcelUpdate.check)

            //update data to firebase
            val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = database.getReference("users/$userId/getData")
            val query = databaseReference.orderByChild("title").equalTo(argsUpdate.parcelUpdate.title.toString())
            query.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    System.out.println("key to update is = "+ snapshot.key)
                    val keyTodo : String? =  snapshot.key
                    if (keyTodo != null) {
                        databaseReference.child(keyTodo).setValue(userUpdate)
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
            //update data to room database
            mUserViewModel.updateUser(userUpdate)

            Toast.makeText(requireContext(),"Updated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_update_to_list)
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