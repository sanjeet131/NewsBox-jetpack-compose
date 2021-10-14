package com.ysanjeet535.newsbox.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ysanjeet535.newsbox.data.remote.NewsApi
import com.ysanjeet535.newsbox.data.remote.dto.NewsResponse
import com.ysanjeet535.newsbox.utils.Constants.API_KEY
import com.ysanjeet535.newsbox.utils.Response
import javax.inject.Inject

class NewsArticleRepository @Inject constructor (private val newsApi : NewsApi) {

    private val _newsResponseLiveData = MutableLiveData<NewsResponse>()
    private val _newsTopicResponseLiveData = MutableLiveData<NewsResponse>()

    val newsResponseLiveData : LiveData<NewsResponse> get() = _newsResponseLiveData
    val newsTopicResponseLiveData : LiveData<NewsResponse> get() = _newsTopicResponseLiveData

    suspend fun getTopHeadlines(country:String,apiKey :String) {

            val result = newsApi.getTopheadlines(country,apiKey)
            if(result?.body()!=null){
                _newsResponseLiveData.postValue(result.body()!!)
            }

    }

    suspend fun getTopHeadlinesOfTopic(country:String,category : String,apiKey :String) {

        val result = newsApi.getTopheadlinesOfTopic(country,category,apiKey)
        if(result?.body()!=null){
            _newsTopicResponseLiveData.postValue(result.body()!!)
        }

    }
}

