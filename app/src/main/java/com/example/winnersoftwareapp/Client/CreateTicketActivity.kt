package com.example.winnersoftwareapp.Client

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateTicketActivity : AppCompatActivity() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var actvServiceType: AutoCompleteTextView
    private lateinit var tvAvatarInitial: TextView
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnBack: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_issue)

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
        tvAvatarInitial = findViewById(R.id.tv_avatar_initial)
        btnSubmit = findViewById(R.id.btn_send)
        btnBack = findViewById(R.id.btn_back_nav)
    }

    private fun loadUserAvatar() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.reference.child("users").child(userId).child("name").get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.value?.toString() ?: ""
                    if (name.isNotEmpty()) {
                        tvAvatarInitial.text = name[0].uppercase().toString()
                    }
                }
        }
    }

    private fun setupDropdowns() {
        val types = arrayOf("Software", "Hardware")
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
            val ticket = TicketFB(
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