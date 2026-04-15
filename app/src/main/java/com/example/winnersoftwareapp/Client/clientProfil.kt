package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.MainActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class clientProfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_profil)

        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val mb_logout = findViewById<MaterialButton>(R.id.mb_logout)
        val mb_edit_profile = findViewById<MaterialButton>(R.id.mb_edit_profile)

        // Setup Bottom Navigation
        bottom_menu.selectedItemId = R.id.profil
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> true
                R.id.home -> {
                    navigateTo(clientHome::class.java)
                    true
                }
                R.id.ticket -> {
                    navigateTo(clientTicket::class.java)
                    true
                }
                else -> false
            }
        }

        // Logout Action
        mb_logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Edit Profile Action
        mb_edit_profile.setOnClickListener {
            Toast.makeText(this, "Modification bientôt disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateTo(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }
}
