package com.example.todo.DataBase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "todoList")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int,

    val title:String,
    val subTitle:String,
    val date:String,
    val check:Boolean
):Parcelable
