package com.example.noshapp.repository

import com.example.noshapp.model.RecommendationListItem
import com.example.noshapp.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class Repository @Inject constructor(
    private val api: ApiService
) {
    private val _recommendationList = MutableStateFlow<List<RecommendationListItem>>(emptyList())
    val recommendationList: StateFlow<List<RecommendationListItem>?>
        get() = _recommendationList

    suspend fun getRecommendation() {
        val result = api.getRecommendation()
        if (result.isSuccessful && result.body() != null) {
            _recommendationList.emit(result.body()!!)
        }
    }

}
