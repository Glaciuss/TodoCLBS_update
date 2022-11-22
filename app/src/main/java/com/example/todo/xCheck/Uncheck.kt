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

class Uncheck : Fragment() {
    private val argsUncheck by navArgs<UncheckArgs>()
    private lateinit var mUserViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_uncheck, container, false)
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val taskUpdate = Task(argsUncheck.uncheck.id,argsUncheck.uncheck.title,argsUncheck.uncheck.subTitle,argsUncheck.uncheck.date,
            check = false)

        //update on room
        mUserViewModel.updateUser(taskUpdate)
        //update to firebase
        val query = UserViewModel.FirebaseURL.databaseReference.orderByChild("title").equalTo(argsUncheck.uncheck.title)
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

        findNavController().navigate(R.id.action_uncheck_to_list)
        System.out.println("UnCheck Done!")

        return view
    }
}