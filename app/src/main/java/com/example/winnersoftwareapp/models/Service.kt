package com.example.winnersoftwareapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Service(
    val id: String? = null,
    val name: String? = null,
    val phone: String? = null
)