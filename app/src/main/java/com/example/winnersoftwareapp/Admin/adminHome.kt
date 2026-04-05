package com.example.winnersoftwareapp.Admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.Client.clientTicket
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class adminHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_menu.selectedItemId = R.id.home
        bottom_menu.itemIconTintList = null
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> {
                    // Handle profile
                    true
                }
                R.id.home -> true
                R.id.ticket -> {
                    startActivity(Intent(this, clientTicket::class.java))
                    true
                }
                else -> false
            }
        }


    }
}