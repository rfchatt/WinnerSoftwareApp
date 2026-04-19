package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.winnersoftwareapp.MainActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class clientHome : AppCompatActivity() {
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var mb_addTicket: MaterialButton
    private lateinit var mb_callTechnician: MaterialButton
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var userStatus: String = "pending"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val ivHamburger = findViewById<ImageView>(R.id.iv_hamburger)
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        mb_addTicket = findViewById(R.id.mb_addTicket)
        mb_callTechnician = findViewById(R.id.mb_callTechnician)

        // Fetch user status
        checkAccountStatus()

        // Hamburger Menu
        ivHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navigation Drawer Item Clicks
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }

        // Bottom Navigation
        bottom_menu.selectedItemId = R.id.home
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> {
                    startActivity(Intent(this, clientProfil::class.java))
                    true
                }
                R.id.home -> true
                R.id.ticket -> {
                    startActivity(Intent(this, CreateTicketActivity::class.java))
                    true
                }
                else -> false
            }
        }

        mb_addTicket.setOnClickListener {
            startActivity(Intent(this, CreateTicketActivity::class.java))
        }

        mb_callTechnician.setOnClickListener {
            if (userStatus == "approved") {
                // Logic to call
                Toast.makeText(this, "Appel du technicien...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Votre compte doit être validé pour utiliser cette fonction", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAccountStatus() {
        val userId = auth.currentUser?.uid ?: return
        database.reference.child("users").child(userId).child("status").get()
            .addOnSuccessListener { snapshot ->
                userStatus = snapshot.value?.toString() ?: "pending"
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