package com.org.scarlet.coroutines.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.org.scarlet.R
import com.org.scarlet.util.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CoActivity : AppCompatActivity() {
    val apiService = FakeRemoteDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareFakeData()

        Log.d(TAG, "onCreate: massive launching started ...")

        lifecycle.coroutineScope.launch {
            Log.d(TAG, "launch started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes in launch = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launch completed: $it")
        }

        lifecycle.coroutineScope.launchWhenCreated {
            Log.d(TAG, "launchWhenCreated started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes in launchWhenCreated = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenCreated completed: $it")
        }

        lifecycle.coroutineScope.launchWhenStarted {
            Log.d(TAG, "launchWhenStarted started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes in launchWhenStarted = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenStarted completed: $it")
        }

        lifecycle.coroutineScope.launchWhenResumed {
            Log.d(TAG, "launchWhenResumed started")
            val recipes = apiService.searchRecipes("eggs")
            Log.d(TAG, "recipes in launchWhenResumed = $recipes")
        }.invokeOnCompletion {
            Log.d(TAG, "launchWhenResumed completed: $it")
        }

        lifecycle.coroutineScope.launch {
            Log.d(TAG, "repeatOnLifecycle launched")
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d(TAG, "repeatOnLifeCycle started")
                val recipes = apiService.searchRecipes("eggs")
                Log.d(TAG, "recipes in repeatOnLifeCycle = $recipes")
            }
            Log.d(TAG, "See when i am printed ...")
        }.invokeOnCompletion {
            Log.d(TAG, "launch for repeatOnLifeCycle completed: $it")
        }
    }

    private fun prepareFakeData() {
        FakeRemoteDataSource.FAKE_NETWORK_DELAY = 3_000
        apiService.addRecipes(TestData.mRecipes)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy:")
    }

    companion object {
        const val TAG = "Coroutine"
    }
}