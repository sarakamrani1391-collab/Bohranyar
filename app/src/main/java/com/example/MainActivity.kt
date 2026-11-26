package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.HamyarBohranApp
import com.example.ui.theme.HamyarBohranTheme
import com.example.ui.viewmodel.AppViewModelProvider
import com.example.ui.viewmodel.MainAppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainAppViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val settings by mainViewModel.userSettings.collectAsStateWithLifecycle()

            HamyarBohranTheme(
                darkTheme = settings.darkModeEnabled,
                highContrast = settings.highContrastMode,
                largeText = settings.largeTextMode
            ) {
                HamyarBohranApp(mainViewModel = mainViewModel)
            }
        }
    }
}
