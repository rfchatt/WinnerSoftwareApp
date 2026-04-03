package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R

class clientLogin : AppCompatActivity() {

    lateinit var tv_create_new_account: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_login)

        tv_create_new_account = findViewById(R.id.tv_create_new_account)
        tv_create_new_account.setOnClickListener {
            startActivity(Intent(this, clientRegister::class.java))
        }

    }
}