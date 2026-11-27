package com.esyhonotdead.vk_edu_kotlin_hw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.screens.HomeScreen
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                HomeScreen()
            }
        }
    }
}