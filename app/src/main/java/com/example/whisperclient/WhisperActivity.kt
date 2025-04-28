package com.example.whisperclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


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
        val whisperText = findViewById<TextView>(R.id.whisperText)      // Whisperのテキスト
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
//
//        ２－４．cancelButtonのクリックイベントリスナーを作成する
//        ２－４－１．自分の画面を閉じる


    }
}