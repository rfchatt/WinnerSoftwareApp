package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.example.winnersoftwareapp.models.User
import com.example.winnersoftwareapp.views.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminTicketsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var ivHamburger: ImageView
    private lateinit var rvTickets: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: AdminTicketAdapter
    private val ticketList = mutableListOf<Ticket>()
    private val usersMap = mutableMapOf<String, User>()

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_tickets)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupRecyclerView()
        loadTicketsWithUserDetails()
        setupNavigation()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        ivHamburger = findViewById(R.id.iv_hamburger)
        rvTickets = findViewById(R.id.rv_admin_tickets)
        progressBar = findViewById(R.id.pb_loading)
        tvEmpty = findViewById(R.id.tv_empty)
        
        ivHamburger.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun setupRecyclerView() {
        adapter = AdminTicketAdapter(ticketList, usersMap)
        rvTickets.layoutManager = LinearLayoutManager(this)
        rvTickets.adapter = adapter
    }

    private fun loadTicketsWithUserDetails() {
        progressBar.visibility = View.VISIBLE
        
        database.child("tickets").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                for (ticketSnapshot in snapshot.children) {
                    val ticket = ticketSnapshot.getValue(Ticket::class.java)
                    ticket?.let { ticketList.add(it) }
                }

                if (ticketList.isEmpty()) {
                    progressBar.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                    rvTickets.visibility = View.GONE
                } else {
                    loadUsersInfo()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun loadUsersInfo() {
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersMap.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.uid?.let { usersMap[it] = user }
                }
                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun updateUI() {
        progressBar.visibility = View.GONE
        tvEmpty.visibility = if (ticketList.isEmpty()) View.VISIBLE else View.GONE
        rvTickets.visibility = if (ticketList.isEmpty()) View.GONE else View.VISIBLE
        adapter.updateData(ticketList, usersMap)
    }

    private fun setupNavigation() {
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.admin_requests
        
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_home -> {
                    startActivity(Intent(this, adminHome::class.java))
                    finish()
                    true
                }
                R.id.admin_requests -> true
                R.id.admin_clients -> {
                    // Navigate to clients list if needed
                    true
                }
                else -> false
            }
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    finish()
                    true
                }
                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
            }
        }
    }
}