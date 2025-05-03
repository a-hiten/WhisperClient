package com.example.whisperclient;

public class MyApplication {



}

a

//１．Applicationクラスを継承する
//
//２．グローバル変数loginUserIdを文字列型で宣言して、空文字で初期化する
//
//３．グローバル変数apiUrlを文字列型で宣言して、各チームのAPIのホームディレクトリで初期化する
//ホームディレクトリ例 ： http://click.ecc.ac.jp/ecc/whisperSystem/
//
//        ４．Applicationクラスを他のクラスから参照できるように companion object を追加する
//	４－１．このApplicationクラスのインスタンス（=自分自身）を保存するための変数を用意する
//				※あとでgetInstance()からアクセスするために使う
//
//	４－２．他の画面（Activityなど）からこのApplicationクラスにアクセスできるようにするための関数を追加する。
//
//        ５．アプリ起動時に一度だけ呼ばれる初期化処理を定義するため、onCreate() をオーバーライドする
//	５－１．自分自身（Applicationクラス）をinstanceに入れる
//
//６．AndroidManifest（AndroidManifest.xml）設定
//全クラスから呼び出し出来るように、applicationタグに以下のパラメータを設定する
//
//パラメータ				android:name
//設定値				.MyApplication
