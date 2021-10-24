package com.org.scarlet.coroutines.testing.version4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.org.scarlet.livedata.ApiService
import com.org.scarlet.model.Article
import com.org.scarlet.util.Resource
import kotlinx.coroutines.*

class ArticleViewModel(
    private val apiService: ApiService,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) : ViewModel() {

    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val articles: LiveData<Resource<List<Article>>> = _articles

    fun onButtonClicked() {
        viewModelScope.launch {
            loadData()
        }
    }

    suspend fun loadData() {
        doLongRunningCalculation()
        val articles = networkRequest()
        update(articles)
    }

    private suspend fun networkRequest(): Resource<List<Article>> {
        // Any improvement?
        return withContext(dispatchers.io) {
            apiService.getArticles()
        }
    }

    private fun update(articles: Resource<List<Article>>) {
        _articles.value = articles
    }

    private suspend fun doLongRunningCalculation() {
        withContext(dispatchers.default) {
            delay(1000)
        }
    }
}