package com.uniquindio.thecatapp

/*

import android.net.Uri
import android.os.Bundle
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@Inject
lateinit var repository: com.uniquindio.thecatapp.domain.repository.CatRepository
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Starting one-time clearAllFavorites()")
                val result = repository.clearAllFavorites()
                result.onSuccess {
                    Log.i("MainActivity", "clearAllFavorites succeeded")
                }.onFailure { err ->
                    Log.e("MainActivity", "clearAllFavorites failed", err)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception while clearing favorites", e)
            }
        }
    }
}

*/