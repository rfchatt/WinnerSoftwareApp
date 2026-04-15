package com.example.winnersoftwareapp.Client

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TicketFB(
    val id: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val serviceType: String? = null,
    val priority: String? = null,
    val status: String = "En attente",
    val timestamp: Long? = null
)