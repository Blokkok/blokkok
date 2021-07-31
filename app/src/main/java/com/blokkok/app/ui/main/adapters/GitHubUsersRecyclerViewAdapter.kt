package com.blokkok.app.ui.main.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.blokkok.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GitHubUsersRecyclerViewAdapter : RecyclerView.Adapter<GitHubUsersRecyclerViewAdapter.ViewHolder>() {

    var users: List<GitHubUser> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.contributor_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = users[position]

        holder.name.text = currentItem.name

        holder.avatar.shapeAppearanceModel = holder.avatar.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(
                CornerFamily.ROUNDED,
                holder.itemView.context.resources.getDimension(R.dimen.cnitem_avatar_round_radius)
            )
            .build()

        holder.avatar.load(currentItem.avatar)
        holder.root.setOnClickListener {
            // Go to the user's github profile
            startActivity(
                it.context,
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(currentItem.githubLink)
                },
                null
            )
        }
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View                  = itemView.findViewById(R.id.cnitem_root)
        val avatar: ShapeableImageView  = itemView.findViewById(R.id.cnitem_avatar)
        val name: TextView              = itemView.findViewById(R.id.cnitem_name)
    }
}

@Serializable
data class GitHubUser(
    @SerialName("avatar_url") val avatar: String,
    @SerialName("login") val name: String,
    @SerialName("html_url") val githubLink: String,
)