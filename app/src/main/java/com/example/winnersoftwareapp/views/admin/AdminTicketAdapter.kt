package com.example.winnersoftwareapp.views.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.example.winnersoftwareapp.models.User
import com.google.firebase.database.FirebaseDatabase

class AdminTicketAdapter(
    private var ticketList: List<Ticket>,
    private var usersMap: Map<String, User> = emptyMap()
) : RecyclerView.Adapter<AdminTicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvClientICE: TextView = view.findViewById(R.id.tvClientICE)
        val tvClientPhone: TextView = view.findViewById(R.id.tvClientPhone)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvProductType: TextView = view.findViewById(R.id.tvProductType)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_demande, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        val user = usersMap[ticket.userId]

        holder.tvTitle.text = ticket.title ?: "Sans titre"
        holder.tvDescription.text = ticket.description ?: "Pas de description"
        holder.tvProductType.text = ticket.serviceType ?: "N/A"
        
        holder.tvClientName.text = user?.name ?: "Client inconnu"
        holder.tvClientICE.text = "ICE: ${user?.ice ?: "N/A"}"
        holder.tvClientPhone.text = user?.phone ?: "N/A"

        holder.ivDelete.setOnClickListener {
            ticket.id?.let { ticketId ->
                FirebaseDatabase.getInstance().reference.child("tickets").child(ticketId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Demande supprimée", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(holder.itemView.context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun getItemCount(): Int = ticketList.size

    fun updateData(newTickets: List<Ticket>, newUsersMap: Map<String, User>) {
        ticketList = newTickets
        usersMap = newUsersMap
        notifyDataSetChanged()
    }
}
