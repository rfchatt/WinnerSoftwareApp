package com.example.winnersoftwareapp.Client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TicketDao {
    @Insert
    suspend fun insert(ticket: Ticket)

    @Query("SELECT * FROM tickets ORDER BY timestamp DESC")
    suspend fun getAllTickets(): List<Ticket>

    @Query("SELECT * FROM tickets WHERE id = :ticketId")
    suspend fun getTicketById(ticketId: Int): Ticket?
}