package com.blokkok.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import java.util.*
import kotlin.collections.ArrayList

data class StoreItemMetadata(
    val title: String = "Error",
    val description: String = "Error",
    val whats_new: String = "Error",
    val downloads: Int = 0,
    val likes: ArrayList<String> = ArrayList(),
    val owner: String = "uidError",
    val type: String = "project",
    val file: String = "error",
    val publish_date: Date? = null,
    val update_date: Date? = null,
    val visibility: String = "public",
);

class StoreItemAdapter(
    private var storeList: List<StoreItemMetadata>

) : RecyclerView.Adapter<StoreItemAdapter.ViewHolder>() {
    fun updateView(newStoreList: List<StoreItemMetadata>) {
        storeList = newStoreList;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.store_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = storeList[position];

        holder.textViewModuleTitle.text = currentItem.title;
    }

    override fun getItemCount(): Int = storeList.size;

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewModuleTitle: TextView = itemView.findViewById(R.id.textViewModuleTitle);
    }
}