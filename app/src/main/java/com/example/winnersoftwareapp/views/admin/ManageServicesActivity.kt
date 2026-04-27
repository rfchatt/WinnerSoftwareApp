package com.example.winnersoftwareapp.views.admin

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winnersoftwareapp.R
import com.example.winnersoftwareapp.models.Service
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class ManageServicesActivity : AppCompatActivity() {
    private lateinit var rvServices: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ServiceAdapter
    private val servicesList = mutableListOf<Service>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_services)

        database = FirebaseDatabase.getInstance().reference.child("services")

        initViews()
        setupRecyclerView()
        loadServices()

        fabAdd.setOnClickListener { showServiceDialog(null) }
        findViewById<View>(R.id.btn_back_nav).setOnClickListener { finish() }
    }

    private fun initViews() {
        rvServices = findViewById(R.id.rv_services)
        fabAdd = findViewById(R.id.fab_add_service)
        progressBar = findViewById(R.id.pb_loading)
    }

    private fun setupRecyclerView() {
        adapter = ServiceAdapter(
            servicesList,
            onEditClick = { service -> showServiceDialog(service) },
            onDeleteClick = { service -> showDeleteConfirmation(service) }
        )
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.adapter = adapter
    }

    private fun loadServices() {
        progressBar.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    initializeDefaultServices()
                    return
                }

                servicesList.clear()
                for (child in snapshot.children) {
                    val service = child.getValue(Service::class.java)
                    service?.let { servicesList.add(it) }
                }
                adapter.updateData(servicesList)
                progressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun initializeDefaultServices() {
        val defaults = listOf(
            Service(name = "Site", phone = "0600000000"),
            Service(name = "Materiel", phone = "0600000001"),
            Service(name = "Logiciel", phone = "0600000002"),
            Service(name = "Autre", phone = "0600000003")
        )
        for (service in defaults) {
            val id = database.push().key
            val newService = service.copy(id = id)
            id?.let { database.child(it).setValue(newService) }
        }
    }

    private fun showServiceDialog(service: Service?) {
        val isEdit = service != null
        val title = if (isEdit) "Modifier Service" else "Ajouter un Service"
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etName = EditText(this)
        etName.hint = "Nom du service"
        if (isEdit) etName.setText(service?.name)
        layout.addView(etName)

        val etPhone = EditText(this)
        etPhone.hint = "Numéro de téléphone"
        etPhone.inputType = InputType.TYPE_CLASS_PHONE
        if (isEdit) etPhone.setText(service?.phone)
        layout.addView(etPhone)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton(if (isEdit) "Enregistrer" else "Ajouter") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    if (isEdit) {
                        val updatedService = service?.copy(name = name, phone = phone)
                        service?.id?.let { database.child(it).setValue(updatedService) }
                    } else {
                        val id = database.push().key
                        val newService = Service(id, name, phone)
                        id?.let { database.child(it).setValue(newService) }
                    }
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showDeleteConfirmation(service: Service) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer")
            .setMessage("Voulez-vous supprimer le service ${service.name} ?")
            .setPositiveButton("Oui") { _, _ ->
                service.id?.let { database.child(it).removeValue() }
            }
            .setNegativeButton("Non", null)
            .show()
    }
}