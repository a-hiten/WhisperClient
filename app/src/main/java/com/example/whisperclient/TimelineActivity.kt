package com.example.whisperclient

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
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

// １．OverFlowMenuActivityクラスを継承する
class TimelineActivity : OverflowMenuActivity() {

    // ２．画面生成時（onCreate処理）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timeline)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val recyclerView = findViewById<RecyclerView>(R.id.timelineRecycle)      // listの内容はささやき行情報を参照してる
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ２－２．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId

        // １－２－２．ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようなデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", loginUserId)
        }
        // BodyのデータをAPIに送る為にRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

        // Requestを作成
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "timelineInfo.php")
            .post(requestBody)
            .build()

        // リクエスト送信（非同期処理）
        client.newCall(request).enqueue(object : Callback {
            // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                // ログ
                println("なかみだよ～"+ body)

                // APIから取得したJSON文字列をJSONオブジェクトに変換
                runOnUiThread {
                    val json = JSONObject(body)
                    val status = json.optString("status", json.optString("result", "error"))

                    if (status != "success") {
                        val errorMsg = json.optString("error", "エラーが発生しました。")
                        runOnUiThread {
                            Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                    }

                    val whisperList = mutableListOf<WhisperRowData>()
                    val whispers = json.optJSONArray("whisperList") ?: JSONArray()

                    // ログ
                    Log.d("Timeline", "loginUserId = $loginUserId")
                    Log.d("Timeline", "whispers length = ${whispers.length()}")

                    for (i in 0 until whispers.length()) {
                        val obj = whispers.getJSONObject(i)
                        val data = WhisperRowData(
                            userId = 0,
                            userName = obj.optString("userName"),
                            whisperId = obj.optInt("whisperNo"),
                            whisperText = obj.optString("content"),
                            userImage = "",
                            goodImage = obj.optBoolean("goodFlg")
                        )
                        whisperList.add(data)
                    }
                    val recyclerView = findViewById<RecyclerView>(R.id.timelineRecycle)
                    recyclerView.layoutManager = LinearLayoutManager(this@TimelineActivity)
                    recyclerView.adapter = WhisperAdapter(whisperList, this@TimelineActivity)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // ２－３－２－１．エラーメッセージをトースト表示する
                runOnUiThread {
                    Toast.makeText(applicationContext, "リクエストに失敗しました。", Toast.LENGTH_SHORT)
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
