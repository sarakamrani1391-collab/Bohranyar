package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ScenarioOption
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.ScenarioLabUiState

@Composable
fun ScenarioLabScreen(
    uiState: ScenarioLabUiState,
    isOnline: Boolean,
    onSelectScenario: (Int) -> Unit,
    onChooseOption: (ScenarioOption) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scenario = uiState.scenarios.getOrNull(uiState.activeScenarioIndex)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "آزمایشگاه سناریوهای بحران",
                onBackClick = onBack,
                isOnline = isOnline
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (scenario == null) return@Box
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تصمیم‌گیری آرام و علمی در شرایط واقعی",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            StatusBadge(
                                text = "${uiState.completedScenarioIds.size}/${uiState.scenarios.size} تکمیل‌شده",
                                containerColor = SafeGreenSoft,
                                contentColor = SafeGreen
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.scenarios.forEachIndexed { idx, item ->
                            val isDone = uiState.completedScenarioIds.contains(item.id)
                            FilterChip(
                                selected = idx == uiState.activeScenarioIndex,
                                onClick = { onSelectScenario(idx) },
                                leadingIcon = if (isDone) {
                                    { Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                label = { Text(item.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("scenario_chip_${item.id}")
                            )
                        }
                    }
                }

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
                                text = scenario.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = scenario.situationDescription,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Surface(
                                color = WarningAmberSoft,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "اشتباهات رایج در این موقعیت:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    scenario.commonMistakes.forEach { mistake ->
                                        Text("• $mistake", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "انتخاب اقدام شما:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        scenario.options.forEach { option ->
                            val selected = uiState.selectedOption
                            val isChosen = selected?.id == option.id
                            val bg = when {
                                selected == null -> MaterialTheme.colorScheme.surface
                                isChosen && option.isSafeAndCorrect -> SafeGreenSoft
                                isChosen && !option.isSafeAndCorrect -> EmergencyRedSoft
                                option.isSafeAndCorrect -> SafeGreenSoft.copy(alpha = 0.45f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }
                            val border = when {
                                selected == null -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                isChosen && option.isSafeAndCorrect -> SafeGreen
                                isChosen && !option.isSafeAndCorrect -> EmergencyRed
                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                            Surface(
                                onClick = { if (selected == null) onChooseOption(option) },
                                color = bg,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, border),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .testTag("scenario_opt_${option.id}")
                            ) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }

                uiState.selectedOption?.let { chosen ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (chosen.isSafeAndCorrect) SafeGreenSoft else WarningAmberSoft
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                if (chosen.isSafeAndCorrect) SafeGreen.copy(alpha = 0.4f) else WarningAmber.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = chosen.outcomeTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (chosen.isSafeAndCorrect) SafeGreen else EmergencyRed
                                )
                                Text(
                                    text = "دلیل علمی و ایمنی: ${chosen.scientificAndSafetyReason}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "توصیه تکمیلی: ${chosen.correctiveAdvice}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = onRetry,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تلاش مجدد")
                                    }
                                    if (uiState.activeScenarioIndex + 1 < uiState.scenarios.size) {
                                        Button(
                                            onClick = { onSelectScenario(uiState.activeScenarioIndex + 1) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                        ) {
                                            Text("سناریوی بعدی")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}
