package com.example.whisperclient

data class GoodRowData(
    val userId: String,     // ユーザID
    val userName: String,   // ユーザ名
    val whisper: String,       // ささやき内容
    val gcnt: Int,          // いいね数
    val userImage: String,  // 画像

)

