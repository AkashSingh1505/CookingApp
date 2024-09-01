package com.example.noshapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noshapp.model.RecommendationListItem
import com.example.noshapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel@Inject constructor(private val repository: Repository) : ViewModel() {

    val recommendationList: StateFlow<List<RecommendationListItem>?>
        get() = repository.recommendationList

    fun getRecommendation(){
        viewModelScope.launch {
            repository.getRecommendation()
        }
    }


    init {
        getRecommendation()
    }
}