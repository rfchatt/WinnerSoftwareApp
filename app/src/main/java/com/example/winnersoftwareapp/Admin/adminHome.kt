package com.example.winnersoftwareapp.Admin

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.winnersoftwareapp.Client.clientTicket
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class adminHome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var ivHamburger: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        ivHamburger = findViewById(R.id.iv_hamburger)

        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Setup Hamburger Click
        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Setup Drawer Item Clicks
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    finish()
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
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
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}