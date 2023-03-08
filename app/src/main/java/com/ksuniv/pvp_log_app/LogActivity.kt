
package com.ksuniv.pvp_log_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ksuniv.pvp_log_app.data.*
import com.ksuniv.pvp_log_app.model.Favorite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.schedule
import java.util.Timer


class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val dialog = LoadingDialog(this@LogActivity)
        dialog.show()
        setContentView(R.layout.activity_log)
        setSupportActionBar(findViewById(R.id.toolbar))

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
        val favoriteButton: ImageButton = findViewById(R.id.favorite_btn)


        // 여러 이미지들이 있는 lol api url
        val profileImageUrl =
            "http://ddragon.leagueoflegends.com/cdn/13.3.1/img/profileicon/${profileIconId}.png"

        nameTextView.text = name
        levelTextView.text = summonerLevel + " 레벨"
//        Log.d("URL", imageUrl)
//        textView.text = "id: " + id + "\nname: " + name + "\npuuid: " + puuid

        val db = AppDatabase.getInstance(applicationContext)!!
        var checkUser: String? = null
        Thread(Runnable {
            checkUser = db.favoriteDao().getUser(name!!)
            if(checkUser == null){
                favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
            }else{
                favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
            }
            favoriteButton.setOnClickListener{
                if(checkUser == null){
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    Toast.makeText(this, "즐겨찾기에 추가", Toast.LENGTH_SHORT).show()
                    Thread(Runnable {
                        db.favoriteDao().insertFavorite(Favorite(id!!, name!!))
                    }).start()
                    checkUser = name
                }else{
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Toast.makeText(this, "즐겨찾기에 제외", Toast.LENGTH_SHORT).show()
                    Thread(Runnable {
                        db.favoriteDao().deleteFavorite(Favorite(id!!, name!!))
                    }).start()
                    checkUser = null
                }
            }
        }).start()

        // ????? 아래 두 줄은 하난도 이해 불가
        Glide.with(this).load(profileImageUrl).into(imageView)
        requestLeagueInfo(id!!)
        requestMatchInfo(puuid!!)

        Timer().schedule(600){
            dialog.dismiss()
        }//딜레이
    }

    // 즐겨찾기 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {
            R.id.favorite -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 티어 function
    private fun requestLeagueInfo(summoner_id: String) {
        val call: Call<List<ResponseLeagueData>> =
            ServiceCreator.sampleService.getLeagueInfo(summoner_id)
        Log.d("league", call.request().toString())
        call.enqueue(object : Callback<List<ResponseLeagueData>> {
            // api 호출 성공 시
            override fun onResponse(
                call: Call<List<ResponseLeagueData>>,
                response: Response<List<ResponseLeagueData>>
            ) {
                // 응답 성공 시
                if (response.isSuccessful) {
                    leagueSearch(response)
                }
                // 응답 실패 시
                else {
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
        val soloRankedImageView: ImageView = findViewById(R.id.solo_ranked_img)
        val soloRankedTextView: TextView = findViewById(R.id.solo_ranked_text)
        val flexRankedImageView: ImageView = findViewById(R.id.flex_ranked_img)
        val flexRankedTextView: TextView = findViewById(R.id.flex_ranked_text)
        val flexRankedRecordTextView: TextView = findViewById(R.id.flex_ranked_record_text)
        val soloRankedRecordTextView: TextView = findViewById(R.id.solo_ranked_record_text)
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
            when (tier) {
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
    private fun requestMatchInfo(puuid: String) {
        val call: Call<List<String>> = ServiceCreator.matchService.getMatchInfo(puuid)
        Log.d("출력", call.request().toString())
        // enqueue하고 object에 빨간줄 뜨는데 onResponse랑 onFailure이 있어야지 빨간줄 사라지는 듯
        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful) {
                    requestIngameInfo(response, puuid)
                } else {
                    Log.d("FAIL", "FAIL!!")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.d("FAIL", t.message!!)
            }
        })
    }

    private fun requestIngameInfo(responseData: Response<List<String>>, puuid: String) {
        val matchId = responseData.body()
        matchId?.map {
            // getIngameInfo의 matchId type을 String으로 바꿔줘야한다( 이유는 모름...)
            val call: Call<ResponseIngameData> = ServiceCreator.matchService.getIngameInfo(it)
            call.enqueue(object : Callback<ResponseIngameData> {
                // 응답을 제대로 받아오지 못함 Call의 type 문제인가?????
                override fun onResponse(
                    call: Call<ResponseIngameData>,
                    response: Response<ResponseIngameData>
                ) {
                    if (response.isSuccessful) {
                        ingameSearch(response, puuid)
                        Log.d("성공", response.toString())
                    } else {
                        Log.d("실패", "실패")
                    }
                    // 시발 들어왔다!!!!
                    Log.d("suc", "sss")
                }

                override fun onFailure(call: Call<ResponseIngameData>, t: Throwable) {
                    Log.d("fail", "F")
                }
            })
            it
        }
        Log.d("ingame", matchId.toString())
    }

    private fun ingameSearch(responseData: Response<ResponseIngameData>, puuid: String) {
        Log.d("response", responseData.toString())
        val body = responseData.body()
        Log.d("body", body.toString())

        // 해당 user의 puuid로 사용자의 정보를 가져와야한다
        body?.metadata?.participants?.map{
            if (it == puuid){
                body?.info?.participants?.map {
                    participarnt(it)
                    Log.d("info",it.toString())
                }
//                Log.d("puuid",it)
            }
            else{
                body?.info?.participants?.map {
//                    participarnt(it)
                    Log.d("info",it.toString())
                }
            }
            // nfo 안의 puuid가 metadata의 puuid랑 같다면??? 그 때의 사용자의 info 정보를 가져오는거지
            Log.d("metadata",it.toString())
        }

        val gameMode = body?.info?.gameMode
    }

    private fun participarnt(responseData: Participant) {
        val body = responseData
        val win: ImageView = findViewById(R.id.win)
        val time: LinearLayout = findViewById(R.id.time)
        val username: TextView = findViewById(R.id.uesrname)
        val killdeath: TextView = findViewById(R.id.killdeath)
        killdeath.text = "${body.kills}/${body.deaths}/${body.assists}(${body.killingSprees})"
        username.text = "${body.championName}"
        Log.d("들오왔습니다", "${body.deaths}")
    }
}