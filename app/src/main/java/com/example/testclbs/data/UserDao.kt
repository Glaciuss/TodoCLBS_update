package com.example.testclbs.data

import androidx.annotation.RequiresPermission
import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)

    @Query("DELETE FROM user_table WHERE Id = :title")
    fun delete(title: String?)

    @Query("DELETE FROM user_table")
    fun deleteAll()
}