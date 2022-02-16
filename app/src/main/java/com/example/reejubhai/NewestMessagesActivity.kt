package com.example.reejubhai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.reejubhai.databinding.ActivityNewestMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NewestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser : User? = null
    }

    private val newestMessages: MutableList<NewestMessage> = mutableListOf()
    private lateinit var binding: ActivityNewestMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewestMessagesBinding.inflate(layoutInflater)

        binding.newestMessagesRecyclerview.adapter = NewestMessageAdapater(newestMessages) { position ->
            onListItemClick(position)
        }
        verifyUserLoggedIn()
        getCurrentUser()
        listenForLatestMessages()
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.new_message_menu -> {
                val intent = Intent(this, AddMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.sign_out_menu -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, SignupActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.newest_messages_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null) {
            val intent = Intent(this, SignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun getCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    val map = HashMap<String, NewestMessage>()

    private fun refreshRV() {
        map.values.forEach {
            newestMessages.add(it)
        }
        binding.newestMessagesRecyclerview.adapter?.notifyDataSetChanged()
    }

    private fun listenForLatestMessages() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$uid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessageDataBase::class.java) ?: return
                val otherPersonId = if(message.fromId==uid) message.toId
                else message.fromId
                FirebaseDatabase.getInstance().getReference("/users/$otherPersonId")
                    .addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java) ?: return
                            val newestMessage = NewestMessage(
                                user.uid,
                                user.username,
                                message.text,
                                user.profileImageUrl
                            )
                            map[snapshot.key!!] = newestMessage
                            refreshRV()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessageDataBase::class.java) ?: return
                val otherPersonId = if(message.fromId==uid) message.toId
                else message.fromId
                FirebaseDatabase.getInstance().getReference("/users/$otherPersonId")
                    .addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java) ?: return
                            val newestMessage = NewestMessage(
                                user.uid,
                                user.username,
                                message.text,
                                user.profileImageUrl
                            )
                            map[snapshot.key!!] = newestMessage
                            refreshRV()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun onListItemClick(position: Int)
    {
        val user = User(
            newestMessages[position].uid,
            newestMessages[position].username,
            newestMessages[position].profileImageUrl
        )
        val intent = Intent(this, ChatScreenActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("USER_KEY", user)
        startActivity(intent)
    }
}