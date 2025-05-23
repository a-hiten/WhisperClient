package com.example.whisperclient

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class OverflowMenuActivity : AppCompatActivity() {
    //    ２．オーバーフローメニュー作成（アクティビティ名：OverflowMenuActivity）
    //    ２－１．オプションメニュー生成時（onCreateOptionsMenu処理）
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //    ２－１－１．インフレータにオーバーフローメニューのデザインを設定する
        menuInflater.inflate(R.menu.menu_item, menu)
        //    ２－１－２．戻り値にtrueをセットする
        return true
    }


    // ＊thisだとその画面でしか使えないからactivityに変更した
    //    ２－２．オプションメニューアイテム選択時（onOptionsItemSelected処理）
    companion object {
        fun handleMenuItemSelected(activity: AppCompatActivity, item: MenuItem): Boolean {
            return when (item.itemId) {
                //    ２－２－１．受け取ったMenuItemがtimelineの時
                R.id.timeline -> {
                    //    ２－２－１－１．タイムライン画面に遷移する
                    val intent = Intent(activity, TimelineActivity::class.java)
                    activity.startActivity(intent)
                    //    ２－２－１－２．戻り値にtrueをセットする
                    true
                }
                //    ２－２－２．受け取ったMenuItemがsearchの時
                R.id.search -> {
                    // ２－２－２－１．検索画面に遷移する
                    val intent = Intent(activity, SearchActivity ::class.java)
                    activity.startActivity(intent)
                    // ２－２－２－２．戻り値にtrueをセットする
                    true
                }
                //    ２－２－３．受け取ったMenuItemがwhisperの時、
                R.id.whisper -> {
                    //    ２－２－３－１．ささやき登録画面に遷移する
                    val intent = Intent(activity, WhisperActivity::class.java)
                    activity.startActivity(intent)
                    //    ２－２－３－２．戻り値にtrueをセットする
                    true
                }
                //    ２－２－４．受け取ったMenuItemがmyprofileの時
                R.id.myprofile ->{
                    //    ２－２－４－１．インテントにログインユーザIDをセットする
                    val intent = Intent(activity, UserInfoActivity::class.java)
                    /*
                    loginUserIdをUser_Idという名前で次の名前に渡す準備をしてる
                    putExtra：インテントに追加情報を詰め込むためのもの
                     */
                    intent.putExtra("USER_ID", MyApplication.getInstance().loginUserId)
                    //    ２－２－４－２．ユーザ情報画面に遷移する
                    activity.startActivity(intent)
                    //    ２－２－４－３．戻り値にtrueをセットする
                    true
                }
                //    ２－２－５．受け取ったMenuItemがprofileeditの時
                R.id.profileedit -> {
                    val intent = Intent(activity, UserEditActivity::class.java)
                    //    ２－２－５－１．インテントにログインユーザIDをセットする
                    intent.putExtra("USER_ID", MyApplication.getInstance().loginUserId)
                    //    ２－２－５－２．プロフィール編集画面に遷移する
                    activity.startActivity(intent)
                    //    ２－２－5－３．戻り値にtrueをセットする
                    true
                }
                //    ２－２－６．受け取ったMenuItemがlogoutの時
                R.id.logout -> {
                    //    ２－２－６－１．グローバル変数loginUserIdに空文字を格納する
                    /*
                    ログイン処理を行った際に、ログインしているユーザIDを空にしてリセットしている
                     */
                    MyApplication.getInstance().loginUserId = ""

                    val intent = Intent(activity, LoginActivity::class.java)
                    //    ２－２－６－２．インテントに前画面情報をクリアするフラグを追加する
                    /*
                    Intent.FLAG_ACTIVITY_CLEAR_TOP：ログイン画面が履歴にあれば、それより上にある画面をすべて削除し、その画面を再利用する
                    Intent.FLAG_ACTIVITY_NEW_TASK：新しいタスクとしてこの画面を起動する
                     */
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    //    ２－２－６－３．ログイン画面に遷移する
                    activity.startActivity(intent)
                    //    ２－２－６－４． 現在のアクティビティを終了する
                    activity.finish()
                    //    ２－２－６－５． 戻り値にtrueをセットする
                    true
                }
                //    ２－２－７．それ以外を受け取った時
                //    ２－２－７－１．親クラスの処理を呼び出してデフォルトの動作を実行
                else -> activity.onOptionsItemSelected(item)
            }
        }
    }

}
