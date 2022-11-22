package com.example.todo.List

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.DataBase.Task
import com.example.todo.DataBase.UserViewModel
import com.example.todo.LoginActivity
import com.example.todo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*


class ListView : Fragment() {
    private lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val recyclerView = view.recycle
        val adapter = ListRecycler()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        UserViewModel
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        mUserViewModel.readAllData.observe(viewLifecycleOwner, Observer { user ->
            adapter.setData(user)
            //Show empty data
            System.out.println("in list user = $user")
            recycle.adapter = adapter
        })
        //recyclerView.adapter = adapter
        onBindingFirebase()


        //insert button
        view.floatingActionButton2.setOnClickListener{
            findNavController().navigate(R.id.action_list_to_insert)
        }

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delet_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.deleteTask){
            deleteAllUser()
        }
        if (item.itemId == R.id.logoutUser){
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _ ->
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

            Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){ _, _ ->}
        builder.setTitle("Logout ")
        builder.setMessage("Are you sure you want to logout? ")
        builder.create().show()
    }

    private fun deleteAllUser(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){ _, _ ->
            mUserViewModel.deleteAll()

            val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = database.getReference("users/$userId/getData")
            System.out.println("Remove that $userId")
            databaseReference.removeValue()

            Toast.makeText(requireContext(),"deleted successFully",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){ _, _ ->}
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete all of task?")
            builder.create().show()
    }

    private fun onBindingFirebase(){
        //clear data in list and add current list in firebase to list
        val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = database.getReference("users/$userId/getData")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val taskFirebase = userSnapshot.getValue(Task::class.java)
                        if (taskFirebase != null) {
                            mUserViewModel.addUser(taskFirebase as Task)
                        }
                    }
                }
            }
            override fun onCancelled(snapshot: DatabaseError) {
            }
        })
    }
}

