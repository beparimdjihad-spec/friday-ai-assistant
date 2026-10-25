package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FridayTab
import com.example.ui.FridayViewModel
import com.example.ui.screens.DeviceAppsScreen
import com.example.ui.screens.FridayCoreScreen
import com.example.ui.screens.ScheduleSyncScreen
import com.example.ui.screens.SmartHomeScreen
import com.example.ui.screens.SocialStudioScreen
import com.example.ui.theme.FridayCardBg
import com.example.ui.theme.FridayCyan
import com.example.ui.theme.FridayDarkBg
import com.example.ui.theme.FridayDarkSurface
import com.example.ui.theme.FridayTextMuted
import com.example.ui.theme.FridayTextPrimary
import com.example.ui.theme.FridayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridayTheme {
                FridayApp()
            }
        }
    }
}

@Composable
fun FridayApp(viewModel: FridayViewModel = viewModel()) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isBn = currentLang == "bn"

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = FridayDarkBg,
        bottomBar = {
            FridayBottomNavBar(
                currentTab = currentTab,
                isBn = isBn,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FridayDarkBg)
        ) {
            when (currentTab) {
                FridayTab.CORE -> FridayCoreScreen(viewModel = viewModel)
                FridayTab.APPS_DEVICE -> DeviceAppsScreen(viewModel = viewModel)
                FridayTab.SMART_HOME -> SmartHomeScreen(viewModel = viewModel)
                FridayTab.SOCIAL_STUDIO -> SocialStudioScreen(viewModel = viewModel)
                FridayTab.SCHEDULE_SYNC -> ScheduleSyncScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FridayBottomNavBar(
    currentTab: FridayTab,
    isBn: Boolean,
    onTabSelected: (FridayTab) -> Unit
) {
    NavigationBar(
        containerColor = FridayDarkSurface,
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            Triple(FridayTab.CORE, Icons.Default.SmartToy, if (isBn) "এফআরআই" else "Core"),
            Triple(FridayTab.APPS_DEVICE, Icons.Default.PhoneAndroid, if (isBn) "ডিভাইস" else "Device"),
            Triple(FridayTab.SMART_HOME, Icons.Default.Home, if (isBn) "স্মার্ট হোম" else "Home"),
            Triple(FridayTab.SOCIAL_STUDIO, Icons.Default.Share, if (isBn) "সোশ্যাল" else "Social"),
            Triple(FridayTab.SCHEDULE_SYNC, Icons.Default.CalendarMonth, if (isBn) "রুটিন" else "Sync")
        )

        tabs.forEach { (tab, icon, label) ->
            val selected = currentTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FridayDarkBg,
                    selectedTextColor = FridayCyan,
                    indicatorColor = FridayCyan,
                    unselectedIconColor = FridayTextMuted,
                    unselectedTextColor = FridayTextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
