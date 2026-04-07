package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.resources.MaterialAttributes

class clientHome : AppCompatActivity() {
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    lateinit var mb_addTicket: MaterialButton
    lateinit var mb_callTechnician: MaterialButton

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

        mb_addTicket.setOnClickListener {
            startActivity(Intent(this, clientTicket::class.java))
        }

        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

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
                    startActivity(Intent(this, clientProfil::class.java))
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