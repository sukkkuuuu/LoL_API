package com.ksuniv.pvp_log_app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private const val BASE_URL = "https://kr.api.riotgames.com/"
    private const val MATCH_BASE_URL = "https://asia.api.riotgames.com/"

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        // Json데이터를 사용자가 정의한 Java 객채로 변환해주는 라이브러리. 즉, 직려로하하는 부분
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val match_retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(MATCH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 최근 전적 10경기
    val matchService: MatchService = match_retrofit.create(MatchService::class.java)

    val sampleService: SampleService = retrofit.create(SampleService::class.java)
}