package com.example.mystoryapplication.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystoryapplication.data.api.ApiConfig
import com.example.mystoryapplication.data.paging_source.QuotePagingSource
import com.example.mystoryapplication.data.pref.UserPreference
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: LoginResult) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<LoginResult> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getQuote(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = {
                val apiService = ApiConfig.getApiService(token)
                Log.d("tokenDariGetQuote", token)
                QuotePagingSource(apiService)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference)
            }.also { instance = it }
    }
}