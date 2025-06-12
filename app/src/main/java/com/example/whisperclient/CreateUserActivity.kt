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

            //１－２－３．ユーザ作成処理APIをリクエストしてユーザの追加を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ(APIに渡したいパラメータを設定)
            val requestBodyJson = JSONObject().apply {
                put("userName", userName)
                put("userId", userId)
                put("password", password)
            }
            // BodyのデータをAPIに送るためにRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "userAdd.php")

                .post(requestBody) // リクエストするパラメータ設定
                .build()

            client.newCall(request).enqueue(object : Callback {
                // １－２－３－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val bodyStr = response.body?.string().orEmpty()
                    runOnUiThread {
                        val json = JSONObject(bodyStr)
                        val status = json.optString("status", json.optString("result", "error"))

                        // １－２－３－１ー１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                        if (status != "success") {
                            val errMsg = json.optString("error", "ユーザー作成に失敗しました")
                            Toast.makeText(applicationContext, errMsg, Toast.LENGTH_SHORT)
                                .show()
                            return@runOnUiThread
                        }

                        // １－２－３－１ー２．グローバル変数loginUserIdに作成したユーザIDを格納する
                        val createdUserId = json.optString("userId", userId)
                        MyApplication.getInstance().loginUserId = createdUserId

                        // １－２－３－１ー３．タイムライン画面に遷移する
                        val intent = Intent(this@CreateUserActivity, TimelineActivity::class.java)
                        intent.putExtra("loginUserId", createdUserId)
                        startActivity(intent)

                        // １－２－３－１ー４．自分の画面を閉じる
                        finish()
                    }
                }

                // １－２－３－２．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        // １－２－３－２ー１．エラーメッセージをトースト表示する
                        Toast.makeText(
                            applicationContext,
                            "リクエストが失敗しました: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        // １－３．cancelButtonのクリックイベントリスナーを作成する
        cancelButton.setOnClickListener {
            // １－３－１．自分の画面を閉じる
            finish()
        }
    }
}

