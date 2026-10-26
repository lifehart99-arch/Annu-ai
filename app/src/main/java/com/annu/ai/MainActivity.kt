package com.annu.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.annu.ai.theme.AnnuTheme
import com.annu.ai.ui.navigation.AnnuNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnnuTheme {
                AnnuNavGraph()
            }
        }
    }
}
