package com.dlifes.textsender

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dlifes.textsender.screen.MainScreen
import com.dlifes.textsender.screen.MainScreenViewModel
import com.dlifes.textsender.ui.theme.TextSenderTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val appContext = this.applicationContext
            val calendar = Calendar.getInstance()

            // Set the expiry date to April 30th of the current year
            calendar.set(Calendar.DAY_OF_MONTH, 20)
            calendar.set(Calendar.MONTH, Calendar.AUGUST) // 0 = January, ..., 11 = December

            val expiryDate = calendar.time
            println(expiryDate)


            // Check if the current date is after April 30th
            val currentDate = Calendar.getInstance().time
            println(currentDate)
            TextSenderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!currentDate.after(expiryDate)) {
                        MainScreen(MainScreenViewModel())
                    }
                    else{
                        Text("APK EXPIRED")
                    }
                }
            }
        }
    }
}


