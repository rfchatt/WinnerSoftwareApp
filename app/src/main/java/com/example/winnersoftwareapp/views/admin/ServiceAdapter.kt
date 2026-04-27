package com.example.winnersoftwareapp.views.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Service

class ServiceAdapter(
    private var services: List<Service>,
    private val onEditClick: (Service) -> Unit,
    private val onDeleteClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_service_name)
        val tvPhone: TextView = view.findViewById(R.id.tv_service_phone)
        val ivEdit: ImageView = view.findViewById(R.id.iv_edit_service)
        val ivDelete: ImageView = view.findViewById(R.id.iv_delete_service)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.tvName.text = service.name
        holder.tvPhone.text = "Tél: ${service.phone ?: "N/A"}"
        
        holder.ivEdit.setOnClickListener { onEditClick(service) }
        holder.ivDelete.setOnClickListener { onDeleteClick(service) }
    }

    override fun getItemCount() = services.size

    fun updateData(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }
}