package com.scarlet.util

import com.scarlet.model.Recipe

object TestData {

    val recipe1 = Recipe(
        "1af01c",
        "Cakespy: Cadbury Creme Deviled Eggs",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/20110321142792deviledcreme26d51.jpg",
        "http://www.seriouseats.com/recipes/2011/03/cakespy-cadbury-creme-deviled-eggs-easter.html",
        arrayOf(),
        99.9999f,
        false
    )
    val recipe2 = Recipe(
        "1cea66",
        "Poached Eggs in Tomato Sauce with Chickpeas and Feta",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/3689636539.jpg",
        "http://www.epicurious.com/recipes/food/views/Poached-Eggs-in-Tomato-Sauce-with-Chickpeas-and-Feta-368963",
        arrayOf(),
        99.9999f,
        false
    )
    val recipe3 = Recipe(
        "10aae5",
        "Cakespy: Cadbury Creme Eggs Benedict",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/20100301cremebenedicthow968f.jpg",
        "http://www.seriouseats.com/recipes/2010/03/cakespy-cadbury-creme-eggs-benedict-dessert-breakfast-recipe.html",
        arrayOf(),
        99.9999f,
        false
    )
    val recipe4 = Recipe(
        "35424",
        "Mexican Baked Eggs",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/Mexican2BBaked2BEggs2B5002B685320aa856d.jpg",
        "http://www.closetcooking.com/2011/09/mexican-baked-eggs.html", arrayOf(),
        99.9999f,
        false
    )

    val recipe1_favored = recipe1.copy(isFavorite = true)
    val recipe2_favored = recipe2.copy(isFavorite = true)

    var mRecipes = listOf(recipe1, recipe2, recipe3, recipe4)
    var mFavorites = listOf(recipe1_favored, recipe2_favored)

    val recipeDetails01 = Recipe(
        "1af01c",
        "Cakespy: Cadbury Creme Deviled Eggs",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/20110321142792deviledcreme26d51.jpg",
        "http://www.seriouseats.com/recipes/2011/03/cakespy-cadbury-creme-deviled-eggs-easter.html",
        arrayOf(
            "4 Cadbury Creme Eggs, chilled for 1 hour",
            "1/2 cup vanilla buttercream, colored yellow with food coloring",
            "red sprinkles, to garnish"
        ),
        99.9999f,
        false
    )
    val recipeDetails02 = Recipe(
        "1cea66",
        "Poached Eggs in Tomato Sauce with Chickpeas and Feta",
        "https://res.cloudinary.com/dk4ocuiwa/image/upload/v1575163942/RecipesApi/3689636539.jpg",
        "http://www.epicurious.com/recipes/food/views/Poached-Eggs-in-Tomato-Sauce-with-Chickpeas-and-Feta-368963",
        arrayOf(
            "1/4 cup olive oil",
            "1 medium onion, finely chopped",
            "4 garlic cloves, coarsely chopped",
            "2 jalapeos, seeded, finely chopped",
            "1 15-ounce can chickpeas, drained",
            "2 teaspoons Hungarian sweet paprika",
            "1 teaspoon ground cumin",
            "1 28-ounce can whole peeled tomatoes, crushed by hand, juices reserved",
            "Kosher salt and freshly ground black pepper",
            "1 cup coarsely crumbled feta",
            "8 large eggs",
            "1 tablespoon chopped flat-leaf parsley",
            "1 tablespoon chopped fresh cilantro",
            "Warm pita bread"
        ),
        99.9999f,
        false
    )

}