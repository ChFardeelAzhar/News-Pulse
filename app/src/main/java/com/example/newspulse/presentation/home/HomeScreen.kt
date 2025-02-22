package com.example.newspulse.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.newspulse.data.model.News
import com.example.newspulse.utils.ResultState
import com.example.newspulse.utils.formatDate
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsHomeScreen(viewModel: NewsViewModel = hiltViewModel()) {

    val uiState = viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showLoadingIndicator = remember { mutableStateOf(false) }
    val newsResponse = remember { mutableStateOf<List<News>>(emptyList()) }
    var text = remember { mutableStateOf("") }


    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            // Search Bar


            NewsSearchBar(text.value, onChangeSearchbar = { str ->
                text.value = str
                viewModel.getNews(
                    text = str
                )

            })


            // Category


            // Lazy Column For each News Articles

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                item {
                    Text("News", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                items(newsResponse.value) { news ->
                    NewsItem(news)
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
                                text.value
                            )
                        }
                    }) {
                        Text("Retry")
                    }
                }

                showLoadingIndicator.value = false
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
fun NewsItem(news: News) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(140.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
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

//        Text(
//            text = news.authors.toString(),
//            style = MaterialTheme.typography.titleSmall,
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(5.dp)
//        )


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
fun NewsSearchBar(text: String, onChangeSearchbar: (String) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onChangeSearchbar,
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
                .padding(end = 16.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
        )

    }

}