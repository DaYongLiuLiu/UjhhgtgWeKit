package dev.ujhhgtg.wekit.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.compose.material3.Text
import dev.ujhhgtg.wekit.ui.utils.AppTheme

@Keep
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Text("回去吧, 这里还啥都没有呢")
            }
        }
    }
}
