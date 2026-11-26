package com.example.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.entity.UrbanReportCategory
import com.example.data.local.entity.UrbanReportStatus
import com.example.data.system.PersianTimeFormatter
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.ReportUiState
import java.io.File
import java.util.Locale

@Composable
fun UrbanReportScreen(
    uiState: ReportUiState,
    onSelectFilter: (UrbanReportCategory?) -> Unit,
    onFetchLocation: () -> Unit,
    onSubmitReport: (
        category: UrbanReportCategory,
        title: String,
        description: String,
        imageUri: Uri?,
        attachLocation: Boolean
    ) -> Unit,
    onSyncQueued: () -> Unit,
    onDeleteReport: (Int) -> Unit,
    onDismissBanner: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "گزارش مشکلات شهری",
                onBackClick = onBack,
                isOnline = uiState.isOnline
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("report_add_fab")
            ) {
                Icon(Icons.Filled.AddCircleOutline, contentDescription = "ثبت گزارش جدید")
            }
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Transparency notice: unverified reports are never presented as official alerts
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "شفافیت اطلاعات",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "گزارش‌های ثبت‌شده تا پیش از بررسی رسمی با برچسب «تأییدنشده» نمایش داده می‌شوند و به عنوان هشدار قطعی تلقی نمی‌گردند.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                uiState.statusBanner?.let { msg ->
                    item {
                        Surface(
                            color = SafeGreenSoft,
                            shape = RoundedCornerShape(14.dp),
                            onClick = onDismissBanner
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

                val queuedCount = uiState.reports.count { it.status == UrbanReportStatus.QUEUED_OFFLINE }
                if (queuedCount > 0 && uiState.isOnline) {
                    item {
                        OutlinedButton(
                            onClick = onSyncQueued,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ارسال $queuedCount گزارش ذخیره‌شده در حالت آفلاین")
                        }
                    }
                }

                // Category filters
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedFilter == null,
                            onClick = { onSelectFilter(null) },
                            label = { Text("همه (${uiState.reports.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        UrbanReportCategory.entries.forEach { cat ->
                            FilterChip(
                                selected = uiState.selectedFilter == cat,
                                onClick = { onSelectFilter(cat) },
                                label = { Text(cat.titleFa) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                if (uiState.reports.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Filled.ReportProblem,
                            title = "گزارشی در این دسته ثبت نشده است",
                            subtitle = "با دکمه + می‌توانید انسداد مسیر، آب‌گرفتگی یا خطر محیطی را حتی بدون اینترنت ثبت کنید."
                        )
                    }
                } else {
                    items(uiState.reports, key = { it.id }) { report ->
                        val (bg, fg, icon) = when (report.status) {
                            UrbanReportStatus.QUEUED_OFFLINE -> Triple(WarningAmberSoft, WarningAmber, Icons.Filled.CloudOff)
                            UrbanReportStatus.SUBMITTED_UNVERIFIED -> Triple(WarningAmberSoft, WarningAmber, Icons.Filled.Info)
                            UrbanReportStatus.VERIFIED -> Triple(SafeGreenSoft, SafeGreen, Icons.Filled.Verified)
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
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (report.isDemoData) {
                                            DemoDataBadge()
                                        } else {
                                            StatusBadge(
                                                text = report.status.titleFa,
                                                containerColor = bg,
                                                contentColor = fg,
                                                icon = icon
                                            )
                                        }
                                        StatusBadge(
                                            text = report.category.titleFa,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (!report.isDemoData) {
                                        IconButton(
                                            onClick = { onDeleteReport(report.id) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(Icons.Filled.Delete, contentDescription = "حذف گزارش")
                                        }
                                    }
                                }

                                Text(
                                    text = report.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = report.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!report.imagePath.isNullOrBlank()) {
                                    val imgFile = File(report.imagePath)
                                    if (imgFile.exists()) {
                                        AsyncImage(
                                            model = imgFile,
                                            contentDescription = "تصویر پیوست گزارش",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp)
                                                .clip(RoundedCornerShape(14.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (report.locationConsentGiven && report.latitude != null && report.longitude != null) {
                                            "موقعیت: ${String.format(Locale.US, "%.4f, %.4f", report.latitude, report.longitude)}"
                                        } else {
                                            "موقعیت مکانی پیوست نشده"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = PersianTimeFormatter.formatTimestamp(report.createdAtMillis),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    if (showCreateDialog) {
        CreateUrbanReportDialog(
            uiState = uiState,
            onFetchLocation = onFetchLocation,
            onDismiss = { showCreateDialog = false },
            onSubmit = { cat, title, desc, uri, attachLoc ->
                onSubmitReport(cat, title, desc, uri, attachLoc)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreateUrbanReportDialog(
    uiState: ReportUiState,
    onFetchLocation: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: (UrbanReportCategory, String, String, Uri?, Boolean) -> Unit
) {
    var category by rememberSaveable { mutableStateOf(UrbanReportCategory.ROAD_BLOCKAGE) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedImageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var attachLocation by rememberSaveable { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUriString = uri?.toString() }
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            attachLocation = true
            onFetchLocation()
        } else {
            attachLocation = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت گزارش مشکل شهری") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UrbanReportCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.titleFa) }
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان کوتاه *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("report_title_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات وضعیت و نشانی *") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("report_description_input")
                )

                // Zero-permission Photo Picker
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("report_pick_photo_button")
                ) {
                    Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (selectedImageUriString == null) "پیوست تصویر (اختیاری)" else "تصویر انتخاب شد ✓ (تغییر)")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ثبت موقعیت مکانی با رضایت شما", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = attachLocation,
                        onCheckedChange = { checked ->
                            if (checked) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                )
                            } else {
                                attachLocation = false
                            }
                        }
                    )
                }

                if (attachLocation) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isFetchingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = uiState.currentLocation?.let {
                                String.format(Locale.US, "%.4f, %.4f", it.latitude, it.longitude)
                            } ?: "در حال دریافت موقعیت...",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uri = selectedImageUriString?.let { Uri.parse(it) }
                    onSubmit(category, title, description, uri, attachLocation)
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.testTag("report_submit_button")
            ) {
                Text("ثبت گزارش")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
