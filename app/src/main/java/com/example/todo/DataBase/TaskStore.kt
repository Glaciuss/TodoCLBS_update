package com.example.todo.DataBase

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
@Parcelize
data class TaskStore(
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    val title:String? = null,
    val subTitle:String? = null,
    val date:String? = null,
    val check:Boolean? = null
):Parcelable