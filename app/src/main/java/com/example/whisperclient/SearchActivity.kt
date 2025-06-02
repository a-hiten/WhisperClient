package com.example.whisperclient

import android.os.Bundle
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









        // ２－２－４．リクエストが失敗した時(コールバック処理)

        // ２－２－４－１．エラーメッセージをトースト表示する

    }









    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}