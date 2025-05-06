package com.example.whisperclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // １．画面生成時（onCreate処理）
        // １－１．画面デザインで定義したオブジェクトを変数として宣言する。

        val userNameEdit = findViewById<EditText>(R.id.userNameEdit)
        val userIdEdit = findViewById<EditText>(R.id.userIdEdit)
        val passwordEdit = findViewById<EditText>(R.id.passwordEdit)
        val rePasswordEdit = findViewById<EditText>(R.id.rePasswordEdit)
        val createButton = findViewById<Button>(R.id.createButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)


        // １－２．createButtonのクリックイベントリスナーを作成する
        createButton.setOnClickListener {
            // １－２－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            val userName = userNameEdit.text.toString()
            val userId = userIdEdit.text.toString()
            val password = passwordEdit.text.toString()
            val repassword = rePasswordEdit.text.toString()



            // isBlankで未入力チェックしてる。
            // 全ての項目が空の場合
            if (userName.isBlank() && userId.isBlank() && password.isBlank() && repassword.isBlank()) {
                // メッセージ内容：全ての項目を入力してください。
                Toast.makeText(
                    applicationContext,
                    "全ての項目を入力してください。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 入力されていない項目がある場合　＊仕様書には書いていない処理です。
            if (userName.isBlank() || userId.isBlank() || password.isBlank() || repassword.isBlank()) {
                // メッセージ内容：入力されていない項目があります。
                Toast.makeText(
                    applicationContext,
                    "入力されていない項目があります。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // １－２－２．パスワードと確認パスワードの内容が違う時、エラーメッセージをトースト表示して処理を終了させる
            if (password != repassword) {
                // メッセージ内容：「パスワードが一致しません」
                Toast.makeText(
                    applicationContext,
                    "パスワードが一致しません。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
        }




//        １－２－３．ユーザ作成処理APIをリクエストしてユーザの追加を行う
//        １－２－３－１．正常にレスポンスを受け取った時(コールバック処理)



//        １－２－３－２．リクエストが失敗した時(コールバック処理)


        // １－３．cancelButtonのクリックイベントリスナーを作成する
        cancelButton.setOnClickListener {
            // １－３－１．自分の画面を閉じる
            finish()
        }

    }
}