package com.example.reejubhai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reejubhai.util_classes.NewestMessage
import com.example.reejubhai.R
import com.squareup.picasso.Picasso

class NewestMessageAdapater(
    private val dataSet: List<NewestMessage>,
    private val onItemClicked: (position: Int) -> Unit
    ):
    RecyclerView.Adapter<NewestMessageAdapater.ViewHolder>()
{
    class ViewHolder(
        view: View,
        private val onItemClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val usernameTextView: TextView = view.findViewById(R.id.newest_messages_username_textview)
        val messageTextView: TextView = view.findViewById(R.id.newest_messages_message_textview)
        val profileImageView: ImageView = view.findViewById(R.id.newest_messages_imageview)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.newest_messages_item, parent, false)
        return ViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.usernameTextView.text = dataSet[position].username
        holder.messageTextView.text = dataSet[position].message
        if(dataSet[position].profileImageUrl=="")   return

        Picasso.get().load(dataSet[position].profileImageUrl).into(holder.profileImageView)
    }

    override fun getItemCount() = dataSet.size
}

