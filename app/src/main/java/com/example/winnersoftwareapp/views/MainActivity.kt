package com.example.winnersoftwareapp.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.views.admin.adminHome
import com.example.winnersoftwareapp.views.admin.adminLogin
import com.example.winnersoftwareapp.views.client.clientHome
import com.example.winnersoftwareapp.views.client.clientLogin
import com.example.winnersoftwareapp.views.client.clientRegister
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // التحقق من الدخول التلقائي فقط إذا كان المستخدم "موجوداً فعلياً"
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserPersistence(currentUser.uid)
        }

        setContentView(R.layout.activity_main)

        val mb_login = findViewById<MaterialButton>(R.id.mb_login)
        val mb_register = findViewById<MaterialButton>(R.id.mb_register)
        val mb_espace_entreprise = findViewById<MaterialButton>(R.id.mb_espace_entreprise)
        val tv_arLanguage = findViewById<TextView>(R.id.ln_arLanguage)
        val tv_frLanguage = findViewById<TextView>(R.id.ln_frLanguage)

        mb_login.setOnClickListener {
            startActivity(Intent(this, clientLogin::class.java))
        }

        mb_register.setOnClickListener {
            startActivity(Intent(this, clientRegister::class.java))
        }

        mb_espace_entreprise.setOnClickListener {
            startActivity(Intent(this, adminLogin::class.java))
        }

        tv_arLanguage.setOnClickListener {
            setLocale("ar")
        }

        tv_frLanguage.setOnClickListener {
            setLocale("fr")
        }

    }

    private fun setLocale(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun checkUserPersistence(userId: String) {
        database.reference.child("users").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val role = snapshot.child("role").value.toString()
                val nextActivity = if (role == "admin") adminHome::class.java else clientHome::class.java

                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

}