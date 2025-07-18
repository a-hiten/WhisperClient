package com.example.whisperclient

data class WhisperRowData(

    // ２－１．ユーザーID、ユーザ名、ささやき番号、ささやき内容、アイコンの画像パス、いいねフラグを保持する変数を宣言する。
    val userId: String,            // ユーザーID
    val userName: String,       // ユーザ名
    val whisperId: Int,         // ささやき番号:
    val whisperText: String,    // ささやき内容
    val userImage: String,      // アイコンの画像パス
    var goodImage: Boolean,       // いいねフラグ

)




