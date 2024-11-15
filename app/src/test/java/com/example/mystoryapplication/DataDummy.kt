package com.example.mystoryapplication

import androidx.lifecycle.MutableLiveData
import com.example.mystoryapplication.data.response.ListStoryItem
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.data.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2024-11-15T06:42:30.092Z",
                name = "User $i",
                description = "Dumy data description for story $i",
                lon = 100.0 + i,
                id = "story-$i",
                lat = 50.0 + i
            )
            items.add(storyItem)
        }
        return StoryResponse(
            listStory = items,
            error = false,
            message = "Success"
        )
    }

    fun generateDummyLoginResult(): LoginResult {
        val dummyLoginResult = LoginResult("name", "userId", "dummyToken")
        return dummyLoginResult
    }
}