package com.example.whisperclient

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class UserAdapter(private val dataset: MutableList<UserRowData>,private val context: Context): RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    /*
    
     */
    class ViewHolder(item : View) : RecyclerView.ViewHolder(item){
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

}