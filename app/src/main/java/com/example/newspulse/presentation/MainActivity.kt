package com.example.newspulse.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newspulse.presentation.home.NewsHomeScreen
import com.example.newspulse.presentation.news_detail.NewsDetailsScreen
import com.example.newspulse.ui.theme.NewsPulseTheme
import com.example.newspulse.utils.NavRouts
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsPulseTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRouts.Destination.NewsHomeScreen.route
                    ) {

                        composable(NavRouts.Destination.NewsHomeScreen.route) {
                            NewsHomeScreen(navController = navController)
                        }

                        composable(NavRouts.Destination.NewsDetailScreen.route) {
                            val jsonNews = it.arguments?.getString("news")
                            jsonNews?.let {
                                val news = NavRouts.getNewsFromRoute(json = jsonNews)
                                NewsDetailsScreen(navController = navController, news = news)
                            }
                        }


                    }

                }
            }
        }
    }
}

