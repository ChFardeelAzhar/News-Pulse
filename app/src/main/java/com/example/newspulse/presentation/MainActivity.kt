package com.example.newspulse.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newspulse.presentation.bookmarks.BookMarksScreen
import com.example.newspulse.presentation.home.NewsHomeScreen
import com.example.newspulse.presentation.news_detail.NewsDetailsScreen
import com.example.newspulse.ui.theme.NewsPulseTheme
import com.example.newspulse.utils.NavRouts
import dagger.hilt.android.AndroidEntryPoint


sealed class BottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = NavRouts.Destination.NewsHomeScreen.route

    )

    object Bookmarks : BottomNavItem(
        label = "Bookmarks",
        icon = Icons.Default.CollectionsBookmark,
        route = NavRouts.Destination.BookMarksScreen.route
    )
}



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsPulseTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold {
                        NavHost(
                            navController = navController,
                            startDestination = NavRouts.Destination.NewsHomeScreen.route,
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


                            composable(NavRouts.Destination.BookMarksScreen.route) {
                                BookMarksScreen(navController = navController)
                            }


                        }
                    }


                }
            }
        }
    }
}


