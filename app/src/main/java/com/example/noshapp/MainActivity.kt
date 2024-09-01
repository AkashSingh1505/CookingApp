package com.example.noshapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.noshapp.model.RecommendationListItem
import com.example.noshapp.model.ScheduleTask
import com.example.noshapp.screens.ScheduleCookingDialog
import com.example.noshapp.ui.theme.NoshAppTheme
import com.example.noshapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoshAppTheme {
                App()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun App() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val recommendationList by mainViewModel.recommendationList.collectAsState()
    var showDialog = remember { mutableStateOf(false) }
    var selectedRecommendation  = remember { mutableStateOf<RecommendationListItem?>(null) }
    var scheduleTask  = remember { mutableStateOf<ScheduleTask?>(null) }

    // Common content
    @Composable
    fun MainContent() {
        Box(
            Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column {
                TopBar(scheduleTask)
                CategoriesBar()
                RecommendationBar(recommendationList) { recommendation ->
                    selectedRecommendation.value = recommendation
                    showDialog.value = true
                }
                Spacer(modifier = Modifier.height(8.dp))
                BottomBannerRow()
            }
        }
    }

    if (!showDialog.value) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBFE))
        ) {
            DrawerContent() // Permanent Drawer
            MainContent()
        }
    } else {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFBFE))
            ) {
                DrawerContent() // Permanent Drawer
                MainContent()
            }

            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = showDialog.value,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))
                ) {
                    ScheduleCookingDialog(
                        onDismissRequest = { showDialog.value = false },
                        onDeleteClicked = { /* Handle delete */ },
                        onCookNowClicked = { time ->
                            selectedRecommendation?.value.let { recommendation ->
                                scheduleTask.value = ScheduleTask(
                                    dishName = recommendation!!.dishName.toString(),
                                    time = time,
                                    imageUrl = recommendation!!.imageUrl.toString()
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DrawerContent() {
    Card(
        elevation = 50.dp, // High elevation for a more prominent shadow
        shape = RoundedCornerShape(8.dp), // Optional: Add rounded corners to the card
        backgroundColor = Color.White, // Background color of the card
        modifier = Modifier
            .fillMaxHeight()
            .width(120.dp) // Adjust the width of the drawer as needed
            .padding(end = 2.dp) // Optional: Add padding to separate the drawer from the content
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(Color.White) // Drawer background color
                .padding(16.dp), // Add padding around the column
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center items vertically
        ) {
            DrawerOption(iconResId = R.drawable.ic_cook, text = "Cook")
            DrawerOption(iconResId = R.drawable.ic_favourites, text = "Favourites")
            DrawerOption(iconResId = R.drawable.ic_manual, text = "Manual")
            DrawerOption(iconResId = R.drawable.ic_device, text = "Device")
            DrawerOption(iconResId = R.drawable.ic_preferences, text = "Preferences")
            DrawerOption(iconResId = R.drawable.ic_settings, text = "Settings")
        }
    }

}

@Composable
fun DrawerOption(iconResId: Int, text: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp) // Fixed vertical spacing
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier
                .size(20.dp) // Adjust size of the image as needed
        )
        Spacer(modifier = Modifier.height(8.dp)) // Spacing between image and text
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
fun TopBar(scheduleTask: MutableState<ScheduleTask?>) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(16.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.fillMaxHeight(),verticalAlignment = Alignment.CenterVertically) {
            SearchBar()
            if(scheduleTask.value!=null){
                Spacer(modifier = Modifier.width(20.dp))
                CurrentOperation(scheduleTask)
            }
        }
        Row(modifier = Modifier.fillMaxHeight(),verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector =  Icons.Outlined.Notifications ,
                colorFilter = ColorFilter.tint(Color(0XFF0a3479)),
                contentDescription = "notification button",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                painter = painterResource(id = R.drawable.baseline_power_settings_new_24), // Use the correct resource ID
                colorFilter = ColorFilter.tint(Color.Red), // Tint the image with black color
                contentDescription = "Power button",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
@Composable
fun CurrentOperation(scheduleTask: MutableState<ScheduleTask?>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0xFF33312f))
        , verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = scheduleTask.value!!.imageUrl,
                builder = {
                    scale(Scale.FILL) // Adjust how the image scales
                }
            ),
            contentDescription ="Dish Photo",
            modifier = Modifier
                .fillMaxWidth(0.2f)
                .clip(RoundedCornerShape(50.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF33312f))
            .padding(horizontal = 8.dp)
            .height(40.dp)
            .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start

        )
        {
            Text(text = scheduleTask.value?.dishName.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(text = "Scheduled "+scheduleTask.value?.time.toString(),
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
        }
    }
}
@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .border(BorderStroke(1.dp, Color(0XFF0a3479)), shape = RoundedCornerShape(50.dp))
            .clip(RoundedCornerShape(50.dp)),
        placeholder = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            ) {
                Text(
                    text = "Search for dish or ingredient",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.CenterStart) // Align text to the start
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0XFF0a3479)
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CategoriesBar() {
    Box(modifier = Modifier.padding(20.dp,8.dp)){
        Column {
            Text(text = "What's on your mind?", color = Color(0XFF0a3479), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Add space between items
            ) {
                items(10) { index -> // Replace 10 with your list size or data
                    CategoriesItem()
                }
            }

        }


    }

}
@Composable
fun CategoriesItem() {
    Card(
        elevation = 8.dp,
        modifier = Modifier
            .width(150.dp) // Adjust width if needed
            .clip(RoundedCornerShape(50.dp))
//            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp), clip = true) // Apply shadow
//            .graphicsLayer {
//                // Apply a red tint to the shadow on the bottom and right
//                shadowElevation = 8.dp.toPx()
//                shape = RoundedCornerShape(50.dp)
//                clip = true
//            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White), // Add padding inside the card if needed
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.dish_photo),
                contentDescription = "Dish Photo",
                modifier = Modifier
                    .size(40.dp) // Set size for the image
                    .clip(RoundedCornerShape(50.dp))
            )
            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between the image and the text
            Text(
                text = "Rice Item",
                color = Color(0XFF0a3479),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun RecommendationBar(
    recommendationList: List<RecommendationListItem>?,
    onClickRecommendationItem:(item :RecommendationListItem)->Unit
) {
    Box(modifier = Modifier.padding(20.dp,8.dp)){
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recommendations", color = Color(0XFF0a3479), fontWeight = FontWeight.Bold)
                Text(text = "Show all", color = Color(0XFF0a3479), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Add space between items
            ) {
                items(recommendationList!!.size) { index -> // Replace 10 with your list size or data
                    RecommendationItem(recommendationList.get(index),onClickRecommendationItem)
                }
            }

        }


    }

}
@Composable
fun RecommendationItem(item: RecommendationListItem, onClickRecommendationItem:(item :RecommendationListItem)->Unit) {

    Card(
        elevation = 8.dp,
        modifier = Modifier
            .width(160.dp) // Adjust width if needed
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClickRecommendationItem(item)
            }
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(6.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFf1f1f0))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = rememberImagePainter(
                                data = item.imageUrl,
                                builder = {
                                    scale(Scale.FILL) // Adjust how the image scales
                                }
                            ),
                            contentDescription = "Dish Photo",
                            modifier = Modifier
                                .size(100.dp) // Set size for the image
                                .clip(RoundedCornerShape(500.dp))
                        )
                        Image(
                            painter = painterResource(id = R.drawable.baseline_power_settings_new_24),
                            contentDescription = "Red Dot",
                            modifier = Modifier
                                .size(15.dp) // Set size for the image
                                .offset(50.dp, -40.dp)
                        )

                    }

                }
                Box(modifier = Modifier.offset(0.dp,-10.dp)){
                    RatingBar()
                }
                Text(
                    text = item.dishName.toString(),
                    color = Color(0XFF0a3479),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp)) // Add spacing between the image and the text
                Text(
                    text = "30 min . Medium prep.",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

    }
}

