package com.example.todo.Repo

import androidx.lifecycle.LiveData
import com.example.todo.DataBase.Task
import com.example.todo.DataBase.UserDao

class Reposotory(private val userDao:UserDao) {

    val readAllData: LiveData<List<Task>> = userDao.readAllData()

    suspend fun addUser(task: Task){
        userDao.addUser(task)
    }

    suspend fun updateUser(task: Task){
        userDao.updateUser(task)
    }

    suspend fun deleteUser(task: Task){
        userDao.deleteUser(task)
    }

    suspend fun deleteAll(){
        userDao.deleteAll()
    }
}