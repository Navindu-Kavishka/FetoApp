package com.example.fetoapp

import android.app.Application

class MyApplication : Application() {

    companion object{

        lateinit var instance : Application
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}