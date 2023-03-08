package com.ksuniv.pvp_log_app.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

private const val API_KEY = "RGAPI-bdab0b6b-b286-42f8-9594-c35a845c7843"

interface SampleService {
    @Headers("Content-Type: application/json")

    @GET("lol/summoner/v4/summoners/by-name/{name}?api_key=$API_KEY")
    fun getUserInfo(
        @Path("name") summonerName: String
    ) : Call<ResponseUserData>

    @GET("lol/league/v4/entries/by-summoner/{summonerId}?api_key=$API_KEY")
    fun getLeagueInfo(
        @Path("summonerId") summonerId: String
    ): Call<List<ResponseLeagueData>>
}