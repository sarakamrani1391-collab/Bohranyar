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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.SosStatus
import com.example.data.system.PersianTimeFormatter
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.dialNumber
import com.example.ui.components.openSmsComposer
import com.example.ui.components.shareEmergencyText
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.SosUiState

@Composable
fun SosScreen(
    uiState: SosUiState,
    onSelectType: (String) -> Unit,
    onUpdateAddressNote: (String) -> Unit,
    onUpdateNote: (String) -> Unit,
    onSubmitSos: () -> Unit,
    onMarkShared: (Int) -> Unit,
    onSyncQueued: () -> Unit,
    onDeleteRequest: (Int) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val emergencyTypes = listOf(
        "درخواست امداد فوری",
        "اورژانس پزشکی",
        "آتش‌سوزی / حریق",
        "محبوس شدن",
        "سیل و آب‌گرفتگی",
        "نیاز به پناهگاه"
    )

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "درخواست کمک اضطراری",
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
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { dialNumber(context, "115") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sos_dial_115")
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تماس ۱۱۵")
                        }
                        Button(
                            onClick = { dialNumber(context, "112") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sos_dial_112")
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("هلال احمر ۱۱۲")
                        }
                        Button(
                            onClick = { dialNumber(context, "125") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sos_dial_125")
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("آتش‌نشانی ۱۲۵")
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "۱. نوع وضعیت اضطراری",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                emergencyTypes.forEach { type ->
                                    FilterChip(
                                        selected = uiState.selectedEmergencyType == type,
                                        onClick = { onSelectType(type) },
                                        label = { Text(type) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmergencyRed,
                                            selectedLabelColor = MaterialTheme.colorScheme.onError
                                        ),
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = uiState.addressNote,
                                onValueChange = onUpdateAddressNote,
                                label = { Text("نشانی یا محل تقریبی (اختیاری)") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sos_address_input")
                            )

                            OutlinedTextField(
                                value = uiState.customNote,
                                onValueChange = onUpdateNote,
                                label = { Text("توضیحات تکمیلی (مثلاً تعداد افراد یا نوع آسیب)") },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sos_note_input")
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "پیش‌نمایش پیام آماده اضطراری:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.buildPreparedSmsText(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Button(
                                onClick = { showConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmergencyRed,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .testTag("sos_trigger_button")
                            ) {
                                Icon(Icons.Filled.Sos, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (uiState.isOnline) {
                                        "ثبت و آماده‌سازی درخواست کمک"
                                    } else {
                                        "ثبت در صف آفلاین و ارسال پیام"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        openSmsComposer(context, "", uiState.buildPreparedSmsText())
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("sos_sms_button"),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Filled.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ارسال با پیامک")
                                }
                                OutlinedButton(
                                    onClick = {
                                        shareEmergencyText(context, uiState.buildPreparedSmsText())
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("sos_share_button"),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اشتراک‌گذاری")
                                }
                            }
                        }
                    }
                }

                val queuedCount = uiState.history.count { it.status == SosStatus.QUEUED_OFFLINE }
                if (queuedCount > 0) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = WarningAmberSoft),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CloudOff, contentDescription = null, tint = WarningAmber)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$queuedCount درخواست در صف آفلاین منتظر اتصال اینترنت است.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (uiState.isOnline) {
                                    TextButton(onClick = onSyncQueued) {
                                        Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ارسال صف")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "تاریخچه درخواست‌های ثبت‌شده (${uiState.history.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.history, key = { it.id }) { req ->
                    val (bg, fg, icon) = when (req.status) {
                        SosStatus.QUEUED_OFFLINE -> Triple(WarningAmberSoft, WarningAmber, Icons.Filled.CloudOff)
                        SosStatus.SENT_SYNCED -> Triple(SafeGreenSoft, SafeGreen, Icons.Filled.CheckCircle)
                        SosStatus.SHARED_BY_USER -> Triple(SafeGreenSoft, SafeGreen, Icons.Filled.Sms)
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = req.status.titleFa,
                                    containerColor = bg,
                                    contentColor = fg,
                                    icon = icon
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = PersianTimeFormatter.formatTimestamp(req.createdAtMillis),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    IconButton(
                                        onClick = { onDeleteRequest(req.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "حذف درخواست",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Text(
                                text = req.emergencyType,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = req.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = {
                                    openSmsComposer(context, "", req.message)
                                    onMarkShared(req.id)
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("ارسال پیامکی")
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("تأیید ثبت درخواست کمک") },
            text = {
                Text(
                    if (uiState.isOnline) {
                        "درخواست شما با مشخصات انتخاب‌شده ثبت می‌شود. برای امداد فوری حتماً با ۱۱۲ یا ۱۱۵ نیز تماس بگیرید یا پیامک ارسال کنید."
                    } else {
                        "به دلیل عدم دسترسی به اینترنت، درخواست در حافظه دستگاه ذخیره می‌شود تا پس از برقراری اتصال همگام‌سازی گردد. همچنین می‌توانید بلافاصله با پیامک ارسال کنید."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmitSos()
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.testTag("sos_confirm_dialog_button")
                ) {
                    Text("تأیید و ثبت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
