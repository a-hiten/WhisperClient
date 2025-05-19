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
class WhisperActivity : OverflowMenuActivity() {

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

        // ２－２．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId

        // ２－３．whisperButtonのクリックイベントリスナーを作成する
        whisperButton.setOnClickListener {
            // ２－３－１．入力項目が空白の時、エラーメッセージをトースト表示して処理を終了させる
            val whisperEdit = whisperEdit.text.toString()
            // isBlankで未入力チェックしてる。
            // whisperEditが空ならトーストを出す
            if (whisperEdit.isBlank()) {
                // メッセージ内容：ささやく内容を入力してください
                Toast.makeText(
                    applicationContext,
                    "ささやく内容を入力してください。",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // ２－３－２．ささやき登録処理APIをリクエストして、入力したささやきの登録処理を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("whisperEdit", whisperEdit)

            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成

            val request = Request.Builder()
                .url("http://10.0.2.2/TestAPI/test_php/whisperAdd.php")
//                .url("http://10.0.2.2/フォルダ名/ファイル名)   //10.0.2.2の後を自分の環境に変更してください
                .post(requestBody)
                .build()

            // リクエスト送信（非同期処理）
            client.newCall(request!!).enqueue(object : Callback {
                // ２－３－２ー１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val bodyStr = response.body?.string().orEmpty()
                    runOnUiThread {
                        if (!response.isSuccessful) {
                            Toast.makeText(applicationContext, "サーバーエラーが発生しました", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        val json = JSONObject(bodyStr)
                        val status = json.optString("status", "error")

                        // ２－３－２ー１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                        if (status != "success") {
                            val errMsg = json.optString("error", "登録に失敗しました")
                            Toast.makeText(applicationContext, errMsg, Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        // ２－３－２ー１－２．タイムライン画面に遷移する
                        val intent = Intent(this@WhisperActivity, TimelineActivity::class.java)
                        intent.putExtra("loginUserId", loginUserId)
                        startActivity(intent)

                        // ２－３－２ー１－３．自分の画面を閉じる
                        finish()
                    }
                }

                // ２－３－２ー２．リクエストが失敗した時(コールバック処理)
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

            // ２－４．cancelButtonのクリックイベントリスナーを作成する
            cancelButton.setOnClickListener {
                // ２－４－１．自分の画面を閉じる
                finish()
            }
        }
    }

    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}
