package com.example.winnersoftwareapp.views.client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class clientDemandes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btnBack: ImageView
    private lateinit var fabAdd: ExtendedFloatingActionButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_demandes)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        setupRecyclerView()
        fetchUserTickets()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateTicketActivity::class.java))
        }

        // Bottom Navigation
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> {
                    startActivity(Intent(this, clientProfil::class.java))
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this, clientHome::class.java))
                    true
                }
                R.id.ticket -> {
                    startActivity(Intent(this, CreateTicketActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewDemandes)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnBack = findViewById(R.id.btn_back)
        fabAdd = findViewById(R.id.fab_add_ticket)
    }

    private fun setupRecyclerView() {
        adapter = TicketAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun fetchUserTickets() {
        val userId = auth.currentUser?.uid ?: return
        
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
                    Toast.makeText(this@clientDemandes, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            })
    }
}