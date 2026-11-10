package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.data.UserPreferencesRepository
import com.example.ui.screens.ClockScreen
import com.example.ui.screens.NightstandScreen
import com.example.ui.theme.AuraClockTheme
import com.example.ui.viewmodel.ClockViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ClockViewModel by viewModels()
    private lateinit var prefsRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        prefsRepo = UserPreferencesRepository(this)

        val startInNightstand = intent?.getBooleanExtra("START_NIGHTSTAND", false) ?: false

        setContent {
            AuraClockTheme {
                MainApp(
                    viewModel = viewModel,
                    prefsRepo = prefsRepo,
                    initialNightstand = startInNightstand
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("START_NIGHTSTAND", false)) {
            // Handled in Compose state
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (event?.action == android.view.KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                android.view.KeyEvent.KEYCODE_HEADSETHOOK,
                android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                android.view.KeyEvent.KEYCODE_MEDIA_PLAY,
                android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    com.example.service.EarbudMediaService.handleMultiClickDirect(this)
                    return true
                }
                android.view.KeyEvent.KEYCODE_MEDIA_NEXT,
                android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    com.example.service.EarbudMediaService.executeAction(this, prefsRepo.preferences.value.earbudDoublePress)
                    return true
                }
                android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                android.view.KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    com.example.service.EarbudMediaService.executeAction(this, prefsRepo.preferences.value.earbudTriplePress)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

@Composable
fun MainApp(
    viewModel: ClockViewModel,
    prefsRepo: UserPreferencesRepository,
    initialNightstand: Boolean = false
) {
    var isNightstandActive by remember { mutableStateOf(initialNightstand) }

    // Request notification permission on Android 13+ (TIRAMISU) for alarms
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        if (isNightstandActive) {
            NightstandScreen(
                prefsRepo = prefsRepo,
                onExit = { isNightstandActive = false }
            )
        } else {
            ClockScreen(
                viewModel = viewModel,
                onLaunchNightstand = { isNightstandActive = true }
            )
        }
    }
}
