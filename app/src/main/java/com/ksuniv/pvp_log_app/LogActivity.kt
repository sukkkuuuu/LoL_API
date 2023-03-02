package com.ksuniv.pvp_log_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.bumptech.glide.Glide
import com.ksuniv.pvp_log_app.data.ResponseLeagueData
import com.ksuniv.pvp_log_app.data.ResponseUserData
import com.ksuniv.pvp_log_app.data.ServiceCreator
import com.ksuniv.pvp_log_app.model.Favorite
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import kotlin.concurrent.schedule

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = LoadingDialog(this@LogActivity)
        dialog.show()
        setContentView(R.layout.activity_log)
        setSupportActionBar(findViewById(R.id.toolbar))

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val profileIconId = intent.getStringExtra("profileIconId")
        val summonerLevel = intent.getStringExtra("summonerLevel")


//        val textView: TextView = findViewById(R.id.textView)
        val imageView: ImageView = findViewById(R.id.imageView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val levelTextView: TextView = findViewById(R.id.levelTextView)
        val favoriteButton: ImageButton = findViewById(R.id.favorite_btn)

        val profileImageUrl = "http://ddragon.leagueoflegends.com/cdn/13.3.1/img/profileicon/${profileIconId}.png"

        nameTextView.text = name
        levelTextView.text = summonerLevel + " 레벨"
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


//        Log.d("URL", imageUrl)
//        textView.text = "id: " + id + "\nname: " + name + "\npuuid: " + puuid
        Glide.with(this).load(profileImageUrl).into(imageView)
        requestLeagueInfo(id!!)



        Timer().schedule(600){
            dialog.dismiss()
        }//딜레이

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
            rankedTextView?.text = rankedTextView?.text.toString() + " ${rank} - ${leaguePoint}LP"
            rankedRecordTextView?.text = "${wins}승 ${losses}패"
            it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId) {
            R.id.favorite -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}