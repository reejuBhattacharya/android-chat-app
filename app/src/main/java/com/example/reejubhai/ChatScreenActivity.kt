package com.example.reejubhai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.reejubhai.adapters.ChatScreenAdapter
import com.example.reejubhai.databinding.ActivityChatScreenBinding
import com.example.reejubhai.util_classes.Message
import com.example.reejubhai.util_classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivityChatScreenBinding

    private val messageList = mutableListOf<Message>()  // list of all messages (for recyclerview)
    private lateinit var otherUser: User  // person with whom current user is chatting

    companion object {
        const val TAG = "ChatScreenActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otherUser = intent.getSerializableExtra("USER_KEY") as User

        // enable backbutton and add title
        supportActionBar?.title = otherUser.username
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.chatScreenRecyclerview.adapter = ChatScreenAdapter(messageList)

        binding.chatScreenSendButton.setOnClickListener {
            // clean up message to remove prefix whitespace, if any
            val text = binding.chatScreenTextview.text.toString().trim()
            if(text=="")    return@setOnClickListener

            else    performSendMessage()
        }

        listenForMessages()

    }

     private fun performSendMessage() {
         val id = UUID.randomUUID().toString()
         val msg = binding.chatScreenTextview.text.toString()
         val fromId = FirebaseAuth.getInstance().uid
         val toId = otherUser.uid
         val timestamp = System.currentTimeMillis()

        if(fromId==null)    return

        // here we store the message on both user references (sender and reciever) and we
        // use fromId and toId to distinguish between sender and reciever.

         val message = ChatMessageDataBase(id, msg, fromId, toId, timestamp)

         val refFrom = FirebaseDatabase.getInstance()
             .getReference("/user_messages/$fromId/$toId").push()
         refFrom.setValue(message).addOnSuccessListener {
             Log.d(TAG, "saved message in refFrom successfully")
         }.addOnFailureListener {
             Log.d(TAG, "failed to upload refFrom  message: ${it.message}")
         }
         val refTo = FirebaseDatabase.getInstance()
             .getReference("/user_messages/$toId/$fromId").push()
         refTo.setValue(message).addOnSuccessListener {
             Log.d(TAG, "saved message in refTo successfully")
             val adapter = binding.chatScreenRecyclerview.adapter

             // clear text area and scroll to last message
             binding.chatScreenTextview.text.clear()
             binding.chatScreenRecyclerview.scrollToPosition(adapter!!.itemCount - 1)
         }.addOnFailureListener {
             Log.d(TAG, "failed to upload refTo  message: ${it.message}")
         }

        // additionally, we add the message to latest_message referenence on both sides. This is 
        // done for the newest-messages functionality
         FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId/$toId").setValue(message)
         FirebaseDatabase.getInstance().getReference("/latest_messages/$toId/$fromId").setValue(message)
     }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = otherUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessageDataBase::class.java)
                val userId = FirebaseAuth.getInstance().uid
                if(chatMessage!=null)
                {
                    // add the chat message to Message class to pass onto the recyclerview
                    val msg = Message(
                        messageText = chatMessage.text,
                        "", isUser = false
                    )
                    
                    // find out who is sender and reciever of message, required for aligning 
                    //  messages left and right
                    if(chatMessage.fromId==userId) {
                        if(NewestMessagesActivity.currentUser!=null) {
                            msg.username = NewestMessagesActivity.currentUser!!.username
                            msg.isUser = true
                        }
                    } else {
                        msg.username = otherUser.username
                    }

                    // update recyclerview and scroll to the last message after sending message
                    messageList.add(msg)
                    binding.chatScreenRecyclerview.adapter!!
                        .notifyItemChanged(binding.chatScreenRecyclerview.adapter!!.itemCount - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

