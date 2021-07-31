package com.blokkok.app.ui.main.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.modsys.ModuleManager
import com.blokkok.modsys.models.ModuleMetadata
import com.google.android.material.switchmaterial.SwitchMaterial

class ModulesRecyclerViewAdapter : RecyclerView.Adapter<ModulesRecyclerViewAdapter.ViewHolder>() {

    var modules = ArrayList<ModuleMetadata>()

    fun updateView(modules: ArrayList<ModuleMetadata>) {
        this.modules = modules
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.module_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curModule = modules[position]

        holder.name.text = curModule.name
        holder.description.text = curModule.description
        holder.enableSwitch.isChecked = curModule.enabled
        holder.enableSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                ModuleManager.enableModule(curModule.id)
            } else {
                ModuleManager.disableModule(curModule.id)
            }
        }

        holder.root.setOnLongClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("Confirmation")
                .setMessage("Do you really want to delete ${curModule.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    ModuleManager.deleteModule(curModule.id)

                    Toast.makeText(it.context, "${curModule.name} deleted", Toast.LENGTH_SHORT)
                        .show()

                    modules.remove(curModule)
                    notifyItemRemoved(holder.adapterPosition)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .create()
                .show()

            true
        }
    }

    override fun getItemCount(): Int = modules.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View = itemView.findViewById(R.id.module_root)
        val name: TextView = itemView.findViewById(R.id.module_title)
        val description: TextView = itemView.findViewById(R.id.module_desc)
        val enableSwitch: SwitchMaterial = itemView.findViewById(R.id.enable_module)
    }
}