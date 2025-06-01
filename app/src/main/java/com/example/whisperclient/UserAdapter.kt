package com.example.whisperclient

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class UserAdapter(private val dataset: MutableList<UserRowData>,private val context: Context): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    // ３－２．ビューホルダー（内部クラス）
    class ViewHolder(item : View) : RecyclerView.ViewHolder(item){
        // ３－２－１．画面デザインで定義したオブジェクトを変数として宣言する。
        val userImage : ImageView
        val userName : TextView
        val follow : TextView
        val fwcnt : TextView
        val follower : TextView
        val fwrcnt : TextView

        init {
            userImage = item.findViewById(R.id.userImage)
            userName = item.findViewById(R.id.userNameText)
            follow = item.findViewById(R.id.followText)
            fwcnt = item.findViewById(R.id.followCntText)
            follower = item.findViewById(R.id.followerText)
            fwrcnt = item.findViewById(R.id.followerCntText)
        }
    }

    // ３－３．ビューホルダー生成時（onCreateViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ３－３－１．ユーザ行情報の画面デザイン（user_recycle_row）をViewHolderに設定し、戻り値にセットする。
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_recycle_row,parent,false)
        return ViewHolder(view)
    }

    // ３－４．ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ３－４－１．ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、フォロー数、フォロワー数）をセットする
        val row = dataset[position]         // 現在の行データを取り出す
        holder.userName.text = row.userName
        holder.fwcnt.text = row.Follow.toString()
        holder.fwrcnt.text = row.Follower.toString()

        // ３－４－２．userImageのクリックイベントリスナーを生成する
        holder.userImage.setOnClickListener {
            // ３－４－２－１．Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する。
            val  intent = Intent(context, UserInfoActivity::class.java).apply {
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