package com.example.reejubhai.util_classes


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