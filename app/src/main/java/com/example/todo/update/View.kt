package com.example.todo.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.DataBase.User
import com.example.todo.DataBase.UserViewModel
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_view.view.*

class View : Fragment() {
    private val args by navArgs<ViewArgs>()

    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view, container, false)
        view.viewDate.setText(args.viewParcel.date).toString()
        view.viewTitle.setText(args.viewParcel.title).toString()
        view.viewSub.setText(args.viewParcel.subTitle).toString()

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        view.updateBtn.setOnClickListener{
            val action = ViewDirections.actionViewToUpdate(User(args.viewParcel.id,args.viewParcel.title,args.viewParcel.subTitle,args.viewParcel.date,args.viewParcel.check))
            findNavController().navigate(action)
        }
        view.DoneBtn.setOnClickListener{
            val userUpdate = User(args.viewParcel.id,args.viewParcel.title,args.viewParcel.subTitle,args.viewParcel.date,check = true)
            mUserViewModel.updateUser(userUpdate)
            findNavController().navigate(R.id.action_view_to_list)
        }
        view.NotDoneBtn.setOnClickListener{
            val userUpdate = User(args.viewParcel.id,args.viewParcel.title,args.viewParcel.subTitle,args.viewParcel.date,check = false)
            mUserViewModel.updateUser(userUpdate)
            findNavController().navigate(R.id.action_view_to_list)
        }

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delet_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.delete){
            deleteUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _ ->

            //delete data from firebase
            val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = database.getReference("users/$userId/getData")
            val query = databaseReference.orderByChild("title").equalTo(args.viewParcel.title)
            query.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    System.out.println("key to delete = " + snapshot.key)
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
            //delete data from room
            mUserViewModel.deleteUser(args.viewParcel)

            Toast.makeText(requireContext(),"deleted succesFully",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_view_to_list)
        }
        builder.setNegativeButton("No"){ _, _ ->}
            builder.setTitle("Delete ${args.viewParcel.title}")
            System.out.println("Deltt = "+args.viewParcel)
            builder.setMessage("Are you shure you want to delete this ${args.viewParcel.title}")
            builder.create().show()
    }
}

