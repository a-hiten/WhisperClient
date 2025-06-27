package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import org.w3c.dom.Text
import java.io.IOException
import kotlin.text.Typography.section

// オーバーフローメニューを継承をする
class UserInfoActivity : OverflowMenuActivity() {

    // リストを保持する用
    private var currentDisplayUserId: String = ""
    //フォロー数表示用
    private lateinit var fwrnt: TextView

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

        // ２－２．インテント(前画面)から対象ユーザIDを取得する
        val intentUserId = intent.getStringExtra("userId")

        // ２－３．ユーザささやき情報取得API　共通実行メソッドを呼び出す
        getUserWhisperInfo(
            intentUserId,
            userName,
            profile,
            fwrnt, fwrcnt,
            follow, follower,
            whisper,
            good,
            recyclerView
        )

        // ２－４．radioGroupのチェック変更イベントリスナーを作成する
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { _, _ ->
            // ２－４－１．ユーザささやき情報取得API　共通実行メソッドを呼び出してラジオボタンにあった情報を取得する
            getUserWhisperInfo(
                intentUserId,
                userName,
                profile,
                fwrnt, fwrcnt,
                follow, follower,
                whisper,
                good,
                recyclerView
            )
        }

        whisper.isChecked = true

        // ２－５．followCntTextのクリックイベントリスナーを作成する
        fwrnt.setOnClickListener {
            // ２－５－１．インテントに対象ユーザIDと文字列followをセットする
            val intent = Intent(this, FollowListActivity::class.java)
            intent.putExtra("userId", currentDisplayUserId)
            intent.putExtra("mode", "follow")
            // ２－５－２．フォロー一覧画面に遷移する
            startActivity(intent)
            Log.d("フォロー","フォローだがね")
        }
        // ２－６．followerCntTextのクリックイベントリスナーを作成する
        fwrcnt.setOnClickListener {
            // ２－６－１．インテントに対象ユーザIDと文字列followerをセットする
            val intent = Intent(this, FollowListActivity::class.java)
            intent.putExtra("userId", currentDisplayUserId)
            intent.putExtra("mode", "follower")
            // ２－６－２．フォロー一覧画面に遷移する
            startActivity(intent)
            Log.d("フォロワー","フォロワーだがね")
        }

        // ２－７．followButtonのクリックイベントリスナーを作成する
        val followButton = findViewById<Button>(R.id.followButton)
        followButton.text = "フォローする"

        followButton.setOnClickListener {
            followButton.text = if (followButton.text == "フォローする") "フォロー解除" else "フォローする"
        }

