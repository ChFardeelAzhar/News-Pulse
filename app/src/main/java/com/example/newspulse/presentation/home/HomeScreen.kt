package com.example.newspulse.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.newspulse.data.model.News
import com.example.newspulse.presentation.BottomNavItem
import com.example.newspulse.utils.NavRouts
import com.example.newspulse.utils.ResultState
import com.example.newspulse.utils.formatDate
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsHomeScreen(viewModel: NewsViewModel = hiltViewModel(), navController: NavController) {

    val uiState = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showLoadingIndicator = remember { mutableStateOf(false) }
    val newsResponse = remember { mutableStateOf<List<News>>(emptyList()) }
    var searchText = remember { mutableStateOf("") }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookmarks
    )

    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(110.dp)
            ) {

                val selectedRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                bottomNavItems.forEach { item ->

                    val selectedColor =
                        if (selectedRoute == item.route) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(
                            alpha = .5f
                        )

                    NavigationBarItem(
                        selected = selectedRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Image(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                colorFilter = ColorFilter.tint(color = selectedColor),
                                modifier = Modifier.height(24.dp) // Adjusted icon size for balance
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = selectedColor,
                                fontSize = 12.sp // Reduced text size for compact look
                            )
                        },

                        )
                }
            }
        }
    ) {

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    viewModel.getNews(text = searchText.value)
                    delay(2000)
                    isRefreshing = false
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth()
            ) {
                // Search Bar


                NewsSearchBar(
                    searchText,
                    onSearchClick = { text ->
                        viewModel.getNews(text = text)
                    }

                )


                // Category


                // Lazy Column For each News Articles

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    item {
                        Text("News", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    }
                    items(newsResponse.value) { news ->
                        NewsItem(news, onNewsClick = {
                            navController.navigate(NavRouts.createDetailScreenRoute(news))
                        })
                    }

                }


            }

        }


        when (val response = uiState.value) {

            ResultState.Loading -> {
                showLoadingIndicator.value = true
            }

            is ResultState.Success -> {
                Log.d(
                    "NewsHomeScreen",
                    "News loaded successfully with ${response.data.news.size} articles"
                )


                showLoadingIndicator.value = false
                newsResponse.value = response.data.news

            }

            is ResultState.Error -> {

                Log.e("NewsHomeScreen", "Failed to fetch news: ${response.error}")

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Failed! ${response.error}")
                    Button(onClick = {
                        scope.launch {
                            viewModel.getNews(
                                searchText.value
                            )
                        }
                    }) {
                        Text("Retry")
                    }
                }

                showLoadingIndicator.value = false
            }

            ResultState.Idle -> {

            }
        }

        if (showLoadingIndicator.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text("Loading...")
            }

        }


    }

}

@Composable
fun NewsItem(news: News, onNewsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .clickable {
                onNewsClick()
            }
            .background(color = MaterialTheme.colorScheme.error)

    ) {


        Log.d("NEWS_IMAGE", "NewsImage: ${news.image}")

        AsyncImage(
            model = news.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = news.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(7.dp)
        )

        Text(
            text = news.authors.toString(),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(5.dp)
        )


        Text(
            text = formatDate(news.publish_date),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(5.dp)
        )

    }

}


@Composable
fun NewsSearchBar(text: MutableState<String>, onSearchClick: (String) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        OutlinedTextField(
            value = text.value,
            onValueChange = {
                text.value = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search here", color = MaterialTheme.colorScheme.onBackground)
            },
            shape = RoundedCornerShape(24.dp)
        )

        Image(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clickable {
                    onSearchClick(text.value)
                },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )

    }

}


/*
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("News", Icons.Default.Newspaper, NavRouts.Destination.NewsHomeScreen.route),
        BottomNavItem(
            "Bookmarks",
            Icons.Default.Bookmark,
            NavRouts.Destination.NewsDetailScreen.route
        )
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // Adapts to light/dark mode
        tonalElevation = 5.dp
    ) {
        val currentRoute = navController.currentDestination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = { Text(item.label) },
                alwaysShowLabel = true, // Always show labels
            )
        }
    }
}


 */