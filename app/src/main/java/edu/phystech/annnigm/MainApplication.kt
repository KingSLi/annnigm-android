package edu.phystech.annnigm

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MainApplication : Application() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        mAuth = FirebaseAuth.getInstance()
        user = User()
        userToken = ""
    }

    companion object {
        lateinit var user: User
        lateinit var userToken: String
        var listGlusoseMeasure: ArrayList<Double> = ArrayList()
        var listInsulinMeasure: ArrayList<Double> = ArrayList()
    }
}