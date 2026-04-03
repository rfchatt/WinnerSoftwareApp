package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R

class clientRegister : AppCompatActivity() {

    lateinit var tv_already_have_account: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_register)

        tv_already_have_account = findViewById(R.id.tv_already_have_account)
        tv_already_have_account.setOnClickListener {
            startActivity(Intent(this, clientLogin::class.java))
        }

    }
}