package com.ekcmn.kotlinstoragedeneme.model

import java.io.Serializable

data class User(
    var name : String,
    var profilePic: String
):Serializable {
}