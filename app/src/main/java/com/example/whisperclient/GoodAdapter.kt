package com.example.whisperclient

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ３．アダプター作成（クラス名：GoodAdapter［引数：MutableList<GoodRowData>、Context ］）
//	３－１．RecyclerView.Adapterクラスを継承する。
class GoodAdapter(private val dataset: MutableList<GoodRowData>, private val context: Context) : RecyclerView.Adapter<GoodAdapter.ViewHolder>(){

    // ３－２．ビューホルダー（内部クラス）
    class ViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        //	３－２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val userImage : ImageView
        val userName : TextView
        val whisper : TextView
        val good : TextView
        val gcnt : TextView

        init {
            userImage = item.findViewById(R.id.userImage)
            userName = item.findViewById(R.id.userNameText)
            whisper = item.findViewById(R.id.whisperText)
            good = item.findViewById(R.id.goodText)
            gcnt = item.findViewById(R.id.goodCntText)
        }
    }

    // ３－３．ビューホルダー生成時（onCreateViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //	３－３－１．いいね行情報の画面デザイン（good_recycle_row）をViewHolderに設定し、戻り値にセットする。
        val view = LayoutInflater.from(parent.context).inflate(R.layout.good_recycle_row, parent,false)
        return ViewHolder(view)
    }



    // ３－４．ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //	３－４－１．ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、ささやき内容、いいね数）をセットする
        val row = dataset[position]
        holder.userName.text = row.userName
        holder.whisper.text = row.whisper
        holder.gcnt.text = row.gcnt.toString()

        // ３－４－２．userImageのクリックイベントリスナーを生成する
        holder.userImage.setOnClickListener {
            // ３－４－２－１．Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する。
            val intent = Intent(context, UserInfoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // ３－４－２－２．インテントに対象行のユーザIDをセットする
                putExtra("userId", row.userId)
            }
            // ３－４－２－３．ユーザ情報画面に遷移する
            context.startActivity(intent)
        }
    }

    // ３－５．行数取得時（getItemCount処理）
    override fun getItemCount(): Int {
        // ３－５－１．行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataset.size
    }
}