@Composable
fun RatingBar() {
    Box(
        modifier = Modifier
            .background(
                Color(0xFFf8a629),
                shape = RoundedCornerShape(15.dp)
            ) // Set background color and shape
            .padding(4.dp)
            .height(8.dp)
        , contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Rating Icon",
                modifier = Modifier.size(12.dp),
                colorFilter = ColorFilter.tint(Color.White),
            )
            Spacer(modifier = Modifier.width(1.dp))
            Text(
                text = "4.2",
                color = Color.White,
                fontSize = 6.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
        }
    }
}


@Preview
@Composable
fun BottomBannerRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(8.dp), // Add padding around the Row
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        BottomBannerCard("Explore all dishes")
        Spacer(modifier = Modifier.width(20.dp))
        BottomBannerCard("Confused what to cook?")
    }
}

@Composable
fun BottomBannerCard(msz:String) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(50.dp), // Adjust height as needed

        shape = RoundedCornerShape(8.dp), // Rounded corners
        elevation = 4.dp // Elevation for shadow effect
    ) {
        Box(
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.dish_photo),
                contentDescription = "Card background",
                modifier = Modifier.fillMaxSize(), // Make Image cover the entire Card
                contentScale = ContentScale.Crop // Scale image to cover the Card
            )

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.5f))) {

            }

            // Text aligned to the start and centered vertically
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart) // Align to the start of the Card
                    .padding(start = 16.dp), // Padding from the start edge
                contentAlignment = Alignment.CenterStart // Center text vertically
            ) {
                Text(
                    text = msz,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}










