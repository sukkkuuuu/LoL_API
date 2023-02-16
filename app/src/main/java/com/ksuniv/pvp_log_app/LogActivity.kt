package com.ksuniv.pvp_log_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ksuniv.pvp_log_app.data.ResponseLeagueData
import com.ksuniv.pvp_log_app.data.ResponseUserData
import com.ksuniv.pvp_log_app.data.ServiceCreator
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val profileIconId = intent.getStringExtra("profileIconId")
        val summonerLevel = intent.getStringExtra("summonerLevel")

//        val textView: TextView = findViewById(R.id.textView)
        val imageView: ImageView = findViewById(R.id.imageView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val levelTextView: TextView = findViewById(R.id.levelTextView)

        val profileImageUrl = "http://ddragon.leagueoflegends.com/cdn/13.3.1/img/profileicon/${profileIconId}.png"
        nameTextView.text = name
        levelTextView.text = summonerLevel + " 레벨"
//        Log.d("URL", imageUrl)
//        textView.text = "id: " + id + "\nname: " + name + "\npuuid: " + puuid
        Glide.with(this).load(profileImageUrl).into(imageView)
        requestLeagueInfo(id!!)
    }

    private fun requestLeagueInfo(summoner_id: String) {
        val call : Call<List<ResponseLeagueData>> = ServiceCreator.sampleService.getLeagueInfo(summoner_id)
        call.enqueue(object : Callback<List<ResponseLeagueData>>{
            override fun onResponse(
                call: Call<List<ResponseLeagueData>>,
                response: Response<List<ResponseLeagueData>>
            ) {
                if(response.isSuccessful){
                    leagueSearch(response)
                } else{
                    Toast.makeText(this@LogActivity, "에러", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ResponseLeagueData>>, t: Throwable) {
                Toast.makeText(this@LogActivity, "에러", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun leagueSearch(responseData: Response<List<ResponseLeagueData>>) {
        val body = responseData.body()
        val soloRankedImageView : ImageView = findViewById(R.id.solo_ranked_img)
        val soloRankedTextView : TextView = findViewById(R.id.solo_ranked_text)
        val flexRankedImageView : ImageView = findViewById(R.id.flex_ranked_img)
        val flexRankedTextView : TextView = findViewById(R.id.flex_ranked_text)
        val flexRankedRecordTextView : TextView = findViewById(R.id.flex_ranked_record_text)
        val soloRankedRecordTextView : TextView = findViewById(R.id.solo_ranked_record_text)
        var rankedImageView: ImageView?
        var rankedTextView: TextView?
        var rankedRecordTextView: TextView?
//        Toast.makeText(this@LogActivity, "${body?.get(0)?.queueType}", Toast.LENGTH_LONG).show()
        body?.map {
            when (it?.queueType) {
                "RANKED_SOLO_5x5" -> {
                    rankedImageView = soloRankedImageView
                    rankedTextView = soloRankedTextView
                    rankedRecordTextView = soloRankedRecordTextView
                }
                "RANKED_FLEX_SR" -> {
                    rankedImageView = flexRankedImageView
                    rankedTextView = flexRankedTextView
                    rankedRecordTextView = flexRankedRecordTextView
                }
                else -> {
                    rankedImageView = null
                    rankedTextView = null
                    rankedRecordTextView = null
                }
            }
            val queryType = it?.queueType.toString()
            val tier = it?.tier.toString()
            val rank = it?.rank.toString()
            val leaguePoint = it?.leaguePoints.toString()
            val wins = it?.wins.toString()
            val losses = it?.losses.toString()
            when(tier) {
                "CHALLENGER" -> {
                    rankedImageView?.setImageResource(R.drawable.challenger)
                    rankedTextView?.text = "CHALLENGER"
                }
                "GRANDMASTER" -> {
                    rankedImageView?.setImageResource(R.drawable.grandmaster)
                    rankedTextView?.text = "GRANDMASTER"
                }
                "MASTER" -> {
                    rankedImageView?.setImageResource(R.drawable.master)
                    rankedTextView?.text = "MASTER"
                }
                "DIAMOND" -> {
                    rankedImageView?.setImageResource(R.drawable.diamond)
                    rankedTextView?.text = "DIAMOND"
                }
                "PLATINUM" -> {
                    rankedImageView?.setImageResource(R.drawable.platinum)
                    rankedTextView?.text = "PLATINUM"
                }
                "GOLD" -> {
                    rankedImageView?.setImageResource(R.drawable.gold)
                    rankedTextView?.text = "GOLD"
                }
                "SILVER" -> {
                    rankedImageView?.setImageResource(R.drawable.silver)
                    rankedTextView?.text = "SILVER"
                }
                "BRONZE" -> {
                    rankedImageView?.setImageResource(R.drawable.bronze)
                    rankedTextView?.text = "BRONZE"
                }
                "IRON" -> {
                    rankedImageView?.setImageResource(R.drawable.iron)
                    rankedTextView?.text = "IRON"
                }
                else -> {
                    rankedImageView?.setImageResource(R.drawable.unranked)
                    rankedTextView?.text = "UNRANKED"
                }
            }
            rankedTextView?.text = rankedTextView?.text.toString() + " - ${leaguePoint}LP"
            rankedRecordTextView?.text = "${wins}승 ${losses}패"
            it
        }
    }
}