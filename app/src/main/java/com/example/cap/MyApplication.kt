package com.example.cap

import android.app.Application
import com.example.cap.database.AppDatabase

class MyApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

}