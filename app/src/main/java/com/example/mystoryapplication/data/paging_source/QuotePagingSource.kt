package com.example.mystoryapplication.data.paging_source


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystoryapplication.data.api.ApiService
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.StoryResponse

class QuotePagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            Log.d("getPagingStories", "position : $position \n params.loadSize : ${params.loadSize}" )
            val responseData = apiService.getPagingStories(position, params.loadSize)
            Log.d("responseData", responseData.message.toString())
            val listStoryItems = responseData.listStory?.filterNotNull() ?: emptyList()
//            listStoryItems.forEach { Log.d("responseData", it.name.toString()) }
            LoadResult.Page(
                data = listStoryItems,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
//                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
                nextKey = if (listStoryItems.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}