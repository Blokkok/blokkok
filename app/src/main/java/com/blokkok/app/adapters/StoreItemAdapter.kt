package com.blokkok.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.managers.projects.ProjectMetadata
import kotlinx.serialization.Serializable

@Serializable
data class StoreItemMetadata(
    val title: String,
    val description: String,
    val whats_new: String,
    val downloads: Int,
    val likes: List<String>,
    val owner: String,
    val type: String,
    val file: String,
    val publish_date: Int,
    val update_date: Int,
    val visibility: String,
);

class StoreItemAdapter(
    private var storeList: List<StoreItemMetadata>

) : RecyclerView.Adapter<ProjectsRecyclerView.ViewHolder>() {
    fun updateView(newStoreList: List<StoreItemMetadata>) {
        storeList = newStoreList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsRecyclerView.ViewHolder =
        ProjectsRecyclerView.ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.store_item, parent, false)
        )

    override fun onBindViewHolder(holder: ProjectsRecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}