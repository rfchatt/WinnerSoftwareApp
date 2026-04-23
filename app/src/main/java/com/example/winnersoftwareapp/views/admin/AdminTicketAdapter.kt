package com.example.winnersoftwareapp.views.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
        val tvClientInitial: TextView = view.findViewById(R.id.ivClientN)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvClientICE: TextView = view.findViewById(R.id.tvClientICE)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvProductType: TextView = view.findViewById(R.id.tvProductType)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_demande, parent, false)
        
        // If the parent is a horizontal RecyclerView, set a fixed width (e.g., 85% of screen width)
        if (parent is RecyclerView && (parent.layoutManager as? LinearLayoutManager)?.orientation == LinearLayoutManager.HORIZONTAL) {
            val params = view.layoutParams
            params.width = (parent.context.resources.displayMetrics.widthPixels * 0.85).toInt()
            view.layoutParams = params
        }

        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        val user = usersMap[ticket.userId]

        holder.tvTitle.text = ticket.title ?: "Sans titre"
        holder.tvDescription.text = ticket.description ?: "Pas de description"
        holder.tvProductType.text = ticket.serviceType ?: "N/A"
        
        val clientName = user?.name ?: "Client inconnu"
        holder.tvClientName.text = clientName
        holder.tvClientICE.text = "ICE: ${user?.ice ?: "N/A"}"

        // Set the first alphabet of the client's name
        holder.tvClientInitial.text = if (clientName.isNotEmpty()) {
            clientName.first().uppercaseChar().toString()
        } else {
            "?"
        }

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
