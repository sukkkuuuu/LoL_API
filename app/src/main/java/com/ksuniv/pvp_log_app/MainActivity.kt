package com.ksuniv.pvp_log_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ksuniv.pvp_log_app.data.ResponseMatchData
import com.ksuniv.pvp_log_app.data.ResponseUserData
import com.ksuniv.pvp_log_app.data.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val summoner_name : EditText = findViewById(R.id.summoner_name)
        val intent = Intent(this, LogActivity::class.java)
        val btn: Button = findViewById(R.id.search_button)
        btn.setOnClickListener{
            requestUserInfo(summoner_name.text.toString())
       }
    }
    private fun requestUserInfo(summoner_name: String) {
        // ResponseUserData class를 불러오겠다(Call하겠다) 어떻게? >> 아래와 같이
        // summoner_name을 파라미터로 받는 ServiceCreator 아래의 samplService 아래의 getUserInfo 메소드로 
        val call: Call<ResponseUserData> = ServiceCreator.sampleService.getUserInfo(summoner_name)
        // enqueue 비동기 실행
        call.enqueue(object : Callback<ResponseUserData>{
            override fun onResponse(
                call: Call<ResponseUserData>,
                response: Response<ResponseUserData>
            ) {
                // 연결 성공 시 
                if(response.isSuccessful) {
                    logSearch(response)
                }
                // 연결 실패 시 
                else{
                    Toast.makeText(this@MainActivity, "소환사 정보 없음", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ResponseUserData>, t: Throwable) {
                Log.e("NetworkTest", "error: $t")
            }
        })
    }
    // 소환사의 정보를 모두 LogActivity로 넘긴다
    // body?이 부분이 뭔지는 좀 더 알아보자
    private fun logSearch(responseData: Response<ResponseUserData>) {
        val intent = Intent(this, LogActivity::class.java)
        val body = responseData.body()
        intent.putExtra("id", body?.id.toString())
        intent.putExtra("puuid", body?.puuid.toString())
        intent.putExtra("name", body?.name.toString())
        intent.putExtra("profileIconId", body?.profileIconId.toString())
        intent.putExtra("summonerLevel", body?.summonerLevel.toString())
        startActivity(intent)
    }
}