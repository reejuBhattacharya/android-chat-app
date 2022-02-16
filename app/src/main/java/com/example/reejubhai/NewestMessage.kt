package com.example.reejubhai

class NewestMessage (val uid:String, val username: String, val message: String, val profileImageUrl: String) {
    constructor() : this("", "", "", "")
}
