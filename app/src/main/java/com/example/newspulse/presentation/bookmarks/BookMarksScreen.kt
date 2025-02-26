package com.example.newspulse.presentation.bookmarks

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bookmarks",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            item {
                Text("News", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            /*
            items() { news ->
                NewsItem(news, onNewsClick = {
                    navController.navigate(NavRouts.createDetailScreenRoute(news))
                })
            }


             */
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

        }

        if (showLoadingIndicator) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }


    }


}