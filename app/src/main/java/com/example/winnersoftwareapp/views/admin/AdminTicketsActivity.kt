package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.example.winnersoftwareapp.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*

class AdminTicketsActivity : AppCompatActivity() {

    private lateinit var rvTickets: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btn_back_nav: ImageView
    private lateinit var chipGroupStatus: ChipGroup
    private lateinit var adapter: AdminTicketAdapter
    
    private val allTicketsList = mutableListOf<Ticket>()
    private val usersMap = mutableMapOf<String, User>()
    private var currentFilterStatus: String = "all"

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_tickets)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupRecyclerView()
        loadTicketsAndUsers()
        setupNavigation()
        setupFilters()
    }

    private fun initViews() {
        rvTickets = findViewById(R.id.rv_admin_tickets)
        progressBar = findViewById(R.id.pb_loading)
        tvEmpty = findViewById(R.id.tv_empty)
        chipGroupStatus = findViewById(R.id.chip_group_status)
        btn_back_nav = findViewById(R.id.btn_back_nav)
        
        btn_back_nav.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AdminTicketAdapter(mutableListOf(), usersMap)
        rvTickets.layoutManager = LinearLayoutManager(this)
        rvTickets.adapter = adapter
    }

    private fun setupFilters() {
        chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterStatus = when (checkedIds.firstOrNull()) {
                R.id.chip_pending -> "En attente"
                R.id.chip_in_progress -> "En cours"
                R.id.chip_completed -> "Terminé"
                else -> "all"
            }
            applyFilter()
        }
    }

    private fun loadTicketsAndUsers() {
        progressBar.visibility = View.VISIBLE
        
        // 1. Load all users to populate the map
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                usersMap.clear()
                for (snapshot in userSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.uid?.let { usersMap[it] = user }
                }
                
                // 2. Load all tickets
                loadTickets()
            }
            override fun onCancelled(error: DatabaseError) { progressBar.visibility = View.GONE }
        })
    }

    private fun loadTickets() {
        database.child("tickets").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allTicketsList.clear()
                for (ticketSnapshot in snapshot.children) {
                    val ticket = ticketSnapshot.getValue(Ticket::class.java)
                    ticket?.let { allTicketsList.add(it) }
                }
                
                // Sort: Newest first
                allTicketsList.sortByDescending { it.timestamp }
                
                applyFilter()
                progressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) { progressBar.visibility = View.GONE }
        })
    }

    private fun applyFilter() {
        val filteredList = if (currentFilterStatus == "all") {
            allTicketsList
        } else {
            allTicketsList.filter { it.status == currentFilterStatus }
        }

        adapter.updateData(filteredList, usersMap)
        
        if (filteredList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvTickets.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvTickets.visibility = View.VISIBLE
        }
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
                    startActivity(Intent(this, AdminClientsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
