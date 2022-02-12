package com.example.reejubhai

import java.io.Serializable


class User(val uid: String, val username: String, val profileImageUrl: String) : Serializable {
    constructor() : this("", "", "")
}
