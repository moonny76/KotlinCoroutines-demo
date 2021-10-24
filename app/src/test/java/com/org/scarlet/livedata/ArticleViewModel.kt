package com.org.scarlet.livedata

import androidx.lifecycle.*
import com.org.scarlet.livedata.ApiService
import com.org.scarlet.model.Article
import com.org.scarlet.util.Resource
import kotlinx.coroutines.*

class ArticleViewModel(
    private val apiService: ApiService
) : ViewModel() {

    /**
     * Style 1
     */
    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val articles: LiveData<Resource<List<Article>>> = _articles

    init {
        viewModelScope.launch {
            _articles.postValue(apiService.getArticles())
        }
    }

    /**
     * Style 2
     */

//    val articles: LiveData<Resource<List<Article>>> =
//        MutableLiveData<Resource<List<Article>>>().apply {
//            viewModelScope.launch {
//                println(Thread.currentThread().name)
//                postValue(apiService.getArticles())
//            }
//        }

    /**/

    val topArticle: LiveData<Resource<Article>> = liveData {
        println(Thread.currentThread().name)
        while (true) {
            println("emitting every 30 secs")
            emit(apiService.getTopArticle())
            delay(30_000)
        }
    }

    @Suppress("UNCHECKED_CAST")
    val articlesByTopAuthor: LiveData<Resource<List<Article>>> =
        topArticle
            .switchMap { resource ->
                when (resource) {
                    is Resource.Success<Article> ->
                        liveData {
                            emit(Resource.Loading)
                            emitSource(apiService.getArticlesByAuthorName(resource.data?.author!!))
                        }
                    else -> liveData<Resource<List<Article>>> {
                        emit(resource as Resource<List<Article>>)
                    }
                }
            }
}