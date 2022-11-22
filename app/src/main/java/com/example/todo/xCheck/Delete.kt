package com.example.todo.xCheck

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_update.*

class Delete : Fragment() {
    private val argsDelete by navArgs<DeleteArgs>()
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_check, container, false)
        mUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        //update on room

        //update to firebase
        val query = UserViewModel.FirebaseURL.databaseReference.orderByChild("title").equalTo(argsDelete.del.title)
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val keyTodo : String? =  snapshot.key
                if (keyTodo != null) {
                    UserViewModel.FirebaseURL.databaseReference.child(keyTodo).removeValue()
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

        mUserViewModel.deleteUser(argsDelete.del)

        Toast.makeText(requireContext(),"deleted successFully", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_delete_to_list)
        System.out.println("Delete Done!")

        setHasOptionsMenu(false)
        return view
    }
}