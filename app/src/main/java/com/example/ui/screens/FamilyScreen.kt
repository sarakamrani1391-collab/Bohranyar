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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.system.PersianTimeFormatter
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.dialNumber
import com.example.ui.components.openSmsComposer
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.FamilyUiState

@Composable
fun FamilyScreen(
    uiState: FamilyUiState,
    isOnline: Boolean,
    onAddMember: (name: String, relation: String, phone: String, bloodType: String, medicalNotes: String) -> Unit,
    onUpdateSafetyStatus: (id: Int, status: FamilySafetyStatus) -> Unit,
    onDeleteMember: (id: Int) -> Unit,
    onSavePlan: (primary: String, secondary: String, outName: String, outPhone: String, notes: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showEditPlanDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "خانواده و افراد نزدیک",
                onBackClick = onBack,
                isOnline = isOnline
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("family_add_member_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "افزودن عضو خانواده")
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
                // Family Communication Plan Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "برنامه ارتباطی خانواده (آفلاین)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    onClick = { showEditPlanDialog = true },
                                    modifier = Modifier.testTag("family_edit_plan_button")
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ویرایش برنامه")
                                }
                            }
                            PlanLine(
                                label = "محل تجمع اول (نزدیک منزل):",
                                value = uiState.plan.primaryMeetingPoint.ifBlank { "تعیین نشده" }
                            )
                            PlanLine(
                                label = "محل تجمع دوم (فضای باز منطقه):",
                                value = uiState.plan.secondaryMeetingPoint.ifBlank { "تعیین نشده" }
                            )
                            val outContact = listOf(uiState.plan.outOfCityContactName, uiState.plan.outOfCityContactPhone)
                                .filter { it.isNotBlank() }
                                .joinToString(" • ")
                            PlanLine(
                                label = "رابط خارج از شهر:",
                                value = outContact.ifBlank { "تعیین نشده" }
                            )
                            if (uiState.plan.emergencyNotes.isNotBlank()) {
                                PlanLine(label = "یادداشت اضطراری:", value = uiState.plan.emergencyNotes)
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "فهرست اعضای خانواده (${uiState.members.size})",
                        subtitle = "اطلاعات تماس و وضعیت ایمنی در حافظه آفلاین نگهداری می‌شود"
                    )
                }

                if (uiState.members.isEmpty()) {
                    item {
                        EmptyStateCard(
                            icon = Icons.Filled.FamilyRestroom,
                            title = "هنوز عضوی ثبت نشده است",
                            subtitle = "با دکمه + اعضای خانواده را اضافه کنید تا در بحران دسترسی فوری به تماس و وضعیت ایمنی داشته باشید."
                        )
                    }
                } else {
                    items(uiState.members, key = { it.id }) { member ->
                        val (bg, fg, icon) = when (member.safetyStatus) {
                            FamilySafetyStatus.SAFE -> Triple(SafeGreenSoft, SafeGreen, Icons.Filled.CheckCircle)
                            FamilySafetyStatus.NEEDS_HELP -> Triple(EmergencyRedSoft, EmergencyRed, Icons.Filled.Warning)
                            FamilySafetyStatus.UNKNOWN -> Triple(WarningAmberSoft, WarningAmber, Icons.Filled.HelpOutline)
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${member.name} (${member.relation.ifBlank { "عضو خانواده" }})",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${member.phone} • گروه خونی: ${member.bloodType.ifBlank { "نامشخص" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteMember(member.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "حذف عضو")
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatusBadge(
                                        text = member.safetyStatus.titleFa,
                                        containerColor = bg,
                                        contentColor = fg,
                                        icon = icon
                                    )
                                    Text(
                                        text = PersianTimeFormatter.formatTimestamp(member.lastUpdatedMillis),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (member.medicalNotes.isNotBlank()) {
                                    Text(
                                        text = "نکته پزشکی: ${member.medicalNotes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // Safety Status quick toggles
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FamilySafetyStatus.entries.forEach { st ->
                                        FilterChip(
                                            selected = member.safetyStatus == st,
                                            onClick = { onUpdateSafetyStatus(member.id, st) },
                                            label = { Text(st.titleFa, style = MaterialTheme.typography.labelSmall) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { dialNumber(context, member.phone) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تماس")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            openSmsComposer(
                                                context,
                                                member.phone,
                                                "سلام، در شرایط بحرانی هستم. وضعیت من: ${member.safetyStatus.titleFa}."
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("پیامک وضعیت")
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddMemberDialog) {
        AddFamilyMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onSave = { name, rel, phone, blood, notes ->
                onAddMember(name, rel, phone, blood, notes)
                showAddMemberDialog = false
            }
        )
    }

    if (showEditPlanDialog) {
        EditFamilyPlanDialog(
            initialPrimary = uiState.plan.primaryMeetingPoint,
            initialSecondary = uiState.plan.secondaryMeetingPoint,
            initialOutName = uiState.plan.outOfCityContactName,
            initialOutPhone = uiState.plan.outOfCityContactPhone,
            initialNotes = uiState.plan.emergencyNotes,
            onDismiss = { showEditPlanDialog = false },
            onSave = { p1, p2, oname, ophone, notes ->
                onSavePlan(p1, p2, oname, ophone, notes)
                showEditPlanDialog = false
            }
        )
    }
}

@Composable
private fun PlanLine(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, relation: String, phone: String, bloodType: String, medicalNotes: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var relation by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var bloodType by rememberSaveable { mutableStateOf("") }
    var medicalNotes by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن عضو خانواده یا فرد نزدیک") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام کامل *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("family_member_name_input")
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text("نسبت (مثلاً همسر، پدر، همکار)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("شماره تماس ضروری *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("family_member_phone_input")
                )
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = { bloodType = it },
                    label = { Text("گروه خونی (اختیاری)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("دارو یا حساسیت مهم (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, relation, phone, bloodType, medicalNotes) },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.testTag("family_member_save_button")
            ) {
                Text("ذخیره")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun EditFamilyPlanDialog(
    initialPrimary: String,
    initialSecondary: String,
    initialOutName: String,
    initialOutPhone: String,
    initialNotes: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var primary by rememberSaveable { mutableStateOf(initialPrimary) }
    var secondary by rememberSaveable { mutableStateOf(initialSecondary) }
    var outName by rememberSaveable { mutableStateOf(initialOutName) }
    var outPhone by rememberSaveable { mutableStateOf(initialOutPhone) }
    var notes by rememberSaveable { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("برنامه ارتباطی خانواده در بحران") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = primary,
                    onValueChange = { primary = it },
                    label = { Text("محل تجمع اول (نزدیک خانه)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = secondary,
                    onValueChange = { secondary = it },
                    label = { Text("محل تجمع دوم (پارک یا میدان امن)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = outName,
                    onValueChange = { outName = it },
                    label = { Text("نام رابط خارج از شهر") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = outPhone,
                    onValueChange = { outPhone = it },
                    label = { Text("تلفن رابط خارج از شهر") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("دستورالعمل ویژه خانواده") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(primary, secondary, outName, outPhone, notes) }) {
                Text("ذخیره برنامه")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
