package com.example.mystoryapplication.view.home

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.dicoding.myunlimitedquotes.MainDispatcherRule
import com.dicoding.myunlimitedquotes.getOrAwaitValue
import com.example.mystoryapplication.DataDummy
import com.example.mystoryapplication.data.Repository
import com.example.mystoryapplication.data.response.ListStoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.mystoryapplication.data.response.LoginResult
import com.example.mystoryapplication.view.adapter.QuoteListAdapter
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var quoteRepository: Repository
    // Injects the mock repository into the view model
    private lateinit var listStoryViewModel: ListStoryViewModel
//    private val token: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXpXeFYxdU44UEhBRjc1QTEiLCJpYXQiOjE3MzA4MDc0ODl9.kD2FyCfu11tOawMF4IL3E8RyYQEob56GxL0GN24335M"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        listStoryViewModel = ListStoryViewModel(quoteRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        // Create dummy LoginResult
//        val dummyLoginResult = LoginResult("name", "userId", "dummyToken")
        val dummyLoginResult = DataDummy.generateDummyLoginResult()
        val expectedLiveData = MutableLiveData<LoginResult>()
        expectedLiveData.value = dummyLoginResult


//        ==================================
        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val nonNullableListStory = dummyQuote.listStory?.filterNotNull() ?: emptyList() // Menghapus elemen null

        val data: PagingData<ListStoryItem> = QuotePagingSource.snapshot(nonNullableListStory)
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data

        // Mock the behavior of repository.getSession()
        Mockito.`when`(quoteRepository.getSession()).thenReturn(expectedLiveData.asFlow())
        // Call the method
        val actualResult = listStoryViewModel.getSession().getOrAwaitValue()
//        Log.d("newToken", actualResult.token)

        Mockito.`when`(quoteRepository.getQuote(actualResult.token)).thenReturn(expectedQuote)

        val mainViewModel = ListStoryViewModel(quoteRepository)

        val actualQuote: PagingData<ListStoryItem> = mainViewModel.getStoryPaginModel(actualResult.token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = QuoteListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyQuote.listStory?.size, differ.snapshot().size)
        Assert.assertEquals(dummyQuote.listStory!![0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val dummyLoginResult = DataDummy.generateDummyLoginResult()
        val expectedLiveData = MutableLiveData<LoginResult>()
        expectedLiveData.value = dummyLoginResult

        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data

        // Mock the behavior of repository.getSession()
        Mockito.`when`(quoteRepository.getSession()).thenReturn(expectedLiveData.asFlow())
        // Call the method
        val actualResult = listStoryViewModel.getSession().getOrAwaitValue()


        Mockito.`when`(quoteRepository.getQuote(actualResult.token)).thenReturn(expectedQuote)
        val mainViewModel = ListStoryViewModel(quoteRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.getStoryPaginModel(actualResult.token).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = QuoteListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class QuotePagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}