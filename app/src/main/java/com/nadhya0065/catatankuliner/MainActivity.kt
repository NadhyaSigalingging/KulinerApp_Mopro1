package com.nadhya0065.catatankuliner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nadhya0065.catatankuliner.ui.screen.MainScreen
import com.nadhya0065.catatankuliner.ui.theme.CatatanKulinerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatatanKulinerTheme {
                MainScreen()

            }
        }
    }
}
