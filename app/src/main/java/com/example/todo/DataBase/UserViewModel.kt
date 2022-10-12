package com.example.todo.DataBase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.Repo.Reposotory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application):AndroidViewModel(application) {
    val readAllData: LiveData<List<User>>
    private val reposotory:Reposotory
    init{
        val userDao = UserDatabase.getData(application).userDao()
        reposotory = Reposotory(userDao)
        readAllData = reposotory.readAllData
    }
    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.addUser(user)
        }
    }
    fun updateUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.updateUser(user)
        }
    }
    fun deleteUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.deleteUser(user)
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            reposotory.deleteAll()
        }
    }
}