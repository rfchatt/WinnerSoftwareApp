package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.winnersoftwareapp.MainActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class clientHome : AppCompatActivity() {
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mb_addTicket: MaterialButton
    private lateinit var mb_callTechnician: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val ivHamburger = findViewById<ImageView>(R.id.iv_hamburger)
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        mb_addTicket = findViewById(R.id.mb_addTicket)
        mb_callTechnician = findViewById(R.id.mb_callTechnician)

        // Menu Hamburger Click
        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navigation Drawer Item Clicks
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        // Bottom Navigation Setup
        bottom_menu.selectedItemId = R.id.home
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> {
                    navigateTo(clientProfil::class.java)
                    true
                }
                R.id.home -> true
                R.id.ticket -> {
                    navigateTo(clientTicket::class.java)
                    true
                }
                else -> false
            }
        }

        // Action Buttons
        mb_addTicket.setOnClickListener {
            navigateTo(clientTicket::class.java)
        }

        mb_callTechnician.setOnClickListener {
            navigateTo(clientDemande::class.java)
        }
    }

    // Helper function to manage Activity Stack
    private fun navigateTo(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}