package com.blokkok.app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blokkok.app.R
import com.blokkok.app.managers.libraries.Library
import java.util.*
import kotlin.collections.ArrayList

class MoveableLibrariesRecyclerView : RecyclerView.Adapter<MoveableLibrariesRecyclerView.ViewHolder>() {

    val libraries = ArrayList<Library>()

    fun setLibraries(libraries: List<Library>) {
        this.libraries.clear()
        this.libraries.addAll(libraries)
        notifyDataSetChanged()
    }

    fun addLibrary(library: Library) {
        libraries.add(library)

        if (libraries.size == 1) notifyDataSetChanged()
        else notifyItemInserted(libraries.size - 1)
    }

    fun swapItem(from: Int, to: Int) {
        Collections.swap(libraries, from, to)
        // FIXME: 7/22/21 Somehow use notifyItemMoved, I can't use it since onBindViewHolder's position won't change
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.moveable_library_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.libraryName.text = libraries[position].name

        holder.up.setOnClickListener {
            if (position != 0) swapItem(position, position + 1)
        }

        holder.down.setOnClickListener {
            if (position != libraries.size - 1) swapItem(position, position - 1)
        }
    }

    override fun getItemCount(): Int = libraries.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View              = itemView.findViewById(R.id.mli_root)
        val libraryName: TextView   = itemView.findViewById(R.id.mli_library_name)
        val up: View                = itemView.findViewById(R.id.mli_up)
        val down: View              = itemView.findViewById(R.id.mli_down)
    }
}