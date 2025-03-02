package com.example.newspulse.presentation.bookmarks

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.newspulse.presentation.BottomNavItem
import com.example.newspulse.presentation.home.NewsItem
import com.example.newspulse.utils.NavRouts
import com.example.newspulse.utils.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookMarksScreen(
    navController: NavController,
    viewModel: BookMarksViewModel = hiltViewModel()
) {

    var showLoadingIndicator by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val bookmarksState = viewModel.bookMarksState.collectAsState()
    val bookMarks = viewModel.getAllBookMarks().collectAsState(listOf())

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookmarks
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bookmarks",
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp)
                .padding(it)
        ) {
            item {
                Text("News", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            items(bookMarks.value) { news ->
                NewsItem(news, onNewsClick = {
                    navController.navigate(NavRouts.createDetailScreenRoute(news))
                })
            }


        }


        when (val value = bookmarksState.value) {
            ResultState.Loading -> {
                showLoadingIndicator = true
            }

            is ResultState.Success -> {
                showLoadingIndicator = false
                Toast.makeText(context, value.data, Toast.LENGTH_SHORT).show()

            }

            is ResultState.Error -> {
                Toast.makeText(context, value.error, Toast.LENGTH_SHORT)
                    .show()
                showLoadingIndicator = false
            }

            ResultState.Idle -> {

            }

        }

        if (showLoadingIndicator) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }


    }


}