package com.example.winnersoftwareapp.Admin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.google.firebase.database.*

class adminClientDetails : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var rvTickets: RecyclerView
    private lateinit var database: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_detail)

        userId = intent.getStringExtra("userId")
        database = FirebaseDatabase.getInstance().reference

        initViews()
        loadClientInfo()
        
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initViews() {
        // Updated IDs to match activity_admin_user_detail.xml
        tvName = findViewById(R.id.tv_display_name)
        tvEmail = findViewById(R.id.tv_display_email)
        tvPhone = findViewById(R.id.tv_display_phone)
        rvTickets = findViewById(R.id.rv_client_tickets)
    }

    private fun loadClientInfo() {
        userId?.let { uid ->
            database.child("users").child(uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val phone = snapshot.child("phone").value.toString()

                    tvName.text = name
                    tvEmail.text = email
                    tvPhone.text = phone
                }
            }
        }
    }
}