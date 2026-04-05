package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class clientTicket : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_ticket)

        // Fix: Use correct ID bottomNavigation
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottom_menu.selectedItemId = R.id.ticket
        bottom_menu.itemIconTintList = null
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> {
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, clientHome::class.java))
                    finish() // Finish ticket activity when going home
                    true
                }
                R.id.ticket -> true
                else -> false
            }
        }
    }
}