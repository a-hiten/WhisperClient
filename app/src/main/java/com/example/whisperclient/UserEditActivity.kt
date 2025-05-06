package com.example.whisperclient

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // オーバーフローメニューを表示するやつ
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //    ２－１－１．インフレータにオーバーフローメニューのデザインを設定する
        menuInflater.inflate(R.menu.menu_item, menu)
        //    ２－１－２．戻り値にtrueをセットする
        return true
    }
}