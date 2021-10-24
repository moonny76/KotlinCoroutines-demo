package com.org.scarlet.coroutines.advanced

import com.org.scarlet.model.Recipe
import com.org.scarlet.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface RecipeApi {
    @GET("api/search")
    fun search(
        @Query("key") key: String,
        @Query("q") query: String
    ): Call<List<Recipe>>
}

interface RecipeCallback<T> {
    fun onSuccess(response: T)
    fun onError(response: T)
}

fun searchRecipes(api: RecipeApi, query: String, callback: RecipeCallback<Resource<List<Recipe>>>) {
    val call = api.search("key", query)
    call.enqueue(object : Callback<List<Recipe>> {
        override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
            if (response.isSuccessful) {
                callback.onSuccess(Resource.Success(response.body()!!))
            } else {
                callback.onError(Resource.Error(response.message()))
            }
        }

        override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
            callback.onError(Resource.Error(t.message))
        }
    })
}

suspend fun searchRecipesV1(api: RecipeApi, query: String): Resource<List<Recipe>> {
    TODO()
}

// Use Call.await()
suspend fun searchRecipesV2(api: RecipeApi, query: String): Resource<List<Recipe>> {
    val call: Call<List<Recipe>> = api.search("key", query)

    TODO()
}

suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body()!!)
                } else {
                    continuation.resumeWithException(Throwable(response.message()))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}