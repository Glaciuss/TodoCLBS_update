package com.example.todo.Repo

import androidx.lifecycle.LiveData
import com.example.todo.DataBase.User
import com.example.todo.DataBase.UserDao

class Reposotory(private val userDao:UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }

    suspend fun deleteAll(){
        userDao.deleteAll()
    }
}