package com.example.todo.xCheck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.DataBase.Task
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.fragment_update.*

class Check : Fragment() {
    private val argsCheck by navArgs<CheckArgs>()
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check, container, false)
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val taskUpdate = Task(argsCheck.check.id,argsCheck.check.title,argsCheck.check.subTitle,argsCheck.check.date,
            check = true)
        //update on room
        mUserViewModel.updateUser(taskUpdate)
        //update to firebase
        val query = UserViewModel.FirebaseURL.databaseReference.orderByChild("title").equalTo(argsCheck.check.title)
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val keyTodo : String? =  snapshot.key
                if (keyTodo != null) {
                    UserViewModel.FirebaseURL.databaseReference.child(keyTodo).setValue(taskUpdate)
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


        findNavController().navigate(R.id.action_check_to_list)
        System.out.println("Check Done!")

        return view
    }
}