package com.example.winnersoftwareapp.views.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase

class AdminUserAdapter(private var userList: List<User>) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_user_name)
        val tvIce: TextView = view.findViewById(R.id.tv_user_ice)
        val btnApprove: MaterialButton = view.findViewById(R.id.btn_approve)
        val btnReject: MaterialButton = view.findViewById(R.id.btn_reject)
        val btnMakeAdmin: MaterialButton = view.findViewById(R.id.btn_make_admin)
        val ivInfo: ImageView = view.findViewById(R.id.iv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_user_validation, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvName.text = user.name
        holder.tvIce.text = "ICE: ${user.ice}"

        val database = FirebaseDatabase.getInstance().reference.child("users")

        holder.btnApprove.setOnClickListener {
            user.uid?.let { uid ->
                database.child(uid).child("status").setValue("approved")
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Compte approuvé", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        holder.btnReject.setOnClickListener {
            user.uid?.let { uid ->
                database.child(uid).child("status").setValue("rejected")
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "Compte refusé", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        holder.btnMakeAdmin.setOnClickListener {
            user.uid?.let { uid ->
                val updates = mapOf(
                    "role" to "admin",
                    "status" to "approved"
                )
                database.child(uid).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context, "${user.name} est maintenant Admin", Toast.LENGTH_SHORT).show()
                    }
            }
        }

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