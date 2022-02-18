package com.example.reejubhai

// helper class for NewestMessage activity
class NewestMessage (val uid:String, val username: String, val message: String, val profileImageUrl: String) {
    constructor() : this("", "", "", "")
}
