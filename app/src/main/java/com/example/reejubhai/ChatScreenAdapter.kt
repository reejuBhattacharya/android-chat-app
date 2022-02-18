package com.example.reejubhai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatScreenAdapter(private val dataset: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Since we have two layouts added dynamically according to the data, we need to implement
    // we need to implement two Viewholders seperately, for each layout
    class LeftMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgTextView: TextView = view.findViewById(R.id.left_message_text_textview)
        val usernameTextView: TextView = view.findViewById(R.id.left_message_username_textview)
    }

    class RightMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgTextView: TextView = view.findViewById(R.id.right_message_text_textview)
        val usernameTextView: TextView = view.findViewById(R.id.right_message_username_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // returns ViewHolder according to the viewType, as specified by the getItemViewType function
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
        // we bind the data according to whether the message is from the user or not
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

    // specifies who the sender is for each message item
    override fun getItemViewType(position: Int): Int {
        val message = dataset[position]
        return if(message.isUser) 1
        else 0
    }
}