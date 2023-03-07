package com.ksuniv.pvp_log_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ksuniv.pvp_log_app.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        //ResponseUserData를 모두 받아온다
        val id = intent.getStringExtra("id")
        val puuid = intent.getStringExtra("puuid")
        val name = intent.getStringExtra("name")
        val profileIconId = intent.getStringExtra("profileIconId")
        val summonerLevel = intent.getStringExtra("summonerLevel")
        print(puuid)

//        val textView: TextView = findViewById(R.id.textView)
        val imageView: ImageView = findViewById(R.id.imageView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val levelTextView: TextView = findViewById(R.id.levelTextView)

        // 여러 이미지들이 있는 lol api url
        val profileImageUrl = "http://ddragon.leagueoflegends.com/cdn/13.3.1/img/profileicon/${profileIconId}.png"

        nameTextView.text = name
        levelTextView.text = summonerLevel + " 레벨"
//        Log.d("URL", imageUrl)
//        textView.text = "id: " + id + "\nname: " + name + "\npuuid: " + puuid

        // ????? 아래 두 줄은 하난도 이해 불가
        Glide.with(this).load(profileImageUrl).into(imageView)
        requestLeagueInfo(id!!)
        requestMatchInfo(puuid!!)
    }
    // 티어 function
    private fun requestLeagueInfo(summoner_id: String) {
        val call : Call<List<ResponseLeagueData>> = ServiceCreator.sampleService.getLeagueInfo(summoner_id)
        Log.d("league", call.request().toString())
        call.enqueue(object : Callback<List<ResponseLeagueData>>{
            // api 호출 성공 시
            override fun onResponse(
                call: Call<List<ResponseLeagueData>>,
                response: Response<List<ResponseLeagueData>>
            ) {
                // 응답 성공 시
                if(response.isSuccessful){
                    leagueSearch(response)
                }
                // 응답 실패 시
                else{
                    Toast.makeText(this@LogActivity, "에러1", Toast.LENGTH_LONG).show()
                }
            }
            // api 호출 실패 시
            override fun onFailure(call: Call<List<ResponseLeagueData>>, t: Throwable) {
                Toast.makeText(this@LogActivity, "에러2", Toast.LENGTH_LONG).show()
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
    // 최근 전적 function
    private fun requestMatchInfo(puuid: String){
        val call: Call<List<String>> = ServiceCreator.matchService.getMatchInfo(puuid)
        Log.d("출력", call.request().toString())
        // enqueue하고 object에 빨간줄 뜨는데 onResponse랑 onFailure이 있어야지 빨간줄 사라지는 듯
        call.enqueue(object: Callback<List<String>>{
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful){
                    requestIngameInfo(response)
                }
                else{
                    Log.d("FAIL", "FAIL!!")
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.d("FAIL", t.message!!)
            }
        })
    }
    private fun requestIngameInfo(responseData: Response<List<String>>){
        val matchId = responseData.body()
        matchId?.map {
            // getIngameInfo의 matchId type을 String으로 바꿔줘야한다( 이유는 모름...)
            val call: Call<ResponseIngameData> = ServiceCreator.matchService.getIngameInfo(it)
            call.enqueue(object : Callback<ResponseIngameData>{
                // 응답을 제대로 받아오지 못함 Call의 type 문제인가?????
                override fun onResponse(
                    call: Call<ResponseIngameData>,
                    response: Response<ResponseIngameData>
                ) {
                    if(response.isSuccessful){
                        ingameSearch(response)
                        Log.d("성공", response.toString())
                    }
                    else{
                        Log.d("실패", "실패")
                    }
                    // 시발 들어왔다!!!!
                    Log.d("suc","sss")
                }
                override fun onFailure(call: Call<ResponseIngameData>, t: Throwable) {
                    Log.d("fail", "F")
                }
            })
            it
        }
        Log.d("ingame", matchId.toString())
    }
    private fun ingameSearch(responseData: Response<ResponseIngameData>){
        Log.d("response",responseData.toString())
        val body = responseData.body()
        Log.d("body",body.toString())
        body?.info?.participants?.map {
            participarnt(it)
        }
        val gameMode = body?.info?.gameMode
    }
    private fun participarnt(responseData: Participant){
        val body = responseData
        Log.d("들오왔습니다","${body.deaths}")
        val matchText: TextView = findViewById(R.id.matchtext)
        matchText.text = "${body.championName}"
    }
}