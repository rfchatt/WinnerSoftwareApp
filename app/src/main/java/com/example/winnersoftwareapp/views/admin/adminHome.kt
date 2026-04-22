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
import com.example.winnersoftwareapp.views.MainActivity
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class adminHome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var ivHamburger: ImageView
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvClientsTt: TextView
    private lateinit var adapter: AdminUserAdapter
    private val pendingUsers = mutableListOf<User>()

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        database = FirebaseDatabase.getInstance().reference.child("users")

        initViews()
        setupRecyclerView()
        loadPendingUsers()
        loadApprovedClientsCount()
        setupNavigation()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        ivHamburger = findViewById(R.id.iv_hamburger)
        rvUsers = findViewById(R.id.rv_admin_demandes)
        progressBar = findViewById(R.id.pb_admin_loading)
        tvEmpty = findViewById(R.id.tv_admin_empty_demandes)
        tvClientsTt = findViewById(R.id.tv_clients_tt)
        ivHamburger.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(pendingUsers)
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
    }

    private fun loadPendingUsers() {
        progressBar.visibility = View.VISIBLE
        
        database.orderByChild("status").equalTo("pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pendingUsers.clear()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) pendingUsers.add(user)
                    }
                    
                    progressBar.visibility = View.GONE
                    if (pendingUsers.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        rvUsers.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvUsers.visibility = View.VISIBLE
                        adapter.updateData(pendingUsers)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun loadApprovedClientsCount() {
        database.orderByChild("status").equalTo("approved")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount
                    tvClientsTt.text = count.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    tvClientsTt.text = "0"
                }
            })
    }

    private fun setupNavigation() {
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.admin_home
        
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tickets -> {
                    startActivity(Intent(this, AdminTicketsActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
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