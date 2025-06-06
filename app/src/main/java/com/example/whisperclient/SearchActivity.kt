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
                put("section",user)
                put("section", whisper)
                put("string", searchText)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url("https://click.ecc.ac.jp/ecc/k_hosoi/WhisperSystem/search.php")
//            .url("http://10.0.2.2/自分の環境に合わせる")   //10.0.2.2の後を自分の環境に変更してください
                .post(requestBody)
                .build()

            // リクエスト送信（非同期処理）
            client.newCall(request).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    // ログ
                    println("なかみだよ～"+ body)

                    runOnUiThread {                     // APIから取得したJSON文字列をJSONオブジェクトに変換
                        val json = JSONObject(body)
                        val status = json.optString("status", json.optString("result", "error"))

                        if (status != "success") {
                            val errorMsg = json.optString("error", "エラーが発生しました。")
                            runOnUiThread {
                                Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
                                return@runOnUiThread
                            }
                        }
                        // ログ
//                        Log.d("Timeline", "loginUserId = $loginUserId")
//                        Log.d("Timeline", "whispers length = ${whispers.length()}")

                    }
                }


                // ２－２－４．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    // ２－２－４－１．エラーメッセージをトースト表示する
                    runOnUiThread {
                        Toast.makeText(applicationContext, "リクエストに失敗しました。", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })


/*






    // BodyのデータをAPIに送るためにRequestBody形式に加工
    val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
    // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
    val request = Request.Builder()
        .url("https://click.ecc.ac.jp/ecc/k_hosoi/WhisperSystem/search.php")
//                .url("http://10.0.2.2/TestAPI/test_php/search.php")
//                    .url("http://10.0.2.2/自分の環境に合わせる")   //10.0.2.2の後を自分の環境に変更してください

        .post(requestBody) // リクエストするパラメータ設定
        .build()

    client.newCall(request).enqueue(object : Callback {
        // ２－２－３－１．正常にレスポンスを受け取った時(コールバック処理
        override fun onResponse(call: Call, response: Response) {
            val bodyStr = response.body?.string().orEmpty()
            runOnUiThread {
                val json = JSONObject(bodyStr)
                val status = json.optString("status", json.optString("result", "error"))

                // ２－２－３－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                if (status != "success") {
                    val errMsg = json.optString("error", "検索に失敗しました。。")
                    Toast.makeText(applicationContext, errMsg, Toast.LENGTH_SHORT)
                        .show()
                    return@runOnUiThread
                }

                // ２－２－３－１－２．ラジオボタンの選択肢に合わせて、以下の処理を繰り返すアダプターにリストをセットする
                if (section == "1") {
                    // ２－２－３－１－２－１．ラジオボタンがuserRadioを選択している時
                    val userList = mutableListOf<UserRowData>()
                    val array = json.optJSONArray("users")
                    if (array != null) {
                        // ２－２－３－１－２－１－１．ユーザ行情報一覧が存在する間、以下の処理を繰り返す
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            // ２－２－３－１－２－１－１－１．ユーザ情報をリストに格納する
                            val userData = UserRowData(
                                item.optString("userId"),
                                item.optString("userName"),
                                item.optString("userImage")
                            )
                            userList.add(userData)
                        }
                    }
                    // ２－２－３－１－２－１－２．ユーザ行情報のアダプターにユーザ情報リストをセットする
                    val adapter = UserAdapter(userList, applicationContext)
                    searchRecycle.adapter = adapter
                    // ２－２－３－１－２－１－３．searchRecycleを表示する
                    searchRecycle.visibility = RecyclerView.VISIBLE
                } else {
                    // ２－２－３－１－２－２．ラジオボタンがwhisperRadioを選択している時
                    val whisperList = mutableListOf<GoodRowData>()
                    val array = json.optJSONArray("whispers")
                    if (array != null) {
                        // ２－２－３－１－２－２－１．いいね行情報一覧が存在する間、以下の処理を繰り返す
                        for (i in 0 until array.length()) {
                            val item = array.getJSONObject(i)
                            // ２－２－３－１－２－２－１－１．いいね情報をリストに格納する
                            val goodData = GoodRowData(
                                item.optString("userId"),
                                item.optString("userName"),
                                item.optString("userImage"),
                                item.optString("whisper"),
                                item.optInt("goodCnt")
                            )
                            whisperList.add(goodData)
                        }
                    }
                    // ２－２－３－１－２－２－２．いいね行情報のアダプターにいいね情報リストをセットする
                    val adapter = GoodAdapter(whisperList, applicationContext)
                    searchRecycle.adapter = adapter
                    // ２－２－３－１－２－２－３．searchRecycleを表示する
                    searchRecycle.visibility = RecyclerView.VISIBLE
                }
            }
        }
        // １－２－３－１ー４．自分の画面を閉じる
        finish()
    }





        // ２－２－４．リクエストが失敗した時(コールバック処理)
        override fun onFailure(call: Call, e: IOException) {
            runOnUiThread {
                // ２－２－４－１．エラーメッセージをトースト表示する
                Toast.makeText(
                    applicationContext,
                    "リクエストが失敗しました: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    })


 */














    // ２－２－２．ラジオボタンの選択肢を変数に保持する。

            // ２－２－３．検索結果取得APIをリクエストして検索キーワードに該当する情報取得を行う
            // ２－２－３－１．正常にレスポンスを受け取った時(コールバック処理)
            // ２－２－３－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる

            // ２－２－３－１－２．ラジオボタンの選択肢に合わせて、以下の処理を繰り返すアダプターにリストをセットする
            // ２－２－３－１－２－１．ラジオボタンがuserRadioを選択している時
            // ２－２－３－１－２－１－１．ユーザ行情報一覧が存在する間、以下の処理を繰り返す
            // ２－２－３－１－２－１－１－１．ユーザ情報をリストに格納する

            // ２－２－３－１－２－１－２．ユーザ行情報のアダプターにユーザ情報リストをセットする

            // ２－２－３－１－２－１－３．searchRecycleを表示する

            // ２－２－３－１－２－２．ラジオボタンがwhisperRadioを選択している時
            // ２－２－３－１－２－２－１．いいね行情報一覧が存在する間、以下の処理を繰り返す
            // ２－２－３－１－２－２－１－１．いいね情報をリストに格納する

            // ２－２－３－１－２－２－２．いいね行情報のアダプターにいいね情報リストをセットする

            // ２－２－３－１－２－２－３．searchRecycleを表示する
        }













    }









    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}