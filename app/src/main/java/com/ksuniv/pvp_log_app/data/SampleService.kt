package com.ksuniv.pvp_log_app.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

private const val API_KEY = "RGAPI-b9e76b93-d62c-4057-9d4f-bea2d3d150ee"

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