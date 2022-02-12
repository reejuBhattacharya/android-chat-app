package com.example.reejubhai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AddMessageAdapter(
    private val dataSet: List<User>,
    private val onItemClicked: (position: Int) -> Unit
    )
    : RecyclerView.Adapter<AddMessageAdapter.ViewHolder>(){

    class ViewHolder(
        view: View,
        private val onItemClicked: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView: TextView = view.findViewById(R.id.add_message_item_textview)
        val imageView: ImageView = view.findViewById(R.id.add_message_item_imageview)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_add_message_item, parent, false)

        return ViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = dataSet[position]
        holder.textView.text = user.username
        Picasso.get().load(user.profileImageUrl).placeholder(R.drawable.placeholder).into(holder.imageView)
    }

    override fun getItemCount() = dataSet.size
}