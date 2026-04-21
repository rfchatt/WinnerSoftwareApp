package com.example.winnersoftwareapp.views.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketAdapter(private var ticketList: List<Ticket>) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_item_title)
        val tvStatus: TextView = view.findViewById(R.id.tv_item_status)
        val tvCategory: TextView = view.findViewById(R.id.tv_item_category)
        val tvDate: TextView = view.findViewById(R.id.tv_item_date)
        val tvDescription: TextView = view.findViewById(R.id.tv_item_description)
        val tvPriority: TextView = view.findViewById(R.id.tv_item_priority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client_demande, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        
        holder.tvTitle.text = ticket.title
        holder.tvStatus.text = ticket.status
        holder.tvCategory.text = ticket.serviceType
        holder.tvDescription.text = ticket.description
        holder.tvPriority.text = "Priorité: ${ticket.priority}"

        // Format Date
        ticket.timestamp?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            holder.tvDate.text = sdf.format(Date(it))
        }

        // Optional: Change status background/color based on status
        when (ticket.status) {
            "En attente" -> holder.tvStatus.setBackgroundResource(R.drawable.bg_border_radius_blue)
            "En cours" -> holder.tvStatus.setBackgroundResource(R.drawable.bg_border_radius_blue) // Update if you have different bg
            "Terminé" -> holder.tvStatus.setBackgroundResource(R.drawable.bg_border_radius_blue) // Update if you have different bg
        }
    }

    override fun getItemCount(): Int = ticketList.size

    fun updateData(newList: List<Ticket>) {
        ticketList = newList
        notifyDataSetChanged()
    }
}