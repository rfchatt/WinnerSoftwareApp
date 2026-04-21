package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.winnersoftwareapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class adminLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val etEmail = findViewById<TextInputEditText>(R.id.edt_admin_email)
        val etPassword = findViewById<TextInputEditText>(R.id.edt_admin_password)
        val btnLogin = findViewById<MaterialButton>(R.id.mb_admin_login)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. تسجيل الدخول عبر Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        // 2. التحقق من "الدور" في قاعدة البيانات
                        userId?.let { verifyAdminRole(it) }
                    } else {
                        Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun verifyAdminRole(uid: String) {
        database.reference.child("users").child(uid).get().addOnSuccessListener { snapshot ->
            val role = snapshot.child("role").value.toString()
            if (role == "admin") {
                // دخول ناجح للأدمن
                startActivity(Intent(this, adminHome::class.java))
                finish()
            } else {
                // محاولة دخول غير مصرح بها (عميل يحاول دخول لوحة الأدمن)
                auth.signOut()
                Toast.makeText(this, "Accès refusé : Vous n'êtes pas un administrateur", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            auth.signOut()
            Toast.makeText(this, "Erreur دو سيرفر", Toast.LENGTH_SHORT).show()
        }
    }
}