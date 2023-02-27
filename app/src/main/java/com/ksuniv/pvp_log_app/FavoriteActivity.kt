package com.ksuniv.pvp_log_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.ksuniv.pvp_log_app.data.ResponseUserData
import com.ksuniv.pvp_log_app.data.ServiceCreator
import com.ksuniv.pvp_log_app.model.Favorite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteActivity : AppCompatActivity() {
    lateinit var favoriteListView: LinearLayout
    lateinit var favoriteList: List<Favorite>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "즐겨찾기"

        val db = AppDatabase.getInstance(applicationContext)!!
        favoriteListView = findViewById(R.id.favorite_list_view)
        Thread(Runnable {
            db.favoriteDao().getAll()!!.map {
                runOnUiThread{
                    val userBtn = Button(applicationContext)
                    val name = it.name
                    userBtn.text = name
                    userBtn.setOnClickListener{
                        requestUserInfo(name)
                    }
                    favoriteListView.addView(userBtn)
                }
                it
            }
        }).start()

//        favoriteList!!.map {
//            val userBtn = Button(applicationContext)
//            userBtn.text = it.name
//            favoriteListView.addView(userBtn)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestUserInfo(summoner_name: String) {
        val call: Call<ResponseUserData> = ServiceCreator.sampleService.getUserInfo(summoner_name)
        call.enqueue(object : Callback<ResponseUserData> {
            override fun onResponse(
                call: Call<ResponseUserData>,
                response: Response<ResponseUserData>
            ) {
                if(response.isSuccessful) {
                    logSearch(response)
                } else{
                    Toast.makeText(this@FavoriteActivity, "소환사 정보 없음", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseUserData>, t: Throwable) {
                Log.e("NetworkTest", "error: $t")
            }
        })
    }

    private fun logSearch(responseData: Response<ResponseUserData>) {
        val intent = Intent(this, LogActivity::class.java)
        val body = responseData.body()
        intent.putExtra("id", body?.id.toString())
        intent.putExtra("name", body?.name.toString())
        intent.putExtra("profileIconId", body?.profileIconId.toString())
        intent.putExtra("summonerLevel", body?.summonerLevel.toString())
        startActivity(intent)
    }
}