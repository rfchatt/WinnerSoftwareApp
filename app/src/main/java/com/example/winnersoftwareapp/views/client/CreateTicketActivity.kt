package com.example.winnersoftwareapp.views.client

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateTicketActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var actvServiceType: AutoCompleteTextView
    private var tvAvatarInitial: TextView? = null
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_report)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        loadUserAvatar()
        setupDropdowns()
        
        btnSubmit.setOnClickListener {
            saveTicketToFirebase()
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initViews() {
        etTitle = findViewById(R.id.tiet_title)
        etDescription = findViewById(R.id.tiet_desc)
        actvServiceType = findViewById(R.id.til_cat)
        btnSubmit = findViewById(R.id.btn_send)
        btnBack = findViewById(R.id.btn_back_nav)
        // tvAvatarInitial is not in activity_client_report.xml, so we find it optionally or remove it
        // tvAvatarInitial = findViewById(R.id.tv_avatar_initial) 
    }

    private fun loadUserAvatar() {
        val userId = auth.currentUser?.uid
        if (userId != null && tvAvatarInitial != null) {
            database.reference.child("users").child(userId).child("name").get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.value?.toString() ?: ""
                    if (name.isNotEmpty()) {
                        tvAvatarInitial?.text = name[0].uppercase().toString()
                    }
                }
        }
    }

    private fun setupDropdowns() {
        val types = arrayOf("Site", "Materiel", "Logiciel", "Autre")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, types)
        actvServiceType.setAdapter(adapter)
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
            btnSubmit.text = "Envoi en cours..."

            val ticketId = database.reference.child("tickets").push().key
            val ticket = Ticket(
                id = ticketId,
                userId = userId,
                title = title,
                description = desc,
                serviceType = serviceType,
                priority = "Normale",
                timestamp = System.currentTimeMillis(),
                status = "En attente"
            )

            ticketId?.let {
                database.reference.child("tickets").child(it).setValue(ticket)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Ticket envoyé avec succès !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Envoyer la demande"
                        Log.e("FirebaseError", "Error: ${e.message}")
                        Toast.makeText(this, "Erreur d'envoi", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Session expirée, veuillez vous reconnecter", Toast.LENGTH_SHORT).show()
        }
    }
}