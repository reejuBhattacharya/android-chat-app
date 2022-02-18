package com.example.reejubhai


// class to store messages in Firebase Database
class ChatMessageDataBase(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}