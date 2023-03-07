package com.ksuniv.pvp_log_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import com.ksuniv.pvp_log_app.data.ResponseUserData
import com.ksuniv.pvp_log_app.data.ServiceCreator
import com.ksuniv.pvp_log_app.model.Favorite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val summoner_name : EditText = findViewById(R.id.summoner_name)

        setSupportActionBar(findViewById(R.id.toolbar))
//        val db = AppDatabase.getInstance(applicationContext)!!
//        Thread(Runnable {
//            db.favoriteDao().deleteFavorite(Favorite(2, "연구소 인턴"))
//        }).start()

//        setCustomToolBar(R.id.toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//        summorer_name.imeOptions = EditorInfo.IME_ACTION_DONE
//        summoner_name.setOnKeyListener{ v, keyCode, event ->
//            if(keyCode == KeyEvent.KEYCODE_ENTER) {
//                val keyword: String by lazy {
//                    if (summoner_name.text.toString().isNullOrEmpty()) {
//                        return@lazy ""
//                    } else {
//                        return@lazy ""
//                    }
//                }
//                summoner_name.clearFocus()
//                summoner_name.requestFocus()
//
//            }
//            return@setOnKeyListener false
//        }
        val intent = Intent(this, LogActivity::class.java)
        val btn: Button = findViewById(R.id.search_button)
        btn.setOnClickListener{
           requestUserInfo(summoner_name.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return true
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

    private fun requestUserInfo(summoner_name: String) {
        val call: Call<ResponseUserData> = ServiceCreator.sampleService.getUserInfo(summoner_name)
        call.enqueue(object : Callback<ResponseUserData>{
            override fun onResponse(
                call: Call<ResponseUserData>,
                response: Response<ResponseUserData>
            ) {
                if(response.isSuccessful) {
                    logSearch(response)
                } else{
                    Toast.makeText(this@MainActivity, "소환사 정보 없음", Toast.LENGTH_LONG).show()
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