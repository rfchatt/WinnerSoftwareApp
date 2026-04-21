package com.example.winnersoftwareapp.views.client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.views.MainActivity
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class clientHome : AppCompatActivity() {
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    lateinit var tvUsername: TextView
    private lateinit var mb_addTicket: MaterialButton
    private lateinit var mb_callTechnician: MaterialButton
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var userStatus: String = "pending"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        setupRecyclerView()
        fetchUserData()
        fetchUserTickets()

        // Hamburger Menu
        findViewById<ImageView>(R.id.iv_hamburger).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Navigation Drawer
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
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
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
                Toast.makeText(this, "Appel du technicien...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Votre compte doit être validé pour utiliser cette fonction", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        tvUsername = findViewById(R.id.tv_user_name)
        mb_addTicket = findViewById(R.id.mb_addTicket)
        mb_callTechnician = findViewById(R.id.mb_callTechnician)
        
        recyclerView = findViewById(R.id.recyclerViewDemandes)
        progressBar = findViewById(R.id.pb_home_demandes)
        tvEmpty = findViewById(R.id.tvEmptyDemandes)
    }

    private fun setupRecyclerView() {
        adapter = TicketAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid ?: return
        database.reference.child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userStatus = snapshot.child("status").value?.toString() ?: "pending"
                    tvUsername.text = snapshot.child("name").value?.toString() ?: "Client"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchUserTickets() {
        val userId = auth.currentUser?.uid ?: return
        
        // Show progress bar initially
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        database.reference.child("tickets")
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tickets = mutableListOf<Ticket>()
                    for (ticketSnapshot in snapshot.children) {
                        val ticket = ticketSnapshot.getValue(Ticket::class.java)
                        ticket?.let { tickets.add(it) }
                    }

                    // Sort by timestamp (newest first)
                    tickets.sortByDescending { it.timestamp }

                    progressBar.visibility = View.GONE
                    if (tickets.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.updateData(tickets)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@clientHome, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}