        // ２－７．followButtonのクリックイベントリスナーを作成する
        followButton.setOnClickListener {
            // 今のボタンの状態からフォローかどうかを確認する
            val followButtonText = followButton.text.toString()
            // 現在の画面のユーザ
            val followerCount = fwrcnt.text.toString().toIntOrNull() ?: 0


            // ２－７－１．未フォローの場合
            if (followButtonText == "フォローする") {
                // ２－７－１－１．フォロー管理処理API　共通実行メソッドを呼び出してフォロー登録を行う。
                postFollowManagement(currentDisplayUserId, true)
                // ２－７－１－２．followButtonの文言をフォロー済みの内容に変更する。
                followButton.text = "フォロー解除"

                fwrcnt.text = (followerCount + 1).toString()

                Log.d("ふぉろー","ふぉろーしたよ")

            // ２－７－２．フォロー済みの場合
            } else if (followButtonText == "フォロー解除") {
                // ２－７－２－１．フォロー管理処理API　共通実行メソッドを呼び出してフォロー解除を行う。
                postFollowManagement(currentDisplayUserId, false)
                // ２－７－２－２．followButtonの文言を未フォローの内容に変更する。
                followButton.text = "フォローする"

                fwrcnt.text = (if (followerCount > 0) followerCount - 1 else 0).toString()

                Log.d("かいじょ","かいじょしたよー")
            }
        }
    }

    // ３．ユーザささやき情報取得API　共通実行メソッド
    private fun getUserWhisperInfo(
        intentId: String?,
        userName: TextView,
        profile: TextView,
        fwrnt: TextView,
        fwrcnt: TextView,
        followText: TextView,
        followerText: TextView,
        whisper: RadioButton,
        good: RadioButton,
        recyclerView: RecyclerView
    ) {

        // ３－１．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId

        // ３－２．intentUserIdがNULLかチェックする。
        currentDisplayUserId = intentId ?: loginUserId

        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", currentDisplayUserId)
            put("loginUserId", loginUserId)
        }

        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "userWhisperInfo.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println("なかみだよ～$body")

                runOnUiThread {
                    val json = JSONObject(body)
                    val status = json.optString("status", json.optString("result", "error"))

                    // ３－３－１－１．エラーハンドリング
                    if (status != "success") {
                        val errorMsg = json.optString("error", "エラーが発生しました。")
                        Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }


                    val whisperList = json.optJSONArray("whisperList") ?: JSONArray()
                    val whispers = mutableListOf<WhisperRowData>()
                    for (i in 0 until whisperList.length()) {
                        val obj = whisperList.getJSONObject(i)
                        val whisper = WhisperRowData(
                            userId = obj.optInt("userId"),
                            userName = obj.optString("userName"),
                            whisperId = obj.optInt("followCount"),
                            whisperText = obj.optString("content"),
                            userImage = obj.optString("followerCount"),
                            goodImage = obj.optBoolean("userImage")
                        )
                        whispers.add(whisper)
                    }

                    val goodList = json.optJSONArray("goodList") ?: JSONArray()
                    val goods = mutableListOf<GoodRowData>()
                    for (i in 0 until goodList.length()) {
                        val obj = goodList.getJSONObject(i)
                        val good = GoodRowData(
                            userId = obj.optString("userId"),
                            userName = obj.optString("userName"),
                            whisper = obj.optString("content"),
                            gcnt = obj.optInt("gcnt"),
                            userImage = obj.optString("userImage")
                        )
                        goods.add(good)
                    }
                    // ２－２－３－１－２－１－２．ユーザ行情報のアダプターにユーザ情報リストをセットする
                    val recyclerView = findViewById<RecyclerView>(R.id.userRecycle)
                    // ２－２－３－１－２－１－３．searchRecycleを表示する
                    recyclerView.layoutManager = LinearLayoutManager(this@UserInfoActivity)

                    // らじおぼたんのやつ
                    val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
                    // 例えば、チェックに応じて処理を分けたい場合
                    when (radioGroup.checkedRadioButtonId) {
                        // ３－３－１－２．ささやき情報取得
                        R.id.whisperRadio -> {
                            // ささやきラジオボタンが選択された時の処理
                            Log.d("RadioGroup", "うぃすぱー")
                            recyclerView.adapter = WhisperAdapter(whispers, this@UserInfoActivity)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                        // ３－３－１－３．イイね情報取得
                        R.id.goodInfoRadio -> {
                            // いいねラジオボタンが選択された時の処理
                            Log.d("RadioGroup", "ぐっど")
                            recyclerView.adapter = GoodAdapter(goods, this@UserInfoActivity)
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }

                    // ３－３－４．取得したデータを各オブジェクトにセットする
                    userName.text = json.optString("userName", "")
                    profile.text = json.optString("profile", "")
                    fwrnt.text = json.optInt("followCount", 0).toString()
                    fwrcnt.text = json.optInt("followerCount", 0).toString()
                    // ３－３－５．フォローボタン制御
                    val followButton = findViewById<Button>(R.id.followButton)
                    if (currentDisplayUserId == loginUserId) {
                        followButton.visibility = View.GONE
                    } else {
                        followButton.visibility = View.VISIBLE
                        followButton.text = if (json.optBoolean("userFollowFlg", false)) "フォロー解除" else "フォローする"
                    }
                }
            }

            // ２－４－２．リクエストが失敗した時(コールバック処理)
            override fun onFailure(call: Call, e: IOException) {
                // ２－４－２－１．エラーメッセージをトースト表示する
                runOnUiThread {
                    Toast.makeText(applicationContext, "リクエストに失敗しました。", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // ４．フォロー管理処理API　共通実行メソッド
    private fun postFollowManagement(followUserId: String, followFlg: Boolean) {
        // ４－１．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId

        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

        // Bodyのデータ（APIに渡したいパラメータを設定）
        val requestBodyJson = JSONObject().apply {
            put("userId", loginUserId)
            put("followUserId", followUserId)
            put("followFlg", followFlg)
        }
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "followCtl.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string() ?: return
                val res = JSONObject(bodyStr)
                val status = res.optString("status", res.optString("result", "error"))
                if (status != "success") {
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            res.optString("error", "フォロー失敗"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // ２－４－２－１．エラーメッセージをトースト表示する
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "リクエストに失敗しました。",
                        Toast.LENGTH_SHORT
                    ).show()
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
１．OverFlowMenuActivityクラスを継承する

２．画面生成時（onCreate処理）
	２－１．画面デザインで定義したオブジェクトを変数として宣言する

	２－２．インテント(前画面)から対象ユーザIDを取得する

	２－３．ユーザささやき情報取得API　共通実行メソッドを呼び出す

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

		3．ユーザささやき情報取得API　共通実行メソッド
          引数　　intentUserId		String
                userNameText		TextView
                profileText			TextView
                followCntText		TextView
                followerCntText		TextView
                followBtn			Button
                userRecycle			RecyclerView
                radioGroup			RadioGroup
        戻り値　　なし

        ３－１．グローバル変数のログインユーザーIDを取得。

        ３－２．intentUserIdがNULLかチェックする。
            ３－２－１．NULLの場合、ログインユーザーIDを表示対象のユーザIDとして変数に保持する。

            ３－２－２．NULLではない場合、intentUserIDを表示対象のユーザIDとして変数に保持する。

        ３－３．ユーザささやき情報取得APIをリクエストして対象ユーザのささやき情報とそのユーザがイイねしている情報取得を行う
            ３－３ー１．正常にレスポンスを受け取った時(コールバック処理)
                ３－３－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる

                ３－３－１－２．ささやき情報一覧が存在するかチェックする
                    ３－３－１－２－１．ささやき情報が存在する場合、以下の処理を繰り返す
                        ３－３－１－２－１－１．ささやき情報をリストに格納する

                ３－３－１－３．イイね情報一覧が存在するかチェックする
                    ３－３－１－３－１．いいね情報が存在する場合、以下の処理を繰り返す
                        ３－３－１－３－１－１．いいね情報をリストに格納する


                ３－３－４．取得したデータを各オブジェクトにセットする

                ３－３－５．取得したユーザIDとログインユーザIDを比較する。
                    ３－３－５－１．一致した場合、フォローボタンを非表示にする。

                    ３－３－５－２．一致しなかった場合、followButtonを表示する。
                        ３－３－５－２－１．ユーザーフォローフラグをチェックする
                            ３－３－５－２－１．フォロー済みの場合、followButtonのテキストを「フォロー解除」に変更する。

                            ３－３－５－２－２．未フォローの場合、followButtonのテキストを「フォローする」に変更する。

                ３－３－６．選択されたラジオボタンにあったリストをuserRecycleにセットする
                    ３－３－６－１．whisperRadioが選択されている場合、ささやき情報リストをセットする

                    ３－３－６－２．goodInfoRadioが選択されている場合、いいね情報リストをセットする

            ３－４．リクエストが失敗した時(コールバック処理)
                ３－４－１．エラーメッセージをトースト表示する

          4. フォロー管理処理API　共通実行メソッド
            引数　　　followUserId	String
            　　　　　followFlg		Boolean
            戻り値   なし


            ４－１．グローバル変数のログインユーザーIDを取得。

            ４－２．フォロー管理処理APIをリクエストして対象ユーザのフォロー登録または解除を行う
                ４－２－１．正常にレスポンスを受け取った時(コールバック処理)
                    ４－２－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる

                ４－２－２．リクエストが失敗した時(コールバック処理)
                    ４－２－２－１．エラーメッセージをトースト表示する
 */