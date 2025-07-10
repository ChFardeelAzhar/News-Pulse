package com.example.newspulse.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    val uiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchText by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    var newsList by remember { mutableStateOf<List<News>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    val bottomNavItems = remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Bookmarks
        )
    }

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
                    hasSearched = false
                    searchText = ""
                    viewModel.getNews()
                    delay(1000) // Reduced delay for better UX
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
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    onSearchClick = { text ->
                        keyboardController?.hide()
                        hasSearched = true
                        viewModel.getNews(text = text)
                    }
                )


                // Category


                // Lazy Column For each News Articles
                if (newsList.isNotEmpty() || !hasSearched) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 8.dp, end = 8.dp)
                    ) {
                        item {
                            Text(
                                "News", 
                                fontSize = 25.sp, 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        items(newsList, key = { it.id }) { news ->
                            NewsItem(news, onNewsClick = {
                                navController.navigate(NavRouts.createDetailScreenRoute(news))
                            })
                        }
                    }
                }


            }

        }


        when (val response = uiState) {
            ResultState.Loading -> {
                isLoading = true
            }

            is ResultState.Success -> {
                Log.d(
                    "NewsHomeScreen",
                    "News loaded successfully with ${response.data.news.size} articles"
                )
                isLoading = false
                newsList = response.data.news
            }

            is ResultState.Error -> {
                Log.e("NewsHomeScreen", "Failed to fetch news: ${response.error}")
                isLoading = false
                
                LaunchedEffect(response.error) {
                    Toast.makeText(context, "Failed to load news: ${response.error}", Toast.LENGTH_LONG).show()
                }
            }

            ResultState.Idle -> {
                // Initial state
            }
        }

        // Show empty state when search returns no results
        if (!isLoading && hasSearched && newsList.isEmpty() && uiState is ResultState.Success) {
            NoNewsFoundState(
                onRetryClick = {
                    hasSearched = false
                    searchText = ""
                    viewModel.getNews()
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading news...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
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
            .clip(RoundedCornerShape(16.dp))
            .clickable { onNewsClick() }
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

        // Dark gradient overlay for better text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    ),
                    RoundedCornerShape(16.dp)
                )
        )

        // Title with better visibility
        Text(
            text = news.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Author with proper null handling
        Text(
            text = when {
                news.authors.isNullOrEmpty() -> "Unknown Author"
                else -> news.authors.joinToString(", ")
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Date with better styling
        Text(
            text = formatDate(news.publish_date),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(
                    Color.Black.copy(alpha = 0.6f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


@Composable
fun NewsSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Search news...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchText.isBlank()) {
                        onSearchClick("")
                    } else {
                        onSearchClick(searchText)
                    }
                }
            )
        )

        Image(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clickable { 
                    if (searchText.isBlank()) {
                        // If search is empty, load all news
                        onSearchClick("")
                    } else {
                        onSearchClick(searchText)
                    }
                }
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    CircleShape
                )
                .padding(8.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun NoNewsFoundState(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // News icon  
        Image(
            imageVector = Icons.Default.Info,
            contentDescription = "No news found",
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    CircleShape
                )
                .padding(24.dp),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No News Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "We couldn't find any news articles matching your search. Try different keywords or check your internet connection.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "Load Latest News",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
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