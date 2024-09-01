package com.example.noshapp.retrofit

import com.example.noshapp.model.RecommendationListItem

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {


    @GET("nosh-assignment")
    suspend fun getRecommendation(): Response<List<RecommendationListItem>>

}