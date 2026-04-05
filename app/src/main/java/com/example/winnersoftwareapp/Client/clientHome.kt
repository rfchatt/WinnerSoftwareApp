package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.resources.MaterialAttributes

class clientHome : AppCompatActivity() {

    lateinit var mb_addTicket: MaterialButton
    lateinit var mb_callTechnician: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_home)

        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        mb_addTicket = findViewById(R.id.mb_addTicket)
        mb_callTechnician = findViewById(R.id.mb_callTechnician)

        mb_addTicket.setOnClickListener {
            startActivity(Intent(this, clientTicket::class.java))
        }

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

        mb_callTechnician.setOnClickListener {
            startActivity(Intent(this, clientDemande::class.java))
        }

    }
}