package com.example.whisperclient

import android.app.Application

// １．Applicationクラスを継承する
class MyApplication : Application() {

    // ２．グローバル変数loginUserIdを文字列型で宣言して、空文字で初期化する
    var loginUserId: String = ""        // 変更される想定だからvarにしている

    // ３．グローバル変数apiUrlを文字列型で宣言して、各チームのAPIのホームディレクトリで初期化する
    val  apiUrl: String = "http://click.ecc.ac.jp/ecc/whisperSystem/"

    // ４．Applicationクラスを他のクラスから参照できるように companion object を追加する
    companion object {
        // ４－１．このApplicationクラスのインスタンス（=自分自身）を保存するための変数を用意する
        // ※あとでgetInstance()からアクセスするために使う
        private lateinit var instance: MyApplication    // 宣言（後で初期化される）
        // ４－２．他の画面（Activityなど）からこのApplicationクラスにアクセスできるようにするための関数を追加する。
        fun getInstance(): MyApplication {
            return instance
        }
    }

    // ５．アプリ起動時に一度だけ呼ばれる初期化処理を定義するため、onCreate() をオーバーライドする
    override fun onCreate() {       // 初期化される
        super.onCreate()
        // ５－１．自分自身（Applicationクラス）をinstanceに入れる
        instance = this
    }
}

/*
１．画面レイアウト作成（レイアウト名：good_recycle_row）
	１－１．画面レイアウト通りにデザインを作成する。

２．データクラス作成（データクラス名：GoodRowData）
	２－１．ユーザID、ユーザ名、ささやき内容、いいね数、アイコンの画像パスを保持する変数を宣言する。

３．アダプター作成（クラス名：GoodAdapter［引数：MutableList<GoodRowData>、Context ］）
	３－１．RecyclerView.Adapterクラスを継承する。

	３－２．ビューホルダー（内部クラス）
		３－２－１．画面デザインで定義したオブジェクトを変数として宣言する。

	３－３．ビューホルダー生成時（onCreateViewHolder処理）
		３－３－１．いいね行情報の画面デザイン（good_recycle_row）をViewHolderに設定し、戻り値にセットする。

	３－４．ビューホルダーバインド時（onBindViewHolder処理）
		３－４－１．ビューホルダーのオブジェクトに対象行のデータ（ユーザ名、ささやき内容、いいね数）をセットする

		３－４－２．userImageのクリックイベントリスナーを生成する
			３－４－２－１．Adapterから画面遷移することになるので、インテントに新しいタスクで起動する為のフラグを追加する。

			３－４－２－２．インテントに対象行のユーザIDをセットする

			３－４－２－３．ユーザ情報画面に遷移する

	３－５．行数取得時（getItemCount処理）
		３－５－１．行リストの件数（データセットのサイズ）を戻り値にセットする
 */


