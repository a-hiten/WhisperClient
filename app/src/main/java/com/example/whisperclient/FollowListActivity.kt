package com.example.whisperclient

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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

// １．OverFlowMenuActivityクラスを継承する
class FollowListActivity : OverflowMenuActivity() {
    // ２．画面生成時（onCreate処理）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_follow_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val recyclerView = findViewById<RecyclerView>(R.id.followRecycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ２－２．インテント(前画面)から対象ユーザIDと区分（フォロー・フォロワー）を取得する
        val userId = intent.getStringExtra("userId") ?: ""
        val section = intent.getStringExtra("section") ?: "1"

        // ２－３．区分に併せて、followListTextのテキストを変更する
        val follwListText = findViewById<TextView>(R.id.followListText)
        follwListText.text = if (section == "1") "フォロー一覧" else "フォロワー一覧"

        // ２－４．フォロワー情報取得APIをリクエストして対象ユーザのフォロー・フォロワー情報取得処理を行う
        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようなデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", userId)
        }
        // BodyのデータをAPIに送る為にRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

        // Requestを作成
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "followerInfo.php")
            .post(requestBody)
            .build()

        // リクエスト送信（非同期処理）
        client.newCall(request).enqueue(object : Callback {
            // ２－４－１．正常にレスポンスを受け取った時(コールバック処理)
            // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
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

                    // ２－４－２ー２．フォロー情報一覧が存在するかチェックする
                    val followList = mutableListOf<UserRowData>()
                    val followArray = json.optJSONArray("followList") ?: JSONArray()
                    if (followArray.length() > 0) {
                        // ２－４－２ー２－１．フォロー情報が存在する場合、以下の処理を繰り返す
                        for (i in 0 until followArray.length()) {
                            val obj = followArray.getJSONObject(i)
                            // ２－４－２ー２－１－１．フォロー情報をリストに格納する
                            val data = UserRowData(
                                userId = obj.optString("userId"),
                                userName = obj.optString("userName"),
                                Follow = obj.optInt("followCount"),
                                Follower = obj.optInt("followerCount"),
                                userImage = obj.optString("userImage")
                            )
                            followList.add(data)
                        }
                    }
                    // ２－４－２ー３．フォロワー情報一覧が存在するかチェックする
                    val followerList = mutableListOf<UserRowData>()
                    val followerArray = json.optJSONArray("followerList") ?: JSONArray()
                    if (followerArray.length() > 0) {
                        // ２－４－２ー３－１．フォロワー情報が存在する場合、以下の処理を繰り返す
                        for (i in 0 until followerArray.length()) {
                            val obj = followerArray.getJSONObject(i)
                            // ２－４－２ー３－１－１．フォロワー情報をリストに格納する
                            val data = UserRowData(
                                userId = obj.optString("userId"),
                                userName = obj.optString("userName"),
                                Follow = obj.optInt("followCount"),
                                Follower = obj.optInt("followerCount"),
                                userImage = obj.optString("userImage")
                            )
                            followerList.add(data)
                        }
                    }

                    // ２－４－２－４．followRecycleにフォロー情報リストまたはフォロワー情報リストをセットする
                    if (section == "1") {
                        // ２－４－２－４ー１．区分がフォローの場合、フォロー情報リストセットする
                        recyclerView.adapter = UserAdapter(followList, applicationContext)
                    } else {
                        // ２－４－２－４ー２．区分がフォロワーの場合、フォロワー情報リストセットする
                        recyclerView.adapter = UserAdapter(followerList, applicationContext)
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
            /*


				２－４－２ー２－１．フォロー情報が存在する場合、以下の処理を繰り返す
					２－４－２ー２－１－１．フォロー情報をリストに格納する

			２－４－２ー３．フォロワー情報一覧が存在するかチェックする
				２－４－２ー３－１．フォロワー情報が存在する場合、以下の処理を繰り返す
					２－４－２ー３－１－１．フォロワー情報をリストに格納する

			２－４－２－４．followRecycleにフォロー情報リストまたはフォロワー情報リストをセットする
				２－４－２－４ー１．区分がフォローの場合、フォロー情報リストセットする

				２－４－２－４ー２．区分がフォロワーの場合、フォロワー情報リストセットする



             */

