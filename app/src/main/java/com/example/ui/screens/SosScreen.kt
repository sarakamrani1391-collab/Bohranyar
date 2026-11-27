package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Switch
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
import com.example.data.model.CrisisType
import com.example.data.system.PersianTimeFormatter
import com.example.data.system.SosLocationStatus
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.dialNumber
import com.example.ui.components.openSmsComposer
import com.example.ui.components.shareEmergencyText
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.SosUiState
import java.util.Locale

@Composable
fun SosScreen(
    uiState: SosUiState,
    onSelectCrisisType: (CrisisType) -> Unit,
    onSelectType: (String) -> Unit,
    onUpdateAddressNote: (String) -> Unit,
    onUpdateNote: (String) -> Unit,
    onToggleContact: (String) -> Unit,
    onSetLocationConsent: (Boolean) -> Unit,
    onLocationPermissionResult: (Boolean) -> Unit,
    onSubmitSos: (Boolean) -> Unit,
    onStopLocationSharing: () -> Unit,
    onResolveSos: () -> Unit,
    onCancelSos: () -> Unit,
    onMarkShared: (Int) -> Unit,
    onSyncQueued: () -> Unit,
    onDeleteRequest: (Int) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var shareLocationWithActivation by rememberSaveable { mutableStateOf(true) }
    var pendingSubmitAfterPermission by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onLocationPermissionResult(granted)
        if (pendingSubmitAfterPermission) {
            pendingSubmitAfterPermission = false
            onSubmitSos(granted)
        }
    }

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
                // Active SOS Session & Live Location Sharing Status Banner
                if (uiState.activeSosRequestId != null) {
                    item {
                        ActiveSosSessionCard(
                            uiState = uiState,
                            onSendLiveLocationSms = {
                                val targetPhones = uiState.buildSelectedContactPhones()
                                openSmsComposer(context, targetPhones, uiState.buildPreparedSmsText())
                                onMarkShared(uiState.activeSosRequestId)
                            },
                            onRequestLocationPermission = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            onOpenLocationSettings = {
                                runCatching {
                                    context.startActivity(
                                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                    )
                                }
                            },
                            onStopLocationSharing = onStopLocationSharing,
                            onResolveSos = onResolveSos,
                            onCancelSos = onCancelSos
                        )
                    }
                }

                // Direct emergency dialing buttons
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

                // SOS Form Card
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
                                text = "۱. نوع بحران را انتخاب کنید",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CrisisType.entries.forEach { crisis ->
                                    FilterChip(
                                        selected = uiState.selectedCrisisType == crisis,
                                        onClick = { onSelectCrisisType(crisis) },
                                        label = { Text(crisis.titleFa) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("sos_crisis_${crisis.id}")
                                    )
                                }
                            }

                            Text(
                                text = "۲. نوع نیاز اضطراری",
                                style = MaterialTheme.typography.titleSmall,
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

                            // Emergency Contacts Selection
                            Text(
                                text = "۳. مخاطبین اضطراری برای ارسال پیامک و موقعیت",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.availableContacts.isEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "هنوز مخاطب خانوادگی ثبت نشده است؛ می‌توانید از بخش «خانواده و نزدیکان» شماره اضافه کنید یا هنگام ارسال پیامک، گیرنده را دستی انتخاب کنید.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    uiState.availableContacts.forEach { contact ->
                                        val isSelected = contact.id in uiState.selectedContactIds
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { onToggleContact(contact.id) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            },
                                            label = { Text("${contact.name} (${contact.relation})") },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("sos_contact_${contact.id}")
                                        )
                                    }
                                }
                            }

                            // Consent Toggle for SOS-scoped Live Location Sharing
                            Surface(
                                color = if (uiState.locationConsentGiven) SafeGreenSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (uiState.locationConsentGiven) SafeGreen.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "اشتراک‌گذاری موقعیت لحظه‌ای در وضعیت اضطراری",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "موقعیت مکانی فقط هنگام فعال‌بودن درخواست SOS و با رضایت شما دریافت می‌شود؛ خارج از SOS هیچ ردیابی انجام نمی‌شود.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = uiState.locationConsentGiven,
                                        onCheckedChange = { checked ->
                                            shareLocationWithActivation = checked
                                            onSetLocationConsent(checked)
                                            if (checked && !uiState.hasLocationPermission) {
                                                locationPermissionLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            }
                                        },
                                        modifier = Modifier.testTag("sos_location_consent_switch")
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
                                onClick = {
                                    shareLocationWithActivation = uiState.locationConsentGiven || !uiState.hasLocationPermission
                                    showConfirmDialog = true
                                },
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
                                        "ثبت و فعال‌سازی درخواست کمک (SOS)"
                                    } else {
                                        "ثبت در صف آفلاین و فعال‌سازی کمک"
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
                                        openSmsComposer(
                                            context,
                                            uiState.buildSelectedContactPhones(),
                                            uiState.buildPreparedSmsText()
                                        )
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
                        SosStatus.RESOLVED -> Triple(SafeGreenSoft, SafeGreen, Icons.Filled.CheckCircle)
                        SosStatus.CANCELLED -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, Icons.Filled.Cancel)
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
                            if (req.latitude != null && req.longitude != null) {
                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "مختصات ثبت‌شده: %.5f, %.5f",
                                        req.latitude,
                                        req.longitude
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SafeGreen
                                )
                            }
                            Text(
                                text = req.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = {
                                    openSmsComposer(context, uiState.buildSelectedContactPhones(), req.message)
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
            title = { Text("فعال‌سازی درخواست کمک اضطراری") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (uiState.isOnline) {
                            "درخواست SOS برای بحران «${uiState.selectedCrisisType.titleFa}» ثبت می‌شود."
                        } else {
                            "به دلیل قطع اینترنت، درخواست SOS در صف آفلاین ثبت می‌شود و می‌توانید پیامک اضطراری را فوراً ارسال کنید."
                        }
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "توضیح دسترسی موقعیت مکانی: برای کمک به امدادگران و مخاطبین منتخب، مختصات GPS فقط تا زمانی که این وضعیت اضطراری فعال است به‌روزرسانی می‌شود و بلافاصله با لغو یا پایان SOS متوقف خواهد شد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "اشتراک‌گذاری موقعیت لحظه‌ای GPS",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = shareLocationWithActivation,
                                onCheckedChange = { shareLocationWithActivation = it }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        if (shareLocationWithActivation && !uiState.hasLocationPermission) {
                            pendingSubmitAfterPermission = true
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            onSubmitSos(shareLocationWithActivation)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.testTag("sos_confirm_dialog_button")
                ) {
                    Text("تأیید و فعال‌سازی SOS")
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

@Composable
private fun ActiveSosSessionCard(
    uiState: SosUiState,
    onSendLiveLocationSms: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onStopLocationSharing: () -> Unit,
    onResolveSos: () -> Unit,
    onCancelSos: () -> Unit
) {
    val isLive = uiState.isLocationSharingActive
    val containerColor = when {
        isLive -> SafeGreenSoft
        uiState.locationStatus == SosLocationStatus.PERMISSION_DENIED ||
            uiState.locationStatus == SosLocationStatus.GPS_UNAVAILABLE -> WarningAmberSoft
        else -> EmergencyRedSoft
    }
    val accentColor = when {
        isLive -> SafeGreen
        uiState.locationStatus == SosLocationStatus.PERMISSION_DENIED ||
            uiState.locationStatus == SosLocationStatus.GPS_UNAVAILABLE -> WarningAmber
        else -> EmergencyRed
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_active_session_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = when {
                            isLive -> Icons.Filled.GpsFixed
                            uiState.locationStatus == SosLocationStatus.PERMISSION_DENIED -> Icons.Filled.LocationOff
                            uiState.locationStatus == SosLocationStatus.GPS_UNAVAILABLE -> Icons.Filled.GpsOff
                            else -> Icons.Filled.LocationOn
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLive) {
                            "وضعیت اضطراری فعال • اشتراک‌گذاری موقعیت لحظه‌ای روشن است"
                        } else {
                            "درخواست SOS فعال • ${uiState.locationStatus.titleFa}"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (isLive && uiState.liveLocation != null) {
                val loc = uiState.liveLocation
                val accuracy = loc.accuracyMeters?.let { " (دقت حدود ${it.toInt()} متر)" } ?: ""
                Text(
                    text = String.format(
                        Locale.US,
                        "مختصات زنده: %.5f, %.5f%s",
                        loc.latitude,
                        loc.longitude,
                        accuracy
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SafeGreen
                )
                Text(
                    text = "به‌روزرسانی خودکار هر ۱۰ ثانیه تا زمان لغو یا حل وضعیت اضطراری فعال است.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (uiState.locationStatus == SosLocationStatus.PERMISSION_DENIED) {
                Text(
                    text = "دسترسی موقعیت مکانی رد شد. می‌توانید دوباره مجوز بدهید یا نشانی را به‌صورت متنی وارد و ارسال کنید.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedButton(
                    onClick = onRequestLocationPermission,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("اعطای مجوز موقعیت مکانی")
                }
            } else if (uiState.locationStatus == SosLocationStatus.GPS_UNAVAILABLE) {
                Text(
                    text = "GPS یا موقعیت‌یاب دستگاه خاموش/خارج از دسترس است. برنامه بدون خطا با پیام متنی ادامه می‌دهد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedButton(
                    onClick = onOpenLocationSettings,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("باز کردن تنظیمات موقعیت‌یاب (GPS)")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSendLiveLocationSms,
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("sos_send_live_sms_button")
                ) {
                    Icon(Icons.Filled.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ارسال موقعیت به مخاطبین")
                }
                if (isLive) {
                    OutlinedButton(
                        onClick = onStopLocationSharing,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("sos_stop_location_button")
                    ) {
                        Text("توقف موقعیت")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onResolveSos,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("sos_resolve_button")
                ) {
                    Text("حل شد / پایان SOS")
                }
                OutlinedButton(
                    onClick = onCancelSos,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("sos_cancel_button")
                ) {
                    Text("لغو درخواست")
                }
            }
        }
    }
}
