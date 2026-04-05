package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton

class clientLogin : AppCompatActivity() {
    lateinit var tv_create_new_account: TextView
    lateinit var mb_client_login: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_login)

        tv_create_new_account = findViewById(R.id.tv_create_new_account)
        mb_client_login = findViewById(R.id.mb_client_login)

        tv_create_new_account.setOnClickListener {
            startActivity(Intent(this, clientRegister::class.java))
        }

        mb_client_login.setOnClickListener {
            startActivity(Intent(this, clientHome::class.java))
        }

    }
}