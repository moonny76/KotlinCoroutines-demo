package com.scarlet.coroutines.android

import com.scarlet.model.Recipe
import com.scarlet.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.LinkedHashMap

class FakeRemoteDataSource {
    private val mRecipes: MutableMap<String, Recipe> = LinkedHashMap<String, Recipe>()

    suspend fun searchRecipes(query: String): Resource<List<Recipe>> {
        return withContext(Dispatchers.IO) {
            delay(FAKE_NETWORK_DELAY)
            Resource.Success(mRecipes.values.toList())
        }
    }

    fun addRecipes(recipes: List<Recipe>) {
        recipes.forEach { recipe -> mRecipes[recipe.recipeId] = recipe.copy() }
    }

    companion object {
        var FAKE_NETWORK_DELAY = 0L
    }
}