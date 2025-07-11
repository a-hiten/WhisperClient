package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

// １．OverFlowMenuActivityクラスを継承する
class UserEditActivity : OverflowMenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val userImage = findViewById<ImageView>(R.id.userImage)         //　自分のプロフィール画像
        val userId = findViewById<TextView>(R.id.userIdText)            // ユーザID（メールアドレスのこと？）
        val userName = findViewById<EditText>(R.id.userNameEdit)        // ユーザの名前
        val profile = findViewById<EditText>(R.id.profileEdit)          // プロフィール入力場所（自己紹介とか書くとこ）
        val changeBt = findViewById<Button>(R.id.changeButton)            // 保存ボタン
        val cancelBt = findViewById<Button>(R.id.cancelButton)            // キャンセルボタン


        // ２－２．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId ?: "null or empty"
        Log.d("チェック", "loginUserId = [$loginUserId]")
        Log.d("チェック","MyApplication.loginUserId = [${MyApplication.getInstance().loginUserId}]")

        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようなデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", loginUserId)   // ユーザID

            Log.d("チェック", "userId.text = [${loginUserId}]")
        }
        // BodyのデータをAPIに送る為にRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        // Requestを作成
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "userInfo.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            // ２－３－１．正常にレスポンスを受け取った時(コールバック処理)
            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string().orEmpty()

                println("APIのれすぽんす(´ぅω・｀)ﾈﾑｲ" + bodyStr)
                Log.d("送信データ", requestBodyJson.toString())

                runOnUiThread {
                    val json = JSONObject(bodyStr)
                    val status = json.optString("status", json.optString("result", "error"))

                    if (status != "success") {
                        val err =
                            json.optString("error", json.optString("errMsg", "JSONデータエラー"))
                        Toast.makeText(applicationContext, err, Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", bodyStr)
                        return@runOnUiThread
                    }

                    // ２－３－１－２．取得したデータを各オブジェクトにセットする
                        userId.text = json.optString("userId")
                        userName.setText(json.optString("userName"))
                        profile.setText(json.optString("profile"))
                }
            }

            // ２－３－２．リクエストが失敗した時(コールバック処理)
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    // ２－３－２－１．エラーメッセージをトースト表示する
                    Toast.makeText(
                        applicationContext,
                        "リクエスト失敗しました。: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        // ２－４－１．ユーザ変更処理APIをリクエストして入力したユーザ情報の更新処理を行う
        changeBt.setOnClickListener {
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val updateBodyJson = JSONObject().apply {
                put("userId", loginUserId)   // ユーザID
                put("userName", userName.text.toString())   // ユーザ名
                put("profile", profile.text.toString())     // プロフィール
            }
            val updateRequestBody = updateBodyJson
                .toString()
                .toRequestBody(mediaType)

            val updateRequest = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "userUpd.php")
                .post(updateRequestBody)
                .build()

            client.newCall(updateRequest).enqueue(object : Callback {

                // ２－４－１－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val bodyStr = response.body?.string().orEmpty()

                    runOnUiThread {
                        val json = JSONObject(bodyStr)
                        val status = json.optString("status", json.optString("result", "error"))

                        // ２－４－１－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                        if (status != "success") {
                            val err = json.optString("error", json.optString("errMsg", "JSONデータエラー"))
                            Toast.makeText(applicationContext, err, Toast.LENGTH_SHORT).show()
                            Log.e("UPDATE_API_ERROR", bodyStr)
                            return@runOnUiThread
                        }

                        // ２－４－１－１－２．ユーザ情報画面に遷移する
                        startActivity(Intent(this@UserEditActivity, UserInfoActivity::class.java))

                        // ２－４－１－１－３．自分の画面を閉じる
                        finish()
                    }
                }

                // ２－４－１－２．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        // ２－４－１－２－１．エラーメッセージをトースト表示する
                        Toast.makeText(
                            applicationContext,
                            "更新リクエスト失敗しました。: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
        // ２－５．cancelButtonのクリックイベントリスナーを作成する
        cancelBt.setOnClickListener {
            // ２－５－１．自分の画面を閉じる
            finish()
        }
    }

    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}