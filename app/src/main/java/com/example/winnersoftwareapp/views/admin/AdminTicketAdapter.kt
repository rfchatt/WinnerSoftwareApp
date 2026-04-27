package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Ticket
import com.example.winnersoftwareapp.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

class AdminTicketAdapter(
    private var ticketList: List<Ticket>,
    private var usersMap: Map<String, User> = emptyMap()
) : RecyclerView.Adapter<AdminTicketAdapter.TicketViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClientInitial: TextView = view.findViewById(R.id.ivClientN)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvClientICE: TextView = view.findViewById(R.id.tvClientICE)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvProductType: TextView = view.findViewById(R.id.tvProductType)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
        val ivCall: ImageView = view.findViewById(R.id.ivCall)
        val imEmail: ImageView = view.findViewById(R.id.imEmail)
        val ivExpandArrow: ImageView = view.findViewById(R.id.iv_expand_arrow)
        val llHeader: LinearLayout = view.findViewById(R.id.ll_header)
        val llExpandableContent: LinearLayout = view.findViewById(R.id.ll_expandable_content)
        
        val btnInProgress: MaterialButton = view.findViewById(R.id.btn_set_in_progress)
        val btnCompleted: MaterialButton = view.findViewById(R.id.btn_set_completed)
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
        
        val clientName = user?.name ?: "Client inconnu"
        holder.tvClientName.text = clientName
        holder.tvClientICE.text = "ICE: ${user?.ice ?: "N/A"}"

        holder.tvClientInitial.text = if (clientName.isNotEmpty()) {
            clientName.first().uppercaseChar().toString()
        } else { "?" }

        val isExpanded = expandedPositions.contains(position)
        holder.llExpandableContent.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.ivExpandArrow.rotation = if (isExpanded) 180f else 0f

        holder.llHeader.setOnClickListener {
            if (isExpanded) expandedPositions.remove(position) else expandedPositions.add(position)
            notifyItemChanged(position)
        }

        // --- الاتصال بالزبون ---
        holder.ivCall.setOnClickListener {
            val phone = user?.phone
            if (!phone.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Numéro de téléphone non disponible", Toast.LENGTH_SHORT).show()
            }
        }

        // --- مراسلة الزبون عبر الإيميل ---
        holder.imEmail.setOnClickListener {
            val email = user?.email
            if (!email.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:$email")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Concernant votre ticket: ${ticket.title}")
                holder.itemView.context.startActivity(intent)
            }
        }

        val ticketRef = FirebaseDatabase.getInstance().reference.child("tickets").child(ticket.id ?: "")

        holder.btnInProgress.setOnClickListener {
            ticketRef.child("status").setValue("En cours")
        }

        holder.btnCompleted.setOnClickListener {
            ticketRef.child("status").setValue("Terminé")
        }

        holder.ivDelete.setOnClickListener {
            showDeleteConfirmation(holder.itemView.context, ticket)
        }
    }

    private fun showDeleteConfirmation(context: android.content.Context, ticket: Ticket) {
        AlertDialog.Builder(context)
            .setTitle("Confirmation")
            .setMessage("Voulez-vous vraiment supprimer cette demande ?")
            .setPositiveButton("Supprimer") { _, _ ->
                ticket.id?.let { ticketId ->
                    FirebaseDatabase.getInstance().reference.child("tickets").child(ticketId).removeValue()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun getItemCount(): Int = ticketList.size

    fun updateData(newTickets: List<Ticket>, newUsersMap: Map<String, User>) {
        ticketList = newTickets
        usersMap = newUsersMap
        expandedPositions.clear()
        notifyDataSetChanged()
    }
}