package com.example.winnersoftwareapp.Client

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class clientRegister : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val btnRegister = findViewById<MaterialButton>(R.id.mb_register)
        val etName = findViewById<TextInputEditText>(R.id.et_client_name)
        val etICE = findViewById<TextInputEditText>(R.id.et_client_ce_number)
        val etPhone = findViewById<TextInputEditText>(R.id.et_client_phone)
        val etEmail = findViewById<TextInputEditText>(R.id.et_client_email)
        val etPass = findViewById<TextInputEditText>(R.id.tiet_client_password)
        val etConfirmPass = findViewById<TextInputEditText>(R.id.tiet_client_password_confirmation)
        val tvAlreadyHaveAccount = findViewById<TextView>(R.id.tv_already_have_account)

        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, clientLogin::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val ice = etICE.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()
            val confirmPassword = etConfirmPass.text.toString().trim()

            if (name.isEmpty() || ice.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userMap = hashMapOf(
                            "uid" to userId,
                            "name" to name,
                            "ice" to ice,
                            "phone" to phone,
                            "email" to email,
                            "role" to "client",
                            "status" to "pending" // الحالة الافتراضية: قيد الانتظار
                        )
                        
                        userId?.let {
                            database.reference.child("users").child(it).setValue(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Compte créé ! En attente de validation par l'entreprise.", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this, clientLogin::class.java))
                                    finish()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erreur: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}