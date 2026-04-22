package com.example.winnersoftwareapp.views.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.example.winnersoftwareapp.models.User
import com.google.firebase.database.*

class adminUserDetails : AppCompatActivity() {

    private lateinit var btn_back_nav: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var rvTickets: RecyclerView
    private lateinit var database: DatabaseReference
    private var userId: String? = null
    
    private lateinit var adapter: AdminTicketAdapter
    private val ticketList = mutableListOf<Ticket>()
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_client_detail)

        userId = intent.getStringExtra("userId")
        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupRecyclerView()
        loadClientInfo()

        btn_back_nav.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        tvName = findViewById(R.id.tv_display_name)
        tvEmail = findViewById(R.id.tv_display_email)
        tvPhone = findViewById(R.id.tv_display_phone)
        rvTickets = findViewById(R.id.rv_client_tickets)
        btn_back_nav = findViewById(R.id.btn_back_nav)
    }

    private fun setupRecyclerView() {
        adapter = AdminTicketAdapter(ticketList)
        rvTickets.layoutManager = LinearLayoutManager(this)
        rvTickets.adapter = adapter
    }

    private fun loadClientInfo() {
        userId?.let { uid ->
            database.child("users").child(uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(User::class.java)
                    
                    currentUser?.let { user ->
                        tvName.text = user.name ?: "N/A"
                        tvEmail.text = user.email ?: "N/A"
                        tvPhone.text = user.phone ?: "N/A"
                        
                        loadUserTickets(uid)
                    }
                }
            }
        }
    }

    private fun loadUserTickets(uid: String) {
        database.child("tickets").orderByChild("userId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ticketList.clear()
                    for (ticketSnapshot in snapshot.children) {
                        val ticket = ticketSnapshot.getValue(Ticket::class.java)
                        ticket?.let { ticketList.add(it) }
                    }
                    
                    val usersMap = mutableMapOf<String, User>()
                    currentUser?.let { user ->
                        user.uid?.let { usersMap[it] = user }
                    }
                    
                    adapter.updateData(ticketList, usersMap)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}