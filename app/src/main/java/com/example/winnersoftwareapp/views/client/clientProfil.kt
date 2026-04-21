package com.example.winnersoftwareapp.views.client

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.winnersoftwareapp.views.MainActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Modifier le profil")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etName = EditText(this)
        etName.hint = "Nom complet"
        etName.setText(tvProfileName.text)
        layout.addView(etName)

        val etPhone = EditText(this)
        etPhone.hint = "Téléphone"
        etPhone.setText(tvDisplayPhone.text)
        layout.addView(etPhone)

        builder.setView(layout)

        builder.setPositiveButton("Enregistrer") { _, _ ->
            val newName = etName.text.toString().trim()
            val newPhone = etPhone.text.toString().trim()

            if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                val updates = mapOf(
                    "name" to newName,
                    "phone" to newPhone
                )
                database.reference.child("users").child(userId).updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
        builder.show()
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