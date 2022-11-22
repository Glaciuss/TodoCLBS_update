package com.example.todo.DataBase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.Repo.Reposotory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application):AndroidViewModel(application) {
    val readAllData: LiveData<List<Task>>
    private val reposotory:Reposotory

    //firebase
    object FirebaseURL {
        val database = FirebaseDatabase.getInstance("https://testcls-c7487-default-rtdb.asia-southeast1.firebasedatabase.app/")
        private val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = database.getReference("users/$userId/getData")
    }

    init{
        val userDao = UserDatabase.getData(application).userDao()
        reposotory = Reposotory(userDao)
        readAllData = reposotory.readAllData
    }
    fun addUser(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.addUser(task)
        }
    }
    fun updateUser(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.updateUser(task)
        }
    }
    fun deleteUser(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.deleteUser(task)
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.deleteAll()
        }
    }
}