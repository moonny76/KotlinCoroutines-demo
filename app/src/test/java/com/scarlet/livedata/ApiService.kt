package com.scarlet.livedata

import androidx.lifecycle.LiveData
import com.scarlet.model.Article
import com.scarlet.util.Resource

interface ApiService {
    /**
     * Get all articles
     */
    suspend fun getArticles(): Resource<List<Article>>

    /**
     * Get the most recommended (i.e., top-ranked) article
     */
    suspend fun getTopArticle(): Resource<Article>

    /**
     * Get all the articles written by the author of the current top-ranked article
     */
    fun getArticlesByAuthorName(name: String): LiveData<Resource<List<Article>>>
}