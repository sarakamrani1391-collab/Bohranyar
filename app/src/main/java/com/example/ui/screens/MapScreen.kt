package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ShelterPointEntity
import com.example.data.local.entity.ShelterType
import com.example.data.system.GeoDistanceHelper
import com.example.data.system.PersianTimeFormatter
import com.example.data.system.UserLocationSnapshot
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.dialNumber
import com.example.ui.components.openExternalMapCoordinates
import com.example.ui.theme.DarkOlive
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LightSage
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.viewmodel.MapUiState
import com.example.ui.viewmodel.ShelterWithDistance
import java.util.Locale
import kotlin.math.hypot

@Composable
fun MapScreen(
    uiState: MapUiState,
    onSelectFilter: (ShelterType?) -> Unit,
    onSelectShelter: (ShelterPointEntity?) -> Unit,
    onFetchLocation: () -> Unit,
    onAddSafePoint: (name: String, address: String, note: String) -> Unit,
    onDeleteShelter: (Int) -> Unit,
    onRefreshOfflineSnapshot: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) onFetchLocation()
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "نقشه و نقاط امن",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Offline Map Snapshot Status + Controls
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (uiState.isOnline) "نقشه آنلاین / آفلاین آماده" else "حالت نقشه آفلاین ذخیره‌شده",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "آخرین ذخیره آفلاین: ${PersianTimeFormatter.formatTimestamp(uiState.lastCachedMillis)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = onRefreshOfflineSnapshot,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("map_save_offline_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.SaveAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ذخیره آفلاین")
                                }
                            }
                            uiState.locationMessage?.let { msg ->
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Interactive Vector Tactical Map Canvas
                item {
                    OfflineTacticalMapCanvas(
                        shelters = uiState.shelters,
                        userLocation = uiState.userLocation,
                        selectedShelter = uiState.selectedShelter,
                        onSelectShelter = onSelectShelter
                    )
                }

                // Location & Add Safe Point Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("map_locate_me_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            if (uiState.isFetchingLocation) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("موقعیت من")
                        }
                        OutlinedButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("map_add_safe_point_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Filled.AddLocationAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ثبت نقطه امن")
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
                            label = { Text("همه (${uiState.shelters.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        ShelterType.entries.forEach { type ->
                            FilterChip(
                                selected = uiState.selectedFilter == type,
                                onClick = { onSelectFilter(type) },
                                label = { Text(type.titleFa) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                // Selected Shelter highlight card
                uiState.selectedShelter?.let { selected ->
                    item {
                        ShelterItemCard(
                            item = uiState.shelters.firstOrNull { it.shelter.id == selected.id }
                                ?: ShelterWithDistance(selected, null),
                            isHighlighted = true,
                            onNavigateExternal = {
                                openExternalMapCoordinates(
                                    context,
                                    selected.latitude,
                                    selected.longitude,
                                    selected.name
                                )
                            },
                            onDial = {
                                if (selected.phone.isNotBlank()) dialNumber(context, selected.phone)
                            },
                            onDelete = { onDeleteShelter(selected.id) },
                            onSelect = { onSelectShelter(selected) }
                        )
                    }
                }

                // Shelters list
                items(uiState.shelters, key = { it.shelter.id }) { item ->
                    ShelterItemCard(
                        item = item,
                        isHighlighted = uiState.selectedShelter?.id == item.shelter.id,
                        onNavigateExternal = {
                            openExternalMapCoordinates(
                                context,
                                item.shelter.latitude,
                                item.shelter.longitude,
                                item.shelter.name
                            )
                        },
                        onDial = {
                            if (item.shelter.phone.isNotBlank()) dialNumber(context, item.shelter.phone)
                        },
                        onDelete = { onDeleteShelter(item.shelter.id) },
                        onSelect = { onSelectShelter(item.shelter) }
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddSafePointDialog(
            hasCurrentLocation = uiState.userLocation != null,
            onDismiss = { showAddDialog = false },
            onSave = { name, address, note ->
                onAddSafePoint(name, address, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun OfflineTacticalMapCanvas(
    shelters: List<ShelterWithDistance>,
    userLocation: UserLocationSnapshot?,
    selectedShelter: ShelterPointEntity?,
    onSelectShelter: (ShelterPointEntity?) -> Unit
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val centerLat = userLocation?.latitude
        ?: shelters.firstOrNull()?.shelter?.latitude
        ?: 35.7000
    val centerLon = userLocation?.longitude
        ?: shelters.firstOrNull()?.shelter?.longitude
        ?: 51.3900

    Card(
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        colors = CardDefaults.cardColors(containerColor = LightSage.copy(alpha = 0.55f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(22.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        zoom = (zoom * gestureZoom).coerceIn(0.6f, 3.5f)
                        panOffset += pan
                    }
                }
                .pointerInput(shelters, zoom, panOffset, centerLat, centerLon) {
                    detectTapGestures { tap ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val scalePx = minOf(w, h) * 18f * zoom
                        var closest: ShelterPointEntity? = null
                        var bestDist = 48f
                        shelters.forEach { entry ->
                            val dx = ((entry.shelter.longitude - centerLon).toFloat() * scalePx) + (w / 2f) + panOffset.x
                            val dy = ((centerLat - entry.shelter.latitude).toFloat() * scalePx) + (h / 2f) + panOffset.y
                            val d = hypot(tap.x - dx, tap.y - dy)
                            if (d < bestDist) {
                                bestDist = d
                                closest = entry.shelter
                            }
                        }
                        if (closest != null) onSelectShelter(closest)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f + panOffset.x, h / 2f + panOffset.y)

                // Grid lines
                val step = 42.dp.toPx() * zoom.coerceAtLeast(0.8f)
                var x = (center.x % step)
                while (x < w) {
                    drawLine(
                        color = OliveGreen.copy(alpha = 0.14f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    x += step
                }
                var y = (center.y % step)
                while (y < h) {
                    drawLine(
                        color = OliveGreen.copy(alpha = 0.14f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    y += step
                }

                // Concentric safety radius rings around center
                listOf(55.dp, 105.dp, 155.dp).forEach { radiusDp ->
                    drawCircle(
                        color = OliveGreen.copy(alpha = 0.25f),
                        radius = radiusDp.toPx() * zoom,
                        center = center,
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }

                val scalePx = minOf(w, h) * 18f * zoom

                // Shelter pins
                shelters.forEach { entry ->
                    val s = entry.shelter
                    val px = ((s.longitude - centerLon).toFloat() * scalePx) + center.x
                    val py = ((centerLat - s.latitude).toFloat() * scalePx) + center.y
                    val pinColor = when (s.type) {
                        ShelterType.HOSPITAL -> EmergencyRed
                        ShelterType.RELIEF_CENTER -> OliveGreen
                        ShelterType.SAFE_ZONE -> SafeGreen
                        ShelterType.FAMILY_POINT -> DarkOlive
                    }
                    val isSelected = selectedShelter?.id == s.id
                    if (isSelected) {
                        drawCircle(
                            color = pinColor.copy(alpha = 0.25f),
                            radius = 20.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 11.dp.toPx() else 9.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = pinColor,
                        radius = if (isSelected) 8.dp.toPx() else 6.5.dp.toPx(),
                        center = Offset(px, py)
                    )
                }

                // User current position marker
                if (userLocation != null) {
                    drawCircle(
                        color = Color(0xFF1565C0).copy(alpha = 0.22f),
                        radius = 18.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFF1565C0),
                        radius = 5.5.dp.toPx(),
                        center = center
                    )
                }
            }

            // Legend overlay
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = EmergencyRed, label = "درمانی")
                    LegendDot(color = OliveGreen, label = "امدادی")
                    LegendDot(color = SafeGreen, label = "امن")
                    LegendDot(color = Color(0xFF1565C0), label = "شما")
                }
            }

            // Coordinates badge
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                val coordLabel = if (userLocation != null) {
                    String.format(Locale.US, "%.4f, %.4f", userLocation.latitude, userLocation.longitude)
                } else {
                    "نمای آفلاین (روی نقاط ضربه بزنید)"
                }
                Text(
                    text = coordLabel,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ShelterItemCard(
    item: ShelterWithDistance,
    isHighlighted: Boolean,
    onNavigateExternal: () -> Unit,
    onDial: () -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    val s = item.shelter
    val typeIcon = when (s.type) {
        ShelterType.HOSPITAL -> Icons.Filled.LocalHospital
        ShelterType.RELIEF_CENTER -> Icons.Filled.Shield
        ShelterType.SAFE_ZONE -> Icons.Filled.Park
        ShelterType.FAMILY_POINT -> Icons.Filled.Verified
    }

    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = if (isHighlighted) 1.5.dp else 1.dp,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        imageVector = typeIcon,
                        contentDescription = s.type.titleFa,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = s.type.titleFa + (item.distanceKm?.let { " • فاصله: ${GeoDistanceHelper.formatDistanceFa(it)}" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!s.isDemoData) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "حذف نقطه امن")
                    }
                }
            }

            if (s.isDemoData) {
                DemoDataBadge()
            } else {
                StatusBadge(
                    text = "نقطه ثبت‌شده توسط شما",
                    containerColor = SafeGreenSoft,
                    contentColor = SafeGreen,
                    icon = Icons.Filled.Verified
                )
            }

            Text(
                text = s.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (s.capacityNote.isNotBlank()) {
                Text(
                    text = s.capacityNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onNavigateExternal,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مسیریابی")
                }
                if (s.phone.isNotBlank()) {
                    OutlinedButton(
                        onClick = onDial,
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تماس ${s.phone}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSafePointDialog(
    hasCurrentLocation: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, address: String, note: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت نقطه امن جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (hasCurrentLocation) {
                        "این نقطه با مختصات فعلی GPS شما در نقشه آفلاین ذخیره می‌شود."
                    } else {
                        "موقعیت GPS فعلی دریافت نشده؛ با مختصات پیش‌فرض منطقه ذخیره می‌شود."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام نقطه امن (مثلاً پارک محل قرار خانواده)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("نشانی یا راهنما") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("یادداشت (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, address, note) },
                enabled = name.isNotBlank()
            ) {
                Text("ذخیره نقطه")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
