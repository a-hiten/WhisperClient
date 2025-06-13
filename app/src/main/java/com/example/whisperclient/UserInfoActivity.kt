package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.text.Typography.section

// オーバーフローメニューを継承をする
class UserInfoActivity : OverflowMenuActivity() {
    // ２．画面生成時（onCreate処理）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        println("ユーザ情報画面")
        Log.d("チェック", "MyApplication.loginUserId = [${MyApplication.getInstance()}]")
        Log.d("チェック", "MyApplication.loginUserId = [${MyApplication.getInstance().loginUserId}]")

        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する
        val userImage = findViewById<ImageView>(R.id.userImage)
        val userName = findViewById<TextView>(R.id.userNameText)
        val profile = findViewById<TextView>(R.id.profileText)
        val follow = findViewById<TextView>(R.id.followText)
        val follower = findViewById<TextView>(R.id.followerText)
        val fwrnt = findViewById<TextView>(R.id.followCntText)
        val fwrcnt = findViewById<TextView>(R.id.followerCntText)
        val whisper = findViewById<RadioButton>(R.id.whisperRadio)
        val good = findViewById<RadioButton>(R.id.goodInfoRadio)

        val recyclerView = findViewById<RecyclerView>(R.id.userRecycle)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // ２－３．ユーザささやき情報取得API　共通実行メソッドを呼び出す
        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようなデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
//            put("userId", userId)
//            put("loginUserId", loginUserId)
        }
        println("requestBodyJsonのやつ" + requestBodyJson)


        // BodyのデータをAPIに送る為にRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

        // Requestを作成
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "userWhisperInfo.php")
            .post(requestBody)
            .build()

        // リクエスト送信（非同期処理）
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                // ログ
                println("なかみだよ～" + body)

                // APIから取得したJSON文字列をJSONオブジェクトに変換
                runOnUiThread {
                    val json = JSONObject(body)
                    val status = json.optString("status", json.optString("result", "error"))

                    // ２－４－２－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                    if (status != "success") {
                        val errorMsg = json.optString("error", "エラーが発生しました。")
                        runOnUiThread {
                            Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                    }

                }
            }

            // ２－４－２．リクエストが失敗した時(コールバック処理)
            override fun onFailure(call: Call, e: IOException) {
                // ２－４－２－１．エラーメッセージをトースト表示する
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "リクエストに失敗しました。",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }


    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}