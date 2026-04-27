package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class AdminClientsActivity : AppCompatActivity() {

    private lateinit var rvClients: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btn_back_nav: ImageView
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var etSearchIce: TextInputEditText
    private lateinit var adapter: AdminUserAdapter
    
    private val allClientsList = mutableListOf<User>()
    private var currentFilterStatus: String = "all"
    private var currentSearchQuery: String = ""

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_clients)

        database = FirebaseDatabase.getInstance().reference.child("users")

        initViews()
        setupRecyclerView()
        loadAllClients()
        setupNavigation()
        setupFilters()
        setupSearch()
    }

    private fun initViews() {
        rvClients = findViewById(R.id.rv_admin_clients)
        progressBar = findViewById(R.id.pb_loading)
        tvEmpty = findViewById(R.id.tv_empty)
        chipGroupFilter = findViewById(R.id.chip_group_filter)
        etSearchIce = findViewById(R.id.et_search_ice)
        btn_back_nav = findViewById(R.id.btn_back_nav)
        
        btn_back_nav.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminUserAdapter(mutableListOf())
        rvClients.layoutManager = LinearLayoutManager(this)
        rvClients.adapter = adapter
    }

    private fun setupFilters() {
        chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            currentFilterStatus = when (checkedIds.firstOrNull()) {
                R.id.chip_pending -> "pending"
                R.id.chip_approved -> "approved"
                R.id.chip_rejected -> "rejected"
                else -> "all"
            }
            applyFilterAndSearch()
        }
    }

    private fun setupSearch() {
        etSearchIce.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAllClients() {
        progressBar.visibility = View.VISIBLE
        
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allClientsList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.role == "client") {
                        allClientsList.add(user)
                    }
                }
                applyFilterAndSearch()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun applyFilterAndSearch() {
        var filteredList = if (currentFilterStatus == "all") {
            allClientsList
        } else {
            allClientsList.filter { it.status == currentFilterStatus }
        }

        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { 
                it.ice?.contains(currentSearchQuery, ignoreCase = true) == true ||
                it.name?.contains(currentSearchQuery, ignoreCase = true) == true
            }
        }

        adapter.updateData(filteredList)
        
        if (filteredList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvClients.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvClients.visibility = View.VISIBLE
        }
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
