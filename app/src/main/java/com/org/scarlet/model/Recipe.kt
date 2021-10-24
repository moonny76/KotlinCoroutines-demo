package com.org.scarlet.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey
    @ColumnInfo(name = "recipe_id") val recipeId: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "source_url") val sourceUrl: String?,
    @ColumnInfo(name = "ingredients") val ingredients: Array<String>?,
    @ColumnInfo(name = "social_rank") val socialRank: Float,
    @ColumnInfo(name = "favorite") val isFavorite: Boolean) {

    fun isSameAs(recipe: Recipe): Boolean {
        return recipeId == recipe.recipeId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (recipeId != other.recipeId) return false
        if (title != other.title) return false
        if (imageUrl != other.imageUrl) return false
        if (sourceUrl != other.sourceUrl) return false
        if (ingredients != null) {
            if (other.ingredients == null) return false
            if (!ingredients.contentEquals(other.ingredients)) return false
        } else if (other.ingredients != null) return false
        if (socialRank != other.socialRank) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipeId.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (sourceUrl?.hashCode() ?: 0)
        result = 31 * result + (ingredients?.contentHashCode() ?: 0)
        result = 31 * result + socialRank.hashCode()
        result = 31 * result + isFavorite.hashCode()
        return result
    }
}