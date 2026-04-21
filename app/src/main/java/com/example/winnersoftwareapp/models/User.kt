package com.example.winnersoftwareapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val ice: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val status: String? = null
)