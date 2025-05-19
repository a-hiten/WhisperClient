package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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


            createButton.setOnClickListener {
                // HTTP接続用インスタンス生成
                val client = OkHttpClient()
                // JSON形式でパラメータを送るようデータ形式を設定
                val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
                // Bodyのデータ(APIに渡したいパラメータを設定)
                val requestBodyJson = JSONObject().apply {
//                    put("productNo", editText.text)
                }
                // BodyのデータをAPIに送るためにRequestBody形式に加工
                val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
                // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
                val request = Request.Builder()
                    .url("http://10.0.2.2/SampleProject/sample.php") // URL設定
                    .post(requestBody) // リクエストするパラメータ設定
                    .build()



                // １－２－３－１．正常にレスポンスを受け取った時(コールバック処理)
                // リクエスト送信（非同期処理）
                client.newCall(request!!).enqueue(object : Callback {
                    // リクエストが成功した場合の処理を実装
                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println("レスポンスを受信しました: $body")
                        // postメソッドを使うことでUIを操作することができる。(runOnUiThreadメソッドでも可)
//                    textView.post { textView.text = body }

                    }

//                    val cBt = Intent(this, TimelineActivity::class.java)


                    // １－２－３－２．リクエストが失敗した時(コールバック処理)
                    // リクエストが失敗した場合の処理を実装
                    override fun onFailure(call: Call, e: IOException) {
                        // メッセージ内容：全ての項目を入力してください。
                        Toast.makeText(
                            applicationContext,
                            "リクエストが失敗しました。。",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

                //        １－２－３．ユーザ作成処理APIをリクエストしてユーザの追加を行う




            }

        }







        // １－３．cancelButtonのクリックイベントリスナーを作成する
        cancelButton.setOnClickListener {
            // １－３－１．自分の画面を閉じる
            finish()
        }

    }
}



//１．画面生成時（onCreate処理）
//１－１．画面デザインで定義したオブジェクトを変数として宣言する。
//
//１－２．createButtonのクリックイベントリスナーを作成する
//１－２－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
//メッセージ内容：「全ての項目を入力してください」
//
//１－２－２．パスワードと確認パスワードの内容が違う時、エラーメッセージをトースト表示して処理を終了させる
//メッセージ内容：「パスワードが一致しません」
//
//１－２－３．ユーザ作成処理APIをリクエストしてユーザの追加を行う
//１－２－３－１．正常にレスポンスを受け取った時(コールバック処理)
//１－２－３－１ー１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
//
//１－２－３－１ー２．グローバル変数loginUserIdに作成したユーザIDを格納する
//
//１－２－３－１ー３．タイムライン画面に遷移する
//
//１－２－３－１ー４．自分の画面を閉じる
//
//１－２－３－２．リクエストが失敗した時(コールバック処理)
//１－２－３－２ー１．エラーメッセージをトースト表示する
//
//１－３．cancelButtonのクリックイベントリスナーを作成する
//１－３－１．自分の画面を閉じる
