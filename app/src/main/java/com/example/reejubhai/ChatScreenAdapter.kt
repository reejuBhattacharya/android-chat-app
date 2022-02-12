package com.example.reejubhai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatScreenAdapter(private val dataset: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class LeftMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgTextView: TextView = view.findViewById(R.id.left_message_text_textview)
        val usernameTextView: TextView = view.findViewById(R.id.left_message_username_textview)
    }

    class RightMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgTextView: TextView = view.findViewById(R.id.right_message_text_textview)
        val usernameTextView: TextView = view.findViewById(R.id.right_message_username_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==1) {
            RightMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.message_right, parent, false
                )
            )
        } else {
            LeftMessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.message_left, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msgText = dataset[position].messageText
        val username = dataset[position].username
        if(dataset[position].isUser) {
            (holder as RightMessageViewHolder).apply {
                msgTextView.text = msgText
                usernameTextView.text = username
            }
        } else {
            (holder as LeftMessageViewHolder).apply {
                msgTextView.text = msgText
                usernameTextView.text = username
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    override fun getItemViewType(position: Int): Int {
        val message = dataset[position]
        return if(message.isUser) 1
        else 0
    }
}