package com.example.whisperclient

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


// ３．アダプター作成（クラス名：WhisperAdapter［引数：MutableList<WhisperRowData>、Context ］）
// ３－１．RecyclerView.Adapterクラスを継承する。
class WhisperAdapter(private val dataset: MutableList<WhisperRowData>,private val context: Context) : RecyclerView.Adapter<WhisperAdapter.ViewHolder>() {

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

        // datasetから現在の行のデータを取得する
        val item = dataset[position]

        // 取得したデータを画面に表示している項目を設定する。
        // ３－４－１．ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、ささやき）をセットする
        holder.userName.text = dataset[position].userName
        holder.whisper.text = dataset[position].whisperText
        Log.d("aa",holder.whisper.text.toString())


        holder.goodImage.setImageResource(
            if (item.goodImage) R.drawable.star_on else R.drawable.star_off
        )

        // ３－４－３．userImageのクリックイベントリスナーを生成する
        holder.userImage.setOnClickListener {
            // ３－４－３－１．Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する。
            val intent = Intent(context, UserInfoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK

                // ３－４－３－２．インテントに対象行のユーザIDをセットする
                putExtra("userId", item.userId)
            }
            // ３－４－３－３．ユーザ情報画面に遷移する
            context.startActivity(intent)
        }


        // ３－４－４．goodImageのクリックイベントリスナーを生成する
        holder.goodImage.setOnClickListener {
//            // ３－４－２．イイねフラグに併せて、いいね画像を切り替える。
//            item.goodImage = !item.goodImage
//
//            holder.goodImage.setImageResource(
//                if (item.goodImage) R.drawable.star_on else R.drawable.star_off
//            )
//            if (item.goodImage) {
//                holder.goodImage.setImageResource(R.drawable.star_on)
//                Log.d("画像切り替え", "いいねをしました。")
//            } else {
//                holder.goodImage.setImageResource(R.drawable.star_off)
//                Log.d("画像切り替え", "いいねを解除しました。")
//            }
//
//            Toast.makeText(context, "イイねの画像をクリックしました！！！", Toast.LENGTH_SHORT).show()
//


            // ３－４－４－１．イイね管理処理APIをリクエストして入力した対象行のささやきのイイねの登録・解除を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ(APIに渡したいパラメータを設定)
            val requestBodyJson = JSONObject().apply {
                put("userId", MyApplication.getInstance().loginUserId)
                put("whisperNo", item.whisperId)
                put("goodFlg", if (item.goodImage) false else true)
            }
            Log.d("ぱらめーたのなまえかくにん", requestBodyJson.toString())

            // BodyのデータをAPIに送るためにRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
            // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "goodCtl.php")
                .post(requestBody) // リクエストするパラメータ設定
                .build()

            client.newCall(request).enqueue(object : Callback {
                // ３－４－４－１－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val bodyStr = response.body?.string().orEmpty()
                    (context as? android.app.Activity)?.runOnUiThread {
                        val json = JSONObject(bodyStr)
                        val status = json.optString("status", json.optString("result", "error"))


                        // ３－４－４－１－１－１．JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                        if (status != "success") {
                            val errMsg = json.optString("error", "いいねに失敗しました。")
                            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT)
                                .show()
                            return@runOnUiThread
                        }
                        // ３－４－４－１－１－２．対象行のいいねのレイアウトを切り替えるため、いいねフラグの変更を通知する。
                        val currentPosition = holder.adapterPosition
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            item.goodImage = !item.goodImage
                            notifyItemChanged(currentPosition)
                        }

                    }
                }


                // ３－４－４－１－２．リクエストが失敗した時(コールバック処理)
                override fun onFailure(call: Call, e: IOException) {
                    // ３－４－４－１－２－１．エラーメッセージをトースト表示する
                    (context as? android.app.Activity)?.runOnUiThread {
                        Toast.makeText(context, "リクエストが失敗しました", Toast.LENGTH_SHORT)
                            .show()
                        return@runOnUiThread
                    }
                }
            })
        }
    }

    // ３－５．行数取得時（getItemCount処理）
    override fun getItemCount(): Int {
        // ３－５－１．行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataset.size
    }
}

