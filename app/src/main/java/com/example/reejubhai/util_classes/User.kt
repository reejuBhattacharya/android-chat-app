package com.example.reejubhai.util_classes

import java.io.Serializable


class User(val uid: String, val username: String, val profileImageUrl: String) : Serializable {
    constructor() : this("", "", "")
}
