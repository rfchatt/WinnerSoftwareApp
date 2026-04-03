package com.example.winnersoftwareapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.winnersoftwareapp.Client.clientLogin
import com.example.winnersoftwareapp.Client.clientRegister
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    lateinit var mb_login: MaterialButton
    lateinit var mb_register: MaterialButton
    lateinit var mb_espace_entreprise: MaterialButton
    lateinit var ln_arLanguage: LinearLayout
    lateinit var ln_frLanguage: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mb_login = findViewById(R.id.mb_login)
        mb_register = findViewById(R.id.mb_register)
        mb_espace_entreprise = findViewById(R.id.mb_espace_entreprise)
        ln_arLanguage = findViewById(R.id.ln_arLanguage)
        ln_frLanguage = findViewById(R.id.ln_frLanguage)

        mb_login.setOnClickListener {
            startActivity(Intent(this, clientLogin::class.java))
        }

        mb_register.setOnClickListener {
            startActivity(Intent(this, clientRegister::class.java))
        }

        mb_espace_entreprise.setOnClickListener {

        }

        ln_arLanguage.setOnClickListener {
            setLocale("ar")
        }

        ln_frLanguage.setOnClickListener {
            setLocale("fr")
        }

    }

    private fun setLocale(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

}