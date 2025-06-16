package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
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

// １．OverFlowMenuActivityクラスを継承する
class SearchActivity : OverflowMenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val user = findViewById<RadioButton>(R.id.userRadio)
        val whisper = findViewById<RadioButton>(R.id.whisperRadio)
        val searchText = findViewById<EditText>(R.id.searchEdit)
        val search = findViewById<Button>(R.id.searchButton)

        // ２－２．searchButtonのクリックイベントリスナーを作成する
        search.setOnClickListener {
            val searchText = searchText.text.toString()

            // ２－２－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            if (searchText.isBlank()) {
                // メッセージ内容：検索内容を入力してください
                Toast.makeText(
                    applicationContext,
                    "検索内容を入力してください。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // ２－２－２．ラジオボタンの選択肢を変数に保持する。
            val section = if (user.isChecked) "1" else "2"

            // ２－２－３．検索結果取得APIをリクエストして検索キーワードに該当する情報取得を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ(APIに渡したいパラメータを設定)
            val requestBodyJson = JSONObject().apply {
                put("section", section)
                put("string", searchText)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "search.php")
                .post(requestBody)
                .build()

            // リクエスト送信（非同期処理）
            client.newCall(request).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    // ログ
                    println("なかみだよ～" + body)

                    // APIから取得したJSON文字列をJSONオブジェクトに変換
                    runOnUiThread {
                        val json = JSONObject(body)
                        val status = json.optString("status", json.optString("result", "error"))

                        // ２－２－３－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                        if (status != "success") {
                            val errorMsg = json.optString("error", "エラーが発生しました。")
                            Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        // ２－２－３－１－２．ラジオボタンの選択肢に合わせて、以下の処理を繰り返すアダプターにリストをセットする
                        if (section == "1") {
                            // ２－２－３－１－２－１．ラジオボタンがuserRadioを選択している時
                            val userList = json.optJSONArray("userList") ?: JSONArray()
                            // ２－２－３－１－２－１－１．ユーザ行情報一覧が存在する間、以下の処理を繰り返す
                            val users = mutableListOf<UserRowData>()

                            for (i in 0 until userList.length()) {
                                val obj = userList.getJSONObject(i)
                                val user = UserRowData(
                                    userId = obj.optString("userId"),
                                    userName = obj.optString("userName"),
                                    Follow = obj.optInt("followCount"),
                                    Follower = obj.optInt("followerCount"),
                                    userImage = obj.optString("userImage")
                                )
                                users.add(user)
                            }

                            Log.d("全体内容", json.toString())
                            Log.d("DEBUG_USER", "ユーザー数: ${users.size}")


                            // ２－２－３－１－２－１－２．ユーザ行情報のアダプターにユーザ情報リストをセットする
                            val recyclerView = findViewById<RecyclerView>(R.id.searchRecycle)
                            // ２－２－３－１－２－１－３．searchRecycleを表示する
                            recyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
                            recyclerView.adapter = UserAdapter(users, this@SearchActivity)

                        } else {
                            // ２－２－３－１－２－２．ラジオボタンがwhisperRadioを選択している時
                            val goodList = json.optJSONArray("whisperList") ?: JSONArray()

                            // ２－２－３－１－２－２－１．いいね行情報一覧が存在する間、以下の処理を繰り返す
                            val goods = mutableListOf<GoodRowData>()

                            for (i in 0 until goodList.length()) {
                                val goodJson = goodList.getJSONObject(i)
                                val good = GoodRowData(
                                    userId = goodJson.optString("userId"),
                                    userName = goodJson.optString("userName"),
                                    whisper = goodJson.optString("content"),
                                    gcnt = goodJson.optInt("gcnt"),
                                    userImage = goodJson.optString("userImage")
                                )
                                goods.add(good)
                            }
                            Log.d("全体内容", json.toString())
                            Log.d("DEBUG_USER", "ユーザー数: ${goods.size}")

                            // ２－２－３－１－２－２－２．いいね行情報のアダプターにいいね情報リストをセットする
                            val recyclerView = findViewById<RecyclerView>(R.id.searchRecycle)

                            // ２－２－３－１－２－２－３．searchRecycleを表示する
                            recyclerView.layoutManager = LinearLayoutManager(this@SearchActivity)
                            recyclerView.adapter = GoodAdapter(goods, this@SearchActivity)
                        }
                    }
                }

                // ２－２－４．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    // ２－２－４－１．エラーメッセージをトースト表示する
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
    }

    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}
