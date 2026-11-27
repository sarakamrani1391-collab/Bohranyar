package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.system.PersianTimeFormatter
import com.example.ui.components.HamyarTopBar
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onSaveProfile: (name: String, city: String, bloodType: String, medicalNotes: String) -> Unit,
    onUpdateAccessibility: (highContrast: Boolean, largeText: Boolean, darkMode: Boolean) -> Unit,
    onRefreshOfflinePackage: () -> Unit,
    onReplayOnboarding: () -> Unit,
    onDismissMessage: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf(uiState.settings.userName) }
    var city by rememberSaveable { mutableStateOf(uiState.settings.userCity) }
    var bloodType by rememberSaveable { mutableStateOf(uiState.settings.bloodType) }
    var medicalNotes by rememberSaveable { mutableStateOf(uiState.settings.medicalNotes) }

    LaunchedEffect(uiState.settings.userName, uiState.settings.userCity, uiState.settings.bloodType, uiState.settings.medicalNotes) {
        if (name.isEmpty()) name = uiState.settings.userName
        if (city == "تهران" && uiState.settings.userCity.isNotBlank()) city = uiState.settings.userCity
        if (bloodType == "نامشخص" && uiState.settings.bloodType.isNotBlank()) bloodType = uiState.settings.bloodType
        if (medicalNotes.isEmpty()) medicalNotes = uiState.settings.medicalNotes
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "تنظیمات و پروفایل",
                onBackClick = onBack,
                isOnline = uiState.isOnline
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                uiState.statusMessage?.let { msg ->
                    item {
                        Surface(
                            color = SafeGreenSoft,
                            shape = RoundedCornerShape(14.dp),
                            onClick = onDismissMessage
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = SafeGreen,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                        }
                    }
                }

                // Profile card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "اطلاعات اضطراری شخصی",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("نام و نام خانوادگی") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("settings_name_input")
                            )
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("شهر") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = bloodType,
                                onValueChange = { bloodType = it },
                                label = { Text("گروه خونی (مثلاً O+)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = medicalNotes,
                                onValueChange = { medicalNotes = it },
                                label = { Text("بیماری خاص یا حساسیت دارویی") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = { onSaveProfile(name, city, bloodType, medicalNotes) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("settings_save_profile_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ذخیره پروفایل")
                            }
                        }
                    }
                }

                // Offline package & sync
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "وضعیت بسته آفلاین",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "آخرین به‌روزرسانی ذخیره داخلی: ${PersianTimeFormatter.formatTimestamp(uiState.settings.lastOfflineSyncMillis)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = onRefreshOfflinePackage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("settings_refresh_offline_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("به‌روزرسانی و ذخیره بسته آفلاین")
                            }
                        }
                    }
                }

                // Accessibility & Display
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "خوانایی و دسترس‌پذیری",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            SettingToggleRow(
                                title = "نوشته‌های درشت‌تر",
                                subtitle = "افزایش اندازه فونت فارسی برای مطالعه آسان‌تر",
                                checked = uiState.settings.largeTextMode,
                                onCheckedChange = {
                                    onUpdateAccessibility(
                                        uiState.settings.highContrastMode,
                                        it,
                                        uiState.settings.darkModeEnabled
                                    )
                                }
                            )
                            SettingToggleRow(
                                title = "کنتراست بالا",
                                subtitle = "تیره‌تر شدن متن‌ها و خطوط در نور زیاد",
                                checked = uiState.settings.highContrastMode,
                                onCheckedChange = {
                                    onUpdateAccessibility(
                                        it,
                                        uiState.settings.largeTextMode,
                                        uiState.settings.darkModeEnabled
                                    )
                                }
                            )
                            SettingToggleRow(
                                title = "حالت شب (تیره)",
                                subtitle = "کاهش نور صفحه و مصرف باتری در قطعی برق",
                                checked = uiState.settings.darkModeEnabled,
                                onCheckedChange = {
                                    onUpdateAccessibility(
                                        uiState.settings.highContrastMode,
                                        uiState.settings.largeTextMode,
                                        it
                                    )
                                }
                            )
                        }
                    }
                }

                // Privacy & Onboarding replay
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "حریم خصوصی و ذخیره‌سازی محلی",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "اطلاعات خانواده، کیف اضطراری، پیشرفت آزمون‌ها و تنظیمات بخش کودکانه مأوا صرفاً در حافظه محلی دستگاه شما ذخیره می‌شوند و هیچ داده مکانی یا شخصی به بیرون ارسال نمی‌شود.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = onReplayOnboarding,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .testTag("settings_replay_onboarding_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نمایش مجدد صفحه خوشامدگویی")
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
