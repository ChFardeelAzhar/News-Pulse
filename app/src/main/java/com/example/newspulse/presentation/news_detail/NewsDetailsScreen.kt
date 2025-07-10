package com.example.newspulse.presentation.news_detail

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newspulse.data.model.News
import com.example.newspulse.utils.NavRouts
import com.example.newspulse.utils.ResultState
import com.example.newspulse.utils.formatDate
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsDetailsScreen(
    navController: NavController,
    news: News,
    viewModel: NewsDetailsViewModel = hiltViewModel()
) {

    val newsState = viewModel.newsState.collectAsState()
    var isClicked = remember { mutableStateOf(false) }
    var showLoadingIndicator by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isBookmarked = viewModel.isBookmarked.collectAsState().value


    LaunchedEffect(newsState.value) {
        when (val value = newsState.value) {
            ResultState.Loading -> {
                showLoadingIndicator = true
            }

            is ResultState.Success -> {
                showLoadingIndicator = false
                Toast.makeText(context, value.data.toString(), Toast.LENGTH_SHORT).show()

            }

            is ResultState.Error -> {
                Toast.makeText(context, value.error, Toast.LENGTH_SHORT)
                    .show()
                showLoadingIndicator = false
            }

            ResultState.Idle -> {

            }

        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkIfBookmarked(news.id)
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
                shape = CircleShape,
                onClick = {
                    viewModel.toggleBookmark(news)
                },
            ) {
                Image(
                    imageVector = if (isBookmarked) Icons.Filled.Book else Icons.Outlined.Book,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error)
                )
            }
        }
    ) {
        NewsDetail(
            news = news, onBackClick = {
                navController.popBackStack()
            }, modifier = Modifier.padding(it),
            onBookmarkClick = {
                viewModel.toggleBookmark(news)
                isClicked.value != isClicked.value
            },
            isBookmarked = isClicked
        )


        if (showLoadingIndicator) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }

}

@Composable
fun NewsDetail(
    news: News,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBookmarkClick: () -> Unit,
    isBookmarked: State<Boolean>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = news.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Dark overlay for better button visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            // Back Button
            Image(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onBackClick() }
                    .padding(6.dp)
                    .align(Alignment.TopStart),
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }

        // Summary Card - overlapping the image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-50).dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = formatDate(news.publish_date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (!news.summary.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when {
                        news.authors.isNullOrEmpty() -> "Unknown Author"
                        else -> "By ${news.authors.joinToString(", ")}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Main Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = news.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
        
        // Bottom spacing for floating action button
        Spacer(modifier = Modifier.height(80.dp))
    }
}