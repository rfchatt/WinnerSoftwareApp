package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.views.MainActivity
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
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
    private lateinit var rvAdminTickets: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var pbTicketContent: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvClientsTt: TextView
    private lateinit var adapter: AdminUserAdapter
    private lateinit var ticketAdapter: AdminTicketAdapter
    private val pendingUsers = mutableListOf<User>()
    private val ticketList = mutableListOf<Ticket>()
    private val ticketUsersMap = mutableMapOf<String, User>()

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupRecyclerViews()
        loadPendingUsers()
        loadTicketsWithUserDetails()
        loadApprovedClientsCount()
        setupNavigation()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    showExitDialog()
                }
            }
        })

    }

    private fun showExitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.alertd_closing, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            finishAffinity()
        }
        dialogView.findViewById<TextView>(R.id.btnNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        ivHamburger = findViewById(R.id.iv_hamburger)
        rvUsers = findViewById(R.id.rv_admin_demandes)
        rvAdminTickets = findViewById(R.id.rv_admin_tickets)
        progressBar = findViewById(R.id.pb_admin_loading)
        pbTicketContent = findViewById(R.id.pb_ticket_content)
        tvEmpty = findViewById(R.id.tv_admin_empty_demandes)
        tvClientsTt = findViewById(R.id.tv_clients_tt)
        ivHamburger.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun setupRecyclerViews() {
        adapter = AdminUserAdapter(pendingUsers)
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter

        ticketAdapter = AdminTicketAdapter(ticketList, ticketUsersMap)
        rvAdminTickets.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvAdminTickets.adapter = ticketAdapter
    }

    private fun loadPendingUsers() {
        progressBar.visibility = View.VISIBLE
        
        database.child("users").orderByChild("status").equalTo("pending")
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

    private fun loadTicketsWithUserDetails() {
        pbTicketContent.visibility = View.VISIBLE
        
        database.child("tickets").limitToLast(10).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                for (ticketSnapshot in snapshot.children) {
                    val ticket = ticketSnapshot.getValue(Ticket::class.java)
                    ticket?.let { ticketList.add(it) }
                }
                ticketList.reverse()

                if (ticketList.isEmpty()) {
                    pbTicketContent.visibility = View.GONE
                    rvAdminTickets.visibility = View.GONE
                } else {
                    loadTicketUsersInfo()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                pbTicketContent.visibility = View.GONE
            }
        })
    }

    private fun loadTicketUsersInfo() {
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketUsersMap.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.uid?.let { ticketUsersMap[it] = user }
                }
                pbTicketContent.visibility = View.GONE
                rvAdminTickets.visibility = View.VISIBLE
                ticketAdapter.updateData(ticketList, ticketUsersMap)
            }

            override fun onCancelled(error: DatabaseError) {
                pbTicketContent.visibility = View.GONE
            }
        })
    }

    private fun loadApprovedClientsCount() {
        database.child("users").orderByChild("status").equalTo("approved")
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
        
        // Handle Bottom Navigation clicks
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_home -> true
                R.id.admin_requests -> {
                    startActivity(Intent(this@adminHome, AdminTicketsActivity::class.java))
                    true
                }
                R.id.admin_clients -> {
                    startActivity(Intent(this@adminHome, AdminClientsActivity::class.java))
                    true
                }
                else -> false
            }
        }


        val logoutItem = navView.menu.findItem(R.id.nav_logout)
        val s = SpannableString(logoutItem.title)
        s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
        logoutItem.title = s
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_clients -> {
                    startActivity(Intent(this@adminHome, AdminClientsActivity::class.java))
                    true
                }
                R.id.nav_tickets -> {
                    startActivity(Intent(this@adminHome, AdminTicketsActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@adminHome, MainActivity::class.java).apply {
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
