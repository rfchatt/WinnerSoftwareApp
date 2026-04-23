package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.User

class ApprovedClientsAdapter(private var userList: List<User>) : RecyclerView.Adapter<ApprovedClientsAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_user_name)
        val tvIce: TextView = view.findViewById(R.id.tv_user_ice)
        val tvPhone: TextView = view.findViewById(R.id.tv_user_phone)
        val ivInfo: ImageView = view.findViewById(R.id.iv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_approved_client, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name ?: "N/A"
        holder.tvIce.text = "ICE: ${user.ice ?: "N/A"}"
        holder.tvPhone.text = user.phone ?: "N/A"

        holder.ivInfo.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, adminUserDetails::class.java)
            intent.putExtra("userId", user.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}