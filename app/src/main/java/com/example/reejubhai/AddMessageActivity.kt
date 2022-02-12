package com.example.reejubhai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reejubhai.databinding.ActivityAddMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddMessageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddMessageBinding
    private lateinit var userList: MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMessageBinding.inflate(layoutInflater)
        fetchUsers()
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select User"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList = mutableListOf()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    userList.add(user!!)
                }
                binding.recyclerviewAddMessage.adapter = AddMessageAdapter(userList) { position ->  
                    onListItemClick(position)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun onListItemClick(position: Int)
    {
        val intent = Intent(this, ChatScreenActivity::class.java)
        intent.putExtra("USER_KEY", userList[position])
        startActivity(intent)
    }
}