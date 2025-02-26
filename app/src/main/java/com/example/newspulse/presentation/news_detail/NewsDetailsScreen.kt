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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
    var isClicked by remember { mutableStateOf(false) }
    var showLoadingIndicator by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isClicked = !isClicked
                    viewModel.addNews(news)
                }
            ) {
                Image(
                    imageVector = if (isClicked) Icons.Outlined.Favorite else Icons.Filled.Favorite,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error)
                )
            }
        }
    ) {
        NewsDetail(news = news, onBackClick = {
            navController.popBackStack()
        }, modifier = Modifier.padding(it))

        when (newsState.value) {
            ResultState.Loading -> {
                showLoadingIndicator = true
            }

            is ResultState.Success -> {
                showLoadingIndicator = false
                Toast.makeText(context, "News Added to Favorite", Toast.LENGTH_SHORT).show()

            }

            is ResultState.Error -> {
                Toast.makeText(context, "Failed! to Add news as Favorite", Toast.LENGTH_SHORT)
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

@Composable
fun NewsDetail(news: News, onBackClick: () -> Unit, modifier: Modifier = Modifier) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        AsyncImage(
            model = news.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(color = Color.LightGray),
            contentScale = ContentScale.FillBounds
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            val (backBtn, topSpace, summary, newsContent) = createRefs()

            Spacer(
                modifier = Modifier
                    .height(250.dp)
                    .constrainAs(topSpace) {
                        top.linkTo(parent.top)
                    }
            )

            Image(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        onBackClick()
                    }
                    .padding(3.dp)
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    },
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.background)
            )

            // News Detail Text

            Box(
                modifier = Modifier
                    .constrainAs(newsContent) {
                        top.linkTo(parent.top, margin = 370.dp) // <-- Position it below summary
                        start.linkTo(summary.start)
                        end.linkTo(summary.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.wrapContent
                    }
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {

                Text(
                    news.text,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                    color = MaterialTheme.colorScheme.background
                )

            }

            // Summary Card

            Card(
                modifier = Modifier
                    .width(300.dp)
                    .constrainAs(summary) {
                        top.linkTo(topSpace.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.onBackground
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formatDate(news.publish_date),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = news.summary ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background
//                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = news.authors?.joinToString(", ") ?: "",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)

                        )
                    }
                }


            }


        }

    }


}