package com.stevenyambos.capotageapp

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialisez Firebase
        FirebaseApp.initializeApp(this)
    }
}
