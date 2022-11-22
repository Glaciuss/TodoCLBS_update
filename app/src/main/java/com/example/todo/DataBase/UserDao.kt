package com.example.todo.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*

//Data Access Object

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(task: Task)

    @Update
    fun updateUser(task: Task)

    @Query("SELECT * FROM todoList ORDER BY id ASC")
    fun readAllData():LiveData<List<Task>>

    @Delete
    fun deleteUser(task: Task)

    @Query("DELETE FROM todoList")
    fun deleteAll()

    @Query("SELECT * FROM todoList ORDER BY id ASC")
    fun getData():List<Task>

}