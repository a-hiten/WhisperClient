package com.example.whisperclient

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
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
        val recyclerView = findViewById<RecyclerView>(R.id.timelineRecycle)      // listの内容はささやき行情報を参照

        // ２－２．グローバル変数のログインユーザーIDを取得。
        val loginUserId = MyApplication.getInstance().loginUserId


        /*
        ２－３．タイムライン情報取得APIをリクエストしてログインユーザが確認できるささやき情報取得を行う
        ２－３－１．正常にレスポンスを受け取った時(コールバック処理)
        ２－２－３－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる

        ２－２－３－２．ささやき情報一覧が存在する間、以下の処理を繰り返す
        ２－２－３－２－１．ささやき情報をリストに格納する

        ２－２－３－３．timelineRecycleにささやき情報リストをセットする

         */

        // ２－３－２．リクエストが失敗した時(コールバック処理)
//        override fun onFailure(call: Call, e: IOException) {
//            runOnUiThread {
//                // ２－３－２－１．エラーメッセージをトースト表示する
//                Toast.makeText(
//                    applicationContext,
//                    "リクエストが失敗しました: ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }








    }



    // オーバーフローメニューを選んだ時に共通処理を呼び出す。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OverflowMenuActivity.handleMenuItemSelected(this,item) || super.onOptionsItemSelected(item)
    }
}