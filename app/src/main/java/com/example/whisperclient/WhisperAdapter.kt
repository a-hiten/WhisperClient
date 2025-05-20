package com.example.whisperclient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ３．アダプター作成（クラス名：WhisperAdapter［引数：MutableList<WhisperRowData>、Context ］）
// ３－１．RecyclerView.Adapterクラスを継承する。
class WhisperAdapter: RecyclerView.Adapter<WhisperAdapter.ViewHolder>() {

    // ３－２．ビューホルダー（内部クラス）
    class ViewHolder(item: View) :RecyclerView.ViewHolder(item){

        // ３－２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val userImage : ImageView
        val userName : TextView
        val whisper : TextView
        val goodImage : ImageView

        init {
            userImage = item.findViewById(R.id.userImage)
            userName = item.findViewById(R.id.userNameText)
            whisper = item.findViewById(R.id.whisperText)
            goodImage = item.findViewById(R.id.goodImage)
        }

    }

    // ３－３．ビューホルダー生成時（onCreateViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ３－３－１．ささやき行情報の画面デザイン（whisper_recycle_row）をViewHolderに設定し、戻り値にセットする。
        val view = LayoutInflater.from(parent.context).inflate(R.layout.whisper_recycle_row, parent, false)
        return ViewHolder(view)
    }


    // ３－４．ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        val item = dataset[position]
//
//        // ３－４－１．ユーザ名、ささやき内容をセット
//        holder.userName.text = item.userName
//        holder.whisper.text = item.whisperText

//        val item = dataset[position]
        // ３－４－１．ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、ささやき）をセットする
//        holder.userName.text = item.userName



        /*
        ３－４－２．イイねフラグに併せて、いいね画像を切り替える。

        ３－４－３．userImageのクリックイベントリスナーを生成する
        ３－４－３－１．Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する。

        ３－４－３－２．インテントに対象行のユーザIDをセットする

        ３－４－３－３．ユーザ情報画面に遷移する

        ３－４－４．goodImageのクリックイベントリスナーを生成する
        ３－４－４－１．イイね管理処理APIをリクエストして入力した対象行のささやきのイイねの登録・解除を行う
        ３－４－４－１－１．正常にレスポンスを受け取った時(コールバック処理)
        ３－４－４－１－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる

        ３－４－４－１－１－２．対象行のいいねのレイアウトを切り替えるため、いいねフラグの変更を通知する。

        ３－４－４－１－２．リクエストが失敗した時(コールバック処理)
        ３－４－４－１－２－１．エラーメッセージをトースト表示する

         */

        TODO("Not yet implemented")
    }




    // ３－５．行数取得時（getItemCount処理）
    override fun getItemCount(): Int {
        // ３－５－１．行リストの件数（データセットのサイズ）を戻り値にセットする
        TODO("Not yet implemented")
    }
}

