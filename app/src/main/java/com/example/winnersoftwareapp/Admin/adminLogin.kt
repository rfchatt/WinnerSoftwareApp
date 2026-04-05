package com.example.winnersoftwareapp.Admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton

class  adminLogin : AppCompatActivity() {

    lateinit var mb_admin_login: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_login)

        mb_admin_login = findViewById(R.id.mb_admin_login)

        mb_admin_login.setOnClickListener {
            startActivity(Intent(this, adminHome::class.java))
        }

    }
}