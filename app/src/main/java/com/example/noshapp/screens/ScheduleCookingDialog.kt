package com.example.noshapp.screens

import android.app.Notification.Style
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.example.noshapp.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCookingDialog(onDismissRequest: () -> Unit,onDeleteClicked : ()->Unit,onCookNowClicked: (time:String)->Unit) {
    var snappedTimeState = remember{ mutableStateOf("") }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .width(400.dp) // Set the fixed width
                    .wrapContentHeight(), // Adjust height based on content
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Schedule Cooking Time",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0XFF0a3479)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clock Picker
                    WheelTimePicker(timeFormat = TimeFormat.AM_PM) { snappedTime ->
                        snappedTimeState.value = snappedTime?.let {
                            val formatter = DateTimeFormatter.ofPattern("h:mm a")
                            it.format(formatter)
                        }.toString()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Delete",
                            style = TextStyle(textDecoration = TextDecoration.Underline),
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onDeleteClicked() }
                                .padding(16.dp) // Add padding if needed for better click area
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Re-schedule Button (Outline)
                        OutlinedButton(
                            onClick = { /* Handle Re-schedule */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Re-schedule")
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        // Cook Now Button
                        Button(
                            onClick = { onCookNowClicked(snappedTimeState.value)
                                onDismissRequest.invoke()
                                      },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFf8a629)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cook Now", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


