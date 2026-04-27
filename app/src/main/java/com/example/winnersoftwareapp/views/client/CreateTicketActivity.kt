package com.example.winnersoftwareapp.views.client

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Service
import com.example.winnersoftwareapp.models.Ticket
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CreateTicketActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var actvServiceType: AutoCompleteTextView
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnEmergencyCall: MaterialButton
    private lateinit var btnBack: ImageView
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    
    private val servicesList = mutableListOf<Service>()
    private var selectedServicePhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_report)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        loadServicesFromFirebase()
        
        btnSubmit.setOnClickListener { saveTicketToFirebase() }
        btnBack.setOnClickListener { onBackPressed() }

        // تفعيل زر الاتصال بالتقني
        btnEmergencyCall.setOnClickListener {
            makeCall()
        }
    }

    private fun initViews() {
        etTitle = findViewById(R.id.tiet_title)
        etDescription = findViewById(R.id.tiet_desc)
        actvServiceType = findViewById(R.id.til_cat)
        btnSubmit = findViewById(R.id.btn_send)
        btnEmergencyCall = findViewById(R.id.btn_emergency_fab)
        btnBack = findViewById(R.id.btn_back_nav)
    }

    private fun loadServicesFromFirebase() {
        database.reference.child("services").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                servicesList.clear()
                val names = mutableListOf<String>()
                
                for (child in snapshot.children) {
                    val service = child.getValue(Service::class.java)
                    service?.let {
                        servicesList.add(it)
                        it.name?.let { name -> names.add(name) }
                    }
                }
                
                setupDropdown(names)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupDropdown(names: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, names)
        actvServiceType.setAdapter(adapter)
        
        actvServiceType.setOnItemClickListener { _, _, position, _ ->
            val selectedName = names[position]
            val selectedService = servicesList.find { it.name == selectedName }
            selectedServicePhone = selectedService?.phone
            
            if (selectedServicePhone != null) {
                btnEmergencyCall.text = "Appeler Technicien ($selectedName)"
            }
        }
    }

    private fun makeCall() {
        if (!selectedServicePhone.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$selectedServicePhone")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Veuillez d'abord choisir un type de service", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTicketToFirebase() {
        val title = etTitle.text.toString().trim()
        val desc = etDescription.text.toString().trim()
        val serviceType = actvServiceType.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (title.isEmpty() || desc.isEmpty() || serviceType.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId != null) {
            btnSubmit.isEnabled = false
            val ticketId = database.reference.child("tickets").push().key
            val ticket = Ticket(
                id = ticketId,
                userId = userId,
                title = title,
                description = desc,
                serviceType = serviceType,
                timestamp = System.currentTimeMillis(),
                status = "En attente"
            )

            ticketId?.let {
                database.reference.child("tickets").child(it).setValue(ticket)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Ticket envoyé !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }
        }
    }
}