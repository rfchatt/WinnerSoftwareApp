package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.Admin.adminHome
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class clientLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Correcting IDs to match activity_client_login.xml
        val etICE = findViewById<TextInputEditText>(R.id.edt_ce_number) 
        val etPassword = findViewById<TextInputEditText>(R.id.edt_client_password)
        val btnLogin = findViewById<MaterialButton>(R.id.mb_client_login)
        val tvRegister = findViewById<TextView>(R.id.tv_create_new_account)
        val tvForgot = findViewById<TextView>(R.id.tv_forgot_pasword)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, clientRegister::class.java))
        }

        tvForgot.setOnClickListener {
            Toast.makeText(this, "Fonctionnalité bientôt disponible", Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            val iceInput = etICE.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (iceInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir le numéro ICE et le mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Step 1: Search for Email by ICE
            database.reference.child("users")
                .orderByChild("ice")
                .equalTo(iceInput)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val email = userSnapshot.child("email").value.toString()

                        // Step 2: Login with Email & Password
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    checkUserRole()
                                } else {
                                    Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Numéro ICE non trouvé", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginError", "Error: ${e.message}")
                    Toast.makeText(this, "Erreur serveur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkUserRole() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.reference.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                val role = snapshot.child("role").value.toString()
                if (role == "admin") {
                    startActivity(Intent(this, adminHome::class.java))
                } else {
                    startActivity(Intent(this, clientHome::class.java))
                }
                finish()
            }
        }
    }
}