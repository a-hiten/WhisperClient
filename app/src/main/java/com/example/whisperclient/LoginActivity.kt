package com.example.whisperclient

import android.content.Context
import android.content.Intent
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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

            // パターン１　未入力チェック
            // isBlankで未入力チェックしてる。
            // userIdとpasswordが両方空ならトーストを出す
            if (userId.isBlank() && password.isBlank()) {
                // メッセージ内容：ユーザーIDとパスワードを入力してください
                Toast.makeText(
                    applicationContext,
                    "ユーザーIDとパスワードを入力してください。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener

                // パターン2 未入力チェック
//            when {
//                // userIdとpasswordが両方空ならトーストを出す
//                userId.isBlank() && password.isBlank() -> {
//                    Toast.makeText(applicationContext, "ユーザーIDとパスワードを入力してください。", Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
//                // userIdが空ならトーストを出す
//                userId.isBlank() -> {
//                    Toast.makeText(applicationContext, "ユーザーIDを入力してください。", Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
//                // passwordが空ならトーストを出す
//                password.isBlank() -> {
//                    Toast.makeText(applicationContext, "パスワードを入力してください。", Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
//            }

            }


            // １－２－２．ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType : MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("userId", userId)
                put("password", password)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成
            val request = Request.Builder()
                .url("各自のサーバーアドレスに設定")  // URL設定
                .post(requestBody)
                .build()

            // リクエスト送信（非同期処理）
            client.newCall(request!!).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response){
                    val body = response.body?.string()
                    println("レスポンスを受診しました: $body")

                    if (response.isSuccessful && body != null) {
                        // APIから取得したJSON文字列をJSONオブジェクトに変換
                        val json = JSONObject(body)
                        val success = json.optBoolean("success", false)

                        if (success) {
                            // ログイン成功
                            // １－２－３－１－２．グローバル変数loginUserIdに作成したユーザIDを格納する
                            val loginUserId = json.optString("userId", "")

                            runOnUiThread {
                                //１－２－３－１－３．タイムライン画面に遷移する
                                val intent = Intent(this@LoginActivity, TimelineActivity::class.java)
                                intent.putExtra("loginUserId", loginUserId)
                                startActivity(intent)

                                //　１－２－３－１－４．自分の画面を閉じる
                                finish()
                            }
                        } else {
                            // ログイン失敗
                            val errorMessage = json.optString("error", "ログインに失敗しました")
                            runOnUiThread {
                                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "サーバーエラーが発生しました", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // １－２－２－２．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        // １－２－２－２－１．エラーメッセージをトースト表示する
                        Toast.makeText(applicationContext, "リクエストが失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
        // １－３．createButtonのクリックイベントリスナーを作成する
        createButton.setOnClickListener {
            // Intentでどの画面に行きたいかを指定する
            val createBt = Intent(this, CreateUserActivity::class.java)
            // １－３－１．ユーザ作成画面に遷移する
            startActivity(createBt)
        }







    }
}