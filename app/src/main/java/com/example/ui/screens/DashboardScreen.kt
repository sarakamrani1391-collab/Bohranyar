package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.MavaCharacterId
import com.example.data.system.PersianTimeFormatter
import com.example.ui.components.ConnectivityChip
import com.example.ui.components.EmergencyDialCard
import com.example.ui.components.MavaCharacterAvatar
import com.example.ui.components.OfflineStatusBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.dialNumber
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.MavaInk
import com.example.ui.theme.MavaInkSoft
import com.example.ui.theme.MavaLavender
import com.example.ui.theme.MavaPink
import com.example.ui.viewmodel.DashboardUiState

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onNavigateToSos: () -> Unit,
    onNavigateToCrises: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToEncyclopedia: () -> Unit,
    onNavigateToWhyItHappens: () -> Unit,
    onNavigateToVirtualLab: () -> Unit,
    onNavigateToScenarioLab: () -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToKit: () -> Unit,
    onNavigateToMava: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAllNumbers by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.userName.isNotBlank()) "سلام، ${uiState.userName}" else "همیار بحران",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "منطقه: ${uiState.userCity} • آمادگی، آموزش علمی و ایمنی خانواده",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ConnectivityChip(isOnline = uiState.isOnline)
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("dashboard_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "تنظیمات"
                            )
                        }
                    }
                }
            }

            // Offline & last sync banner
            item {
                OfflineStatusBanner(
                    isOnline = uiState.isOnline,
                    lastSyncFormatted = PersianTimeFormatter.formatTimestamp(uiState.lastOfflineSyncMillis),
                    queuedCount = uiState.queuedSosCount
                )
            }

            // Emergency SOS primary action card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedSoft),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.5.dp, EmergencyRed.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وضعیت اضطراری",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmergencyRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تماس سریع، پیام آماده و ثبت آفلاین درخواست کمک",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onNavigateToSos,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmergencyRed,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("dashboard_sos_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Sos,
                                contentDescription = "درخواست کمک اضطراری",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "کمک فوری",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Overall Preparedness Card
            item {
                PreparednessStatusCard(
                    percent = uiState.preparednessPercent,
                    kitPrepared = uiState.kitPreparedCount,
                    kitTotal = uiState.kitTotalCount,
                    familyCount = uiState.familyCount,
                    hasFamilyPlan = uiState.hasFamilyPlan,
                    quizzesDone = uiState.quizzesCompletedCount,
                    onClickKit = onNavigateToKit
                )
            }

            // Distinct Toddler Sanctuary Entry Card ("مأوا")
            item {
                Card(
                    onClick = onNavigateToMava,
                    colors = CardDefaults.cardColors(containerColor = MavaLavender.copy(alpha = 0.78f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, MavaPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tile_mava")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MavaCharacterAvatar(character = MavaCharacterId.PUFI, size = 66.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ChildCare, contentDescription = null, tint = MavaInk, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "بخش کودکانه «مأوا»",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MavaInk
                                )
                            }
                            Text(
                                text = "دنیای کوچک آرامش و لبخند (۱ تا ۳ سال) • قصه، بازی، آرامش و کنترل والدین",
                                style = MaterialTheme.typography.bodySmall,
                                color = MavaInkSoft
                            )
                        }
                    }
                }
            }

            // All Core Sections Grid
            item {
                SectionHeader(
                    title = "بخش‌های آموزشی، علمی و آمادگی",
                    subtitle = "همه بخش‌ها بدون نیاز به اینترنت در دسترس هستند"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickModuleTile(
                            title = "راهنمای بحران‌ها",
                            subtitle = "۸ بحران و چک‌لیست",
                            icon = Icons.Filled.MenuBook,
                            onClick = onNavigateToCrises,
                            testTag = "tile_crises",
                            modifier = Modifier.weight(1f)
                        )
                        QuickModuleTile(
                            title = "آموزش کمک‌های اولیه",
                            subtitle = "۷ درس ویدئویی + آزمون",
                            icon = Icons.Filled.MedicalServices,
                            onClick = onNavigateToTraining,
                            testTag = "tile_training",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickModuleTile(
                            title = "دانشنامه علم و بحران",
                            subtitle = "علوم هفتم، هشتم و نهم",
                            icon = Icons.Filled.Science,
                            onClick = onNavigateToEncyclopedia,
                            testTag = "tile_encyclopedia",
                            modifier = Modifier.weight(1f)
                        )
                        QuickModuleTile(
                            title = "چرا این اتفاق می‌افتد؟",
                            subtitle = "پرسش‌های علمی کاربردی",
                            icon = Icons.Filled.HelpOutline,
                            onClick = onNavigateToWhyItHappens,
                            testTag = "tile_why",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickModuleTile(
                            title = "آزمایشگاه مجازی",
                            subtitle = "۶ شبیه‌سازی تعاملی",
                            icon = Icons.Filled.Biotech,
                            onClick = onNavigateToVirtualLab,
                            testTag = "tile_virtual_lab",
                            modifier = Modifier.weight(1f)
                        )
                        QuickModuleTile(
                            title = "آزمایشگاه سناریوها",
                            subtitle = "۶ موقعیت تصمیم‌گیری",
                            icon = Icons.Filled.Psychology,
                            onClick = onNavigateToScenarioLab,
                            testTag = "tile_scenario_lab",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickModuleTile(
                            title = "خانواده و نزدیکان",
                            subtitle = "${uiState.familyCount} عضو ثبت‌شده",
                            icon = Icons.Filled.FamilyRestroom,
                            onClick = onNavigateToFamily,
                            testTag = "tile_family",
                            modifier = Modifier.weight(1f)
                        )
                        QuickModuleTile(
                            title = "کیف اضطراری و ابزار",
                            subtitle = "${uiState.kitPreparedCount} از ${uiState.kitTotalCount} آماده",
                            icon = Icons.Filled.Backpack,
                            onClick = onNavigateToKit,
                            testTag = "tile_kit",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Essential Emergency Numbers
            item {
                SectionHeader(
                    title = "شماره‌های ضروری (تماس مستقیم آفلاین)",
                    actionText = if (showAllNumbers) "نمایش کمتر" else "همه شماره‌ها",
                    onActionClick = { showAllNumbers = !showAllNumbers }
                )
                Spacer(modifier = Modifier.height(10.dp))
                val numbers = if (showAllNumbers) {
                    uiState.emergencyNumbers
                } else {
                    uiState.emergencyNumbers.filter { it.isPrimary }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    numbers.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { contact ->
                                EmergencyDialCard(
                                    title = contact.title,
                                    number = contact.number,
                                    description = contact.description,
                                    isPrimary = contact.isPrimary,
                                    onDial = { dialNumber(context, contact.number) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun PreparednessStatusCard(
    percent: Int,
    kitPrepared: Int,
    kitTotal: Int,
    familyCount: Int,
    hasFamilyPlan: Boolean,
    quizzesDone: Int,
    onClickKit: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer

    Card(
        onClick = onClickKit,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.testTag("preparedness_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(84.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = (percent / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$percent٪",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "سطح آمادگی کلی شما",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when {
                        percent >= 75 -> "عالی! برنامه خانواده و کیف شما در وضعیت خوبی است."
                        percent >= 40 -> "در مسیر خوب هستید؛ تکمیل کیف اضطراری امتیاز شما را بالا می‌برد."
                        else -> "برای شروع، اقلام کیف اضطراری و برنامه خانواده را تکمیل کنید."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniStatPill("کیف: $kitPrepared/$kitTotal")
                    MiniStatPill(if (hasFamilyPlan) "برنامه خانواده ✓" else "خانواده: $familyCount")
                    MiniStatPill("آزمون: $quizzesDone")
                }
            }
        }
    }
}

@Composable
private fun MiniStatPill(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun QuickModuleTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
