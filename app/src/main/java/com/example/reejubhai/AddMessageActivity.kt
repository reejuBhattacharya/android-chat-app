package com.example.reejubhai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.reejubhai.adapters.AddMessageAdapter
import com.example.reejubhai.databinding.ActivityAddMessageBinding
import com.example.reejubhai.util_classes.User
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

        // enable backbutton and add title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select User"
    }

    // fetches all available users in the database
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList = mutableListOf()
                // adds user to userList for the recyclerview
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    userList.add(user!!)
                }
                // adds recyclerview adapter with onClicklistener on each item
                binding.recyclerviewAddMessage.adapter = AddMessageAdapter(userList) { position ->
                    onListItemClick(position)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // onClickListener for each item on the recyclerview
    private fun onListItemClick(position: Int)
    {
        val intent = Intent(this, ChatScreenActivity::class.java)
        intent.putExtra("USER_KEY", userList[position])
        startActivity(intent)
    }
}