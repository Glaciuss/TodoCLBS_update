package com.example.todo.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*

//Data Access Object

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM todoList ORDER BY id ASC")
    fun readAllData():LiveData<List<User>>

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM todoList")
    suspend fun deleteAll()

    @Query("SELECT * FROM todoList ORDER BY id ASC")
    fun getData():List<User>

}