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
import com.example.winnersoftwareapp.models.User
import com.example.winnersoftwareapp.views.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminClientsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var rvClients: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btn_back_nav: ImageView
    private lateinit var adapter: ApprovedClientsAdapter
    private val clientList = mutableListOf<User>()

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_clients)

        database = FirebaseDatabase.getInstance().reference.child("users")

        initViews()
        setupRecyclerView()
        loadApprovedClients()
        setupNavigation()
    }


    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        rvClients = findViewById(R.id.rv_admin_clients)
        progressBar = findViewById(R.id.pb_loading)
        tvEmpty = findViewById(R.id.tv_empty)

        btn_back_nav = findViewById(R.id.btn_back_nav)
        btn_back_nav.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ApprovedClientsAdapter(clientList)
        rvClients.layoutManager = LinearLayoutManager(this)
        rvClients.adapter = adapter
    }

    private fun loadApprovedClients() {
        progressBar.visibility = View.VISIBLE
        
        database.orderByChild("status").equalTo("approved")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    clientList.clear()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        // Only add if it's a client (optional: check role)
                        if (user != null && user.role != "admin") {
                            clientList.add(user)
                        }
                    }
                    
                    progressBar.visibility = View.GONE
                    if (clientList.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvClients.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvClients.visibility = View.VISIBLE
                        adapter.updateData(clientList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun setupNavigation() {
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.admin_clients
        
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_home -> {
                    startActivity(Intent(this, adminHome::class.java))
                    finish()
                    true
                }
                R.id.admin_requests -> {
                    startActivity(Intent(this, AdminTicketsActivity::class.java))
                    finish()
                    true
                }
                R.id.admin_clients -> true
                else -> false
            }
        }
    }
}
