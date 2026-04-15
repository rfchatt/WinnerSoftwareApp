package com.example.winnersoftwareapp.Client

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val serviceType: String,
    val product: String,
    val priority: String,
    val imagePath: String? = null,
    val audioPath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)