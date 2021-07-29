package com.blokkok.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.modsys.ModuleManager
import com.blokkok.modsys.models.ModuleMetadata
import com.google.android.material.switchmaterial.SwitchMaterial

class ModulesRecyclerViewAdapter : RecyclerView.Adapter<ModulesRecyclerViewAdapter.ViewHolder>() {

    var modules: List<ModuleMetadata> = emptyList()

    fun updateView(modules: List<ModuleMetadata>) {
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
        holder.enableSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                ModuleManager.enableModule(curModule.id)
            } else {
                ModuleManager.disableModule(curModule.id)
            }
        }
    }

    override fun getItemCount(): Int = modules.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.module_title)
        val description: TextView = itemView.findViewById(R.id.module_desc)
        val enableSwitch: SwitchMaterial = itemView.findViewById(R.id.enable_module)
    }
}