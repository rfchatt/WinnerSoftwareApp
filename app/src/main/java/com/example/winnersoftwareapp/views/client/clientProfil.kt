package com.example.winnersoftwareapp.views.client

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.views.MainActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class clientProfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var tvProfileName: TextView
    private lateinit var tvDisplayEmail: TextView
    private lateinit var tvDisplayPhone: TextView
    private lateinit var tvDisplayIce: TextView
    private lateinit var tvAccountStatus: TextView
    private lateinit var mbEditProfile: MaterialButton
    private lateinit var mbLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_profil)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        initViews()
        loadUserData()
        setupBottomNavigation()

        mbEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        mbLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun initViews() {
        tvProfileName = findViewById(R.id.tv_profile_name)
        tvDisplayEmail = findViewById(R.id.tv_display_email)
        tvDisplayPhone = findViewById(R.id.tv_display_phone)
        tvDisplayIce = findViewById(R.id.tv_display_ice)
        tvAccountStatus = findViewById(R.id.tv_account_status)
        mbEditProfile = findViewById(R.id.mb_edit_profile)
        mbLogout = findViewById(R.id.mb_logout)
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        database.reference.child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: ""
                    val email = snapshot.child("email").value?.toString() ?: ""
                    val phone = snapshot.child("phone").value?.toString() ?: ""
                    val ice = snapshot.child("ice").value?.toString() ?: ""
                    val status = snapshot.child("status").value?.toString() ?: "pending"

                    tvProfileName.text = name
                    tvDisplayEmail.text = email
                    tvDisplayPhone.text = phone
                    tvDisplayIce.text = ice
                    
                    updateStatusUI(status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    private fun updateStatusUI(status: String) {
        when (status) {
            "approved" -> {
                tvAccountStatus.text = "Compte Validé"
                tvAccountStatus.setTextColor(Color.parseColor("#2E7D32")) // Green
            }
            "pending" -> {
                tvAccountStatus.text = "Compte non Validé"
                tvAccountStatus.setTextColor(Color.GRAY) // Gray
            }
            "rejected" -> {
                tvAccountStatus.text = "Compte Refusé"
                tvAccountStatus.setTextColor(Color.parseColor("#ab2229")) // Red
            }
            else -> {
                tvAccountStatus.text = "Statut Inconnu"
                tvAccountStatus.setTextColor(Color.GRAY) // Gray
            }
        }
    }

    private fun showEditProfileDialog() {
        val userId = auth.currentUser?.uid ?: return
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_client_edit_profile, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.et_edit_name)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.et_edit_phone)
        val btnSave = dialogView.findViewById<MaterialButton>(R.id.btn_save_profile)

        etName.setText(tvProfileName.text)
        etPhone.setText(tvDisplayPhone.text)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()

            if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                val updates = mapOf(
                    "name" to newName,
                    "phone" to newPhone
                )
                database.reference.child("users").child(userId).updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setupBottomNavigation() {
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_menu.selectedItemId = R.id.profil
        bottom_menu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profil -> true
                R.id.home -> {
                    startActivity(Intent(this, clientHome::class.java))
                    finish()
                    true
                }
                R.id.ticket -> {
                    startActivity(Intent(this, CreateTicketActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Déconnexion")
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
        builder.setPositiveButton("Oui") { _, _ ->
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}