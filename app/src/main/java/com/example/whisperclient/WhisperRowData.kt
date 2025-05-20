package com.example.whisperclient

//nemui
data class WhisperRowData(

    // ２－１．ユーザーID、ユーザ名、ささやき番号、ささやき内容、アイコンの画像パス、いいねフラグを保持する変数を宣言する。

    val userId: Int,            // ユーザーID
    val userName: String,       // ユーザ名
    val whisperId: Int,         // ささやき番号
    val whisperText: String,    // ささやき内容
    val userImage: String,      // アイコンの画像パス
    val goodImage: Boolean,       // いいねフラグ


)




