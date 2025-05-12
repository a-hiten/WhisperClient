package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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
class WhisperActivity : AppCompatActivity() {
    // ２．画面生成時（onCreate処理）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_whisper)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // ２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val whisperEdit = findViewById<EditText>(R.id.whisperEdit)      // 入力できるとこ
        val whisperButton = findViewById<Button>(R.id.whisperButton)    // Whisperボタン
        val cancelButton = findViewById<Button>(R.id.cancelButton)      // キャンセルボタン

//        ２－２．グローバル変数のログインユーザーIDを取得。

        // ２－３．whisperButtonのクリックイベントリスナーを作成する
        whisperButton.setOnClickListener {
            // ２－３－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            val whisperEdit = whisperEdit.text.toString()
            // isBlankで未入力チェックしてる。
            // whisperEditが空ならトーストを出す
            if (whisperEdit.isBlank()) {
                // メッセージ内容：ささやく内容を入力してください
                Toast.makeText(applicationContext, "ささやく内容を入力してください。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // １－２－２．ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("whisperEdit", whisperEdit)
//
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成

            val request = Request.Builder()
//                    .url("http://10.0.2.2/WhisperSystem/loginAuth.php")   //自分の環境に変更してください
                .url("http://10.0.2.2/TestAPI/test_php/loginAuth.php")

                .post(requestBody)
                .build()
            // リクエスト送信（非同期処理）
            client.newCall(request!!).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    println("レスポンスを受診しました: $body")

                    if (response.isSuccessful && body != null) {
                        // APIから取得したJSON文字列をJSONオブジェクトに変換
                        val json = JSONObject(body)
                        val status = json.optString("status", "error")

                        if (status == "success") {
                            // ログイン成功
                            // １－２－３－１－２．グローバル変数loginUserIdに作成したユーザIDを格納する
                            val loginUserId = json.optString("userId", "")

                            runOnUiThread {
                                //１－２－３－１－３．タイムライン画面に遷移する
                                val intent =
//                                    Intent(this@LoginActivity, TimelineActivity::class.java)
                                intent.putExtra("loginUserId", loginUserId)
                                startActivity(intent)

                                //　１－２－３－１－４．自分の画面を閉じる
                                finish()
                            }
                        } else {
                            // ログイン失敗
                            val errorMessage = json.optString("error", "ログインに失敗しました")
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "サーバーエラーが発生しました",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // １－２－２－２．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        // １－２－２－２－１．エラーメッセージをトースト表示する
                        Toast.makeText(
                            applicationContext,
                            "リクエストが失敗しました: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })


        }


//        ２－３－２．ささやき登録処理APIをリクエストして、入力したささやきの登録処理を行う
//        ２－３－２ー１．正常にレスポンスを受け取った時(コールバック処理)
//        ２－３－２ー１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
//        　　　　　　　　　　
//        ２－３－２ー１－２．タイムライン画面に遷移する
//
//        ２－３－２ー１－３．自分の画面を閉じる
//
//        ２－３－２ー２．リクエストが失敗した時(コールバック処理)

//        ２－３－２ー２－１．エラーメッセージをトースト表示する


        // ２－４．cancelButtonのクリックイベントリスナーを作成する
        cancelButton.setOnClickListener {
            // ２－４－１．自分の画面を閉じる
            finish()
        }
    }

    // オーバーフローメニューを表示するやつ
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //    ２－１－１．インフレータにオーバーフローメニューのデザインを設定する
        menuInflater.inflate(R.menu.menu_item, menu)
        //    ２－１－２．戻り値にtrueをセットする
        return true
    }

    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }



}