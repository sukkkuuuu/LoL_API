package com.ksuniv.pvp_log_app.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

private const val API_KEY = "RGAPI-5de35e27-855e-4bff-a27d-f3af83373080"

//최근 전적을 위한 Service
interface MatchService {
    @Headers("Content-Type: application/json")

    // 사용자의 puuid를 사용해서 matchId(최근전적)을 들고 와야함
    @GET("lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=10&api_key=$API_KEY")
    fun getMatchInfo(
        @Path("puuid") puuid: String
    ) : Call<List<String>> // Call<> 부분이 조금 헷갈리네, 왔다 갔다하는 느낌이라네요

    @GET("lol/match/v5/matches/{matchId}?api_key=$API_KEY")
    fun getIngameInfo(
        @Path("matchId") matchId: String
    ) : Call<ResponseIngameData>
    // 인게임도 해줘야함
}