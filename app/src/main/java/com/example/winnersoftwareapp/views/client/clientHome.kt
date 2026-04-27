package com.example.winnersoftwareapp.views.client

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
    private lateinit var tvHeaderUsername: TextView
    private lateinit var tvHeaderUserICE: TextView
    private lateinit var mb_addTicket: MaterialButton
    private lateinit var mb_callTechnician: MaterialButton
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var userStatus: String = "pending"
    private var companyGeneralPhone: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        setupRecyclerView()
        fetchUserData()
        fetchUserTickets()
        loadCompanyPhone()

        findViewById<ImageView>(R.id.iv_hamburger).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupNavigationListeners()

        mb_addTicket.setOnClickListener {
            startActivity(Intent(this, CreateTicketActivity::class.java))
        }

        mb_callTechnician.setOnClickListener {
            handleCallRequest()
        }

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

    private fun loadCompanyPhone() {
        database.reference.child("services").limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        companyGeneralPhone = child.child("phone").value?.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun handleCallRequest() {
        if (userStatus != "approved") {
            Toast.makeText(this, "Votre compte doit être validé", Toast.LENGTH_LONG).show()
            return
        }

        if (!companyGeneralPhone.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$companyGeneralPhone")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Service d'appel indisponible pour le moment", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigationListeners() {
        val logoutItem = navView.menu.findItem(R.id.nav_logout)
        val s = SpannableString(logoutItem.title)
        s.setSpan(ForegroundColorSpan(Color.RED), 0, s.length, 0)
        logoutItem.title = s
        
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_my_demandes -> startActivity(Intent(this, clientDemandes::class.java))
                R.id.nav_profile -> startActivity(Intent(this, clientProfil::class.java))
                R.id.nav_logout -> performLogout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.home
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> { startActivity(Intent(this, clientProfil::class.java)); true }
                R.id.home -> true
                R.id.ticket -> { startActivity(Intent(this, CreateTicketActivity::class.java)); true }
                else -> false
            }
        }
    }

    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
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
        tvUsername = findViewById(R.id.tv_user_name)
        val headerView = navView.getHeaderView(0)
        tvHeaderUsername = headerView.findViewById(R.id.tv_user_name)
        tvHeaderUserICE = headerView.findViewById(R.id.tv_user_ice)

        headerView.setOnClickListener {
            startActivity(Intent(this, clientProfil::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

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
                    val userName = snapshot.child("name").value?.toString() ?: "Client"
                    val userIce = snapshot.child("ice").value?.toString() ?: "..."
                    tvUsername.text = userName
                    tvHeaderUsername.text = userName
                    tvHeaderUserICE.text = "ICE : $userIce"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchUserTickets() {
        val userId = auth.currentUser?.uid ?: return
        progressBar.visibility = View.VISIBLE
        database.reference.child("tickets").orderByChild("userId").equalTo(userId)
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
                }
            })
    }
}
