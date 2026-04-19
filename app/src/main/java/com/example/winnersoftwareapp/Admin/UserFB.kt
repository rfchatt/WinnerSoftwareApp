package com.example.winnersoftwareapp.Admin

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserFB(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val ice: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val status: String? = null
)