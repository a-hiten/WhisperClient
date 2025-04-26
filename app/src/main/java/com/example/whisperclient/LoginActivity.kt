package com.example.whisperclient

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // １．画面生成時（onCreate処理）
        // １－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val loginText = findViewById<TextView>(R.id.loginText)          // ログインテキスト
        val userIdEdit = findViewById<EditText>(R.id.userIdEdit)        // メールアドレス入力
        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)    // パスワード入力
        val loginButton = findViewById<Button>(R.id.loginButton)        // ログインボタン
        val createButton = findViewById<Button>(R.id.createButton)      // ユーザ作成画面へ遷移するボタン


        // １－２．loginButtonのクリックイベントリスナーを作成する
        loginButton.setOnClickListener {
            // １－２－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            val userId = userIdEdit.text.toString()
            val password = passwordEdit.text.toString()

            // isBlankで未入力チェックしてる。
            if (userId.isBlank() && password.isBlank()) {
                Toast.makeText(applicationContext, "ユーザーIDとパスワードを入力してください。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }
        // メッセージ内容：ユーザーIDとパスワードを入力してください



//        １－２－２．ログイン認証APIをリクエストして入力ユーザのログイン認証を行う



//        １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
//        １－２－２－１ー１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる


//        １－２－３－１－２．グローバル変数loginUserIdに作成したユーザIDを格納する


//        １－２－３－１－３．タイムライン画面に遷移する



//        １－２－３－１－４．自分の画面を閉じる



//        １－２－２－２．リクエストが失敗した時(コールバック処理)
//        １－２－２－２－１．エラーメッセージをトースト表示する


//        １－３．createButtonのクリックイベントリスナーを作成する
//        １－３－１．ユーザ作成画面に遷移する







    }
}