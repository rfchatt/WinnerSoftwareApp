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
import android.widget.Toast
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
import java.util.*

class adminHome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var ivHamburger: ImageView
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvClientsTt: TextView
    
    private lateinit var tvStatTodayTotal: TextView
    private lateinit var tvStatPending: TextView
    private lateinit var tvStatInProgress: TextView
    private lateinit var tvStatCompleted: TextView

    private lateinit var adapter: AdminUserAdapter
    private val pendingUsers = mutableListOf<User>()

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        database = FirebaseDatabase.getInstance().reference

        initViews()
        setupRecyclerView()
        loadPendingUsers()
        loadTodayStats()
        loadApprovedClientsCount()
        setupNavigation()
        
        // تشغيل عملية تنظيف الطلبات القديمة (أقدم من سنة)
        autoCleanupOldTickets()

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

    private fun autoCleanupOldTickets() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1) // العودة سنة إلى الوراء
        val oneYearAgoThreshold = calendar.timeInMillis

        database.child("tickets").orderByChild("timestamp").endAt(oneYearAgoThreshold.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val count = snapshot.childrenCount
                        for (ticketSnapshot in snapshot.children) {
                            ticketSnapshot.ref.removeValue()
                        }
                        // إشعار للأدمن (اختياري)
                        // Toast.makeText(this@adminHome, "$count anciens tickets ont été archivés/supprimés", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
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
        progressBar = findViewById(R.id.pb_admin_loading)
        tvEmpty = findViewById(R.id.tv_admin_empty_demandes)
        tvClientsTt = findViewById(R.id.tv_clients_tt)
        
        tvStatTodayTotal = findViewById(R.id.tv_stat_today_total)
        tvStatPending = findViewById(R.id.tv_stat_pending)
        tvStatInProgress = findViewById(R.id.tv_stat_in_progress)
        tvStatCompleted = findViewById(R.id.tv_stat_completed)

        ivHamburger.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(pendingUsers)
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = adapter
    }

    private fun loadPendingUsers() {
        progressBar.visibility = View.VISIBLE
        database.child("users").orderByChild("status").equalTo("pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pendingUsers.clear()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null && user.role == "client") pendingUsers.add(user)
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
                override fun onCancelled(error: DatabaseError) { progressBar.visibility = View.GONE }
            })
    }

    private fun loadTodayStats() {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        database.child("tickets").orderByChild("timestamp").startAt(startOfDay.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var total = 0
                    var pending = 0
                    var inProgress = 0
                    var completed = 0

                    for (ticketSnapshot in snapshot.children) {
                        val status = ticketSnapshot.child("status").value.toString()
                        total++
                        when (status) {
                            "En attente" -> pending++
                            "En cours" -> inProgress++
                            "Terminé" -> completed++
                        }
                    }

                    tvStatTodayTotal.text = total.toString()
                    tvStatPending.text = pending.toString()
                    tvStatInProgress.text = inProgress.toString()
                    tvStatCompleted.text = completed.toString()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadApprovedClientsCount() {
        database.child("users").orderByChild("status").equalTo("approved")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var count = 0
                    for (userSnapshot in snapshot.children) {
                        if (userSnapshot.child("role").value == "client") count++
                    }
                    tvClientsTt.text = count.toString()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupNavigation() {
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.admin_home
        
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_home -> true
                R.id.admin_requests -> {
                    startActivity(Intent(this, AdminTicketsActivity::class.java))
                    true
                }
                R.id.admin_clients -> {
                    startActivity(Intent(this, AdminClientsActivity::class.java))
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
                R.id.nav_dashboard -> { drawerLayout.closeDrawer(GravityCompat.START); true }
                R.id.nav_clients -> { startActivity(Intent(this, AdminClientsActivity::class.java)); true }
                R.id.nav_tickets -> { startActivity(Intent(this, AdminTicketsActivity::class.java)); true }
                R.id.nav_services -> { startActivity(Intent(this, ManageServicesActivity::class.java)); true }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    finish()
                    true
                }
                else -> { drawerLayout.closeDrawer(GravityCompat.START); true }
            }
        }
    }
}
