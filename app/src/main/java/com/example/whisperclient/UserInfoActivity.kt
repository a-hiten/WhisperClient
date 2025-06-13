package com.example.whisperclient

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
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
        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する
        val recyclerView = findViewById<RecyclerView>(R.id.userRecycle)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ２－２．インテント(前画面)から対象ユーザIDを取得する
        val userId = intent.getStringExtra("userId")

        // ２－３．ユーザささやき情報取得API　共通実行メソッドを呼び出す
        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようなデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", userId)
            put("loginUserId", userId)
        }
        // BodyのデータをAPIに送る為にRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

        // Requestを作成
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "userWhisperInfo.php")
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




        /*






２－４．radioGroupのチェック変更イベントリスナーを作成する
	２－４－１．ユーザささやき情報取得API　共通実行メソッドを呼び出してラジオボタンにあった情報を取得する

２－５．followCntTextのクリックイベントリスナーを作成する
	２－５－１．インテントに対象ユーザIDと文字列followをセットする

	２－５－２．フォロー一覧画面に遷移する

２－６．followerCntTextのクリックイベントリスナーを作成する
	２－６－１．インテントに対象ユーザIDと文字列followerをセットする

	２－６－２．フォロー一覧画面に遷移する

２－７．followButtonのクリックイベントリスナーを作成する
	２－７－１．未フォローの場合
		２－７－１－１．フォロー管理処理API　共通実行メソッドを呼び出してフォロー登録を行う。

		２－７－１－２．followButtonの文言をフォロー済みの内容に変更する。

	２－７－２．フォロー済みの場合
		２－７－２－１．フォロー管理処理API　共通実行メソッドを呼び出してフォロー解除を行う。

		２－７－２－２．followButtonの文言を未フォローの内容に変更する。

         */

    }


    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}