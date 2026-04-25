package com.example.deisaapplication.ui.screens.master

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.DormitoryItem
import com.example.deisaapplication.data.model.MajorItem
import com.example.deisaapplication.data.model.SchoolClassItem
import com.example.deisaapplication.ui.components.DeisaCard
import com.example.deisaapplication.ui.components.DeisaRadioGroup
import com.example.deisaapplication.ui.components.DeisaTopBar
import com.example.deisaapplication.ui.components.ErrorBox
import com.example.deisaapplication.ui.components.LoadingBox
import com.example.deisaapplication.ui.components.ModernStatCard
import com.example.deisaapplication.ui.components.StatusBadge
import com.example.deisaapplication.ui.theme.AppBackground
import com.example.deisaapplication.ui.theme.AppError
import com.example.deisaapplication.ui.theme.AppSurface
import com.example.deisaapplication.ui.theme.MutedText
import com.example.deisaapplication.ui.theme.OnAppBackground
import com.example.deisaapplication.ui.theme.Primary
import com.example.deisaapplication.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MasterDataScreen(
    section: MasterSection,
    viewModel: MasterDataViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var showClassDialog by remember { mutableStateOf(false) }
    var showMajorDialog by remember { mutableStateOf(false) }
    var showDormitoryDialog by remember { mutableStateOf(false) }
    var editingClass by remember { mutableStateOf<SchoolClassItem?>(null) }
    var editingMajor by remember { mutableStateOf<MajorItem?>(null) }
    var editingDormitory by remember { mutableStateOf<DormitoryItem?>(null) }

    LaunchedEffect(section) {
        viewModel.load(section)
        if (section == MasterSection.CLASS) {
            viewModel.loadReferenceData()
        }
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = section.title,
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { viewModel.load(section) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Muat ulang", tint = Primary)
                    }
                },
            )
        },
        floatingActionButton = {
            if (canManageData) {
                FloatingActionButton(
                    onClick = {
                        when (section) {
                            MasterSection.CLASS -> {
                                editingClass = null
                                showClassDialog = true
                            }
                            MasterSection.MAJOR -> {
                                editingMajor = null
                                showMajorDialog = true
                            }
                            MasterSection.DORMITORY -> {
                                editingDormitory = null
                                showDormitoryDialog = true
                            }
                        }
                    },
                    containerColor = Primary,
                    contentColor = AppBackground,
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Tambah")
                }
            }
        },
        containerColor = AppBackground,
        snackbarHost = {
            state.toast?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = AppSurface,
                    contentColor = OnAppBackground,
                ) { Text(it) }
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.load(section) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                state.isLoading && section.items(state).isEmpty() -> LoadingBox()
                state.error != null && section.items(state).isEmpty() -> ErrorBox(
                    message = state.error!!,
                    onRetry = { viewModel.load(section) },
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item { MasterSummary(section = section, state = state) }
                        if (section.items(state).isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 36.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("Belum ada data.", color = MutedText)
                                }
                            }
                        }
                        when (section) {
                            MasterSection.CLASS -> items(state.classes, key = { it.id }) { item ->
                                ClassCard(
                                    item = item,
                                    canManageData = canManageData,
                                    onEdit = {
                                        editingClass = item
                                        showClassDialog = true
                                    },
                                    onDelete = { viewModel.deleteClass(item.id) },
                                )
                            }
                            MasterSection.MAJOR -> items(state.majors, key = { it.id }) { item ->
                                MajorCard(
                                    item = item,
                                    canManageData = canManageData,
                                    onEdit = {
                                        editingMajor = item
                                        showMajorDialog = true
                                    },
                                    onDelete = { viewModel.deleteMajor(item.id) },
                                )
                            }
                            MasterSection.DORMITORY -> items(state.dormitories, key = { it.id }) { item ->
                                DormitoryCard(
                                    item = item,
                                    canManageData = canManageData,
                                    onEdit = {
                                        editingDormitory = item
                                        showDormitoryDialog = true
                                    },
                                    onDelete = { viewModel.deleteDormitory(item.id) },
                                )
                            }
                        }
                        item { Spacer(Modifier.height(96.dp)) }
                    }
                }
            }
        }
    }

    if (showClassDialog) {
        ClassFormDialog(
            item = editingClass,
            majors = state.majors,
            onDismiss = { showClassDialog = false },
            onSave = { body ->
                viewModel.saveClass(editingClass?.id, body) { success ->
                    if (success) showClassDialog = false
                }
            },
        )
    }

    if (showMajorDialog) {
        MajorFormDialog(
            item = editingMajor,
            onDismiss = { showMajorDialog = false },
            onSave = { body ->
                viewModel.saveMajor(editingMajor?.id, body) { success ->
                    if (success) showMajorDialog = false
                }
            },
        )
    }

    if (showDormitoryDialog) {
        DormitoryFormDialog(
            item = editingDormitory,
            onDismiss = { showDormitoryDialog = false },
            onSave = { body ->
                viewModel.saveDormitory(editingDormitory?.id, body) { success ->
                    if (success) showDormitoryDialog = false
                }
            },
        )
    }

    LaunchedEffect(state.toast) {
        if (state.toast != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearToast()
        }
    }
}

@Composable
private fun MasterSummary(section: MasterSection, state: MasterDataState) {
    when (section) {
        MasterSection.CLASS -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModernStatCard(
                title = "Total Kelas",
                value = state.classes.size.toString(),
                icon = Icons.Default.Class,
                color = Primary,
                modifier = Modifier.weight(1f),
            )
            ModernStatCard(
                title = "Jurusan Terkait",
                value = state.classes.sumOf { it.majorNames.size }.toString(),
                icon = Icons.Default.Workspaces,
                color = Secondary,
                modifier = Modifier.weight(1f),
            )
        }
        MasterSection.MAJOR -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModernStatCard(
                title = "Total Jurusan",
                value = state.majors.size.toString(),
                icon = Icons.Default.School,
                color = Primary,
                modifier = Modifier.weight(1f),
            )
            ModernStatCard(
                title = "Dipakai Kelas",
                value = state.classes.count { it.majorIds.isNotEmpty() }.toString(),
                icon = Icons.Default.Workspaces,
                color = Secondary,
                modifier = Modifier.weight(1f),
            )
        }
        MasterSection.DORMITORY -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ModernStatCard(
                title = "Total Asrama",
                value = state.dormitories.size.toString(),
                icon = Icons.Default.Apartment,
                color = Primary,
                modifier = Modifier.weight(1f),
            )
            ModernStatCard(
                title = "Total Penghuni",
                value = state.dormitories.sumOf { it.santriCount }.toString(),
                icon = Icons.Default.Person,
                color = Secondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ClassCard(
    item: SchoolClassItem,
    canManageData: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    DeisaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Class, contentDescription = null, tint = Primary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(item.name, color = OnAppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (!item.description.isNullOrBlank()) {
                    Text(item.description, color = MutedText, fontSize = 12.sp)
                }
                if (item.majorNames.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.majorNames.forEach { major ->
                            AssistChip(
                                onClick = {},
                                label = { Text(major) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = AppBackground,
                                    labelColor = OnAppBackground,
                                ),
                            )
                        }
                    }
                }
            }
            if (canManageData) {
                ActionButtons(onEdit = onEdit, onDelete = onDelete)
            }
        }
    }
}

@Composable
private fun MajorCard(
    item: MajorItem,
    canManageData: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    DeisaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Secondary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Secondary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(item.name, color = OnAppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.description ?: "Belum ada deskripsi.", color = MutedText, fontSize = 12.sp)
            }
            if (canManageData) {
                ActionButtons(onEdit = onEdit, onDelete = onDelete)
            }
        }
    }
}

@Composable
private fun DormitoryCard(
    item: DormitoryItem,
    canManageData: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    DeisaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Apartment, contentDescription = null, tint = Primary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.name, color = OnAppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    StatusBadge(item.gender, if (item.gender == "L") "Putra" else "Putri")
                }
                Text(item.building ?: "Bangunan belum diisi", color = MutedText, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetaLine(icon = Icons.Default.MeetingRoom, text = "${item.santriCount} santri")
                    MetaLine(icon = Icons.Default.Person, text = item.supervisorName ?: "-")
                }
            }
            if (canManageData) {
                ActionButtons(onEdit = onEdit, onDelete = onDelete)
            }
        }
    }
}

@Composable
private fun MetaLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MutedText, modifier = Modifier.size(14.dp))
        Text(text, modifier = Modifier.padding(start = 4.dp), color = MutedText, fontSize = 12.sp)
    }
}

@Composable
private fun ActionButtons(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Ubah", tint = Secondary)
        }
        ConfirmDeleteButton(onDelete = onDelete)
    }
}

@Composable
private fun ConfirmDeleteButton(onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = AppError)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AppSurface,
            title = { Text("Hapus data?", color = OnAppBackground) },
            text = { Text("Data yang dihapus tidak bisa dikembalikan.", color = MutedText) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) { Text("Hapus", color = AppError) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal", color = MutedText) }
            },
        )
    }
}

@Composable
private fun ClassFormDialog(
    item: SchoolClassItem?,
    majors: List<MajorItem>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var description by remember(item) { mutableStateOf(item?.description ?: "") }
    var selectedMajors by remember(item) { mutableStateOf(item?.majorIds?.toSet() ?: emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text(if (item == null) "Tambah Kelas" else "Ubah Kelas", color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama kelas") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Jurusan terkait", color = OnAppBackground, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    majors.forEach { major ->
                        val selected = major.id in selectedMajors
                        AssistChip(
                            onClick = {
                                selectedMajors = if (selected) selectedMajors - major.id else selectedMajors + major.id
                            },
                            label = { Text(major.name) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected) Primary.copy(alpha = 0.16f) else AppBackground,
                                labelColor = OnAppBackground,
                                leadingIconContentColor = Primary,
                            ),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    mapOf(
                        "name" to name,
                        "description" to description.ifBlank { null },
                        "major_ids" to selectedMajors.toList(),
                    ),
                )
            }) { Text("Simpan", color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        },
    )
}

@Composable
private fun MajorFormDialog(
    item: MajorItem?,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var description by remember(item) { mutableStateOf(item?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text(if (item == null) "Tambah Jurusan" else "Ubah Jurusan", color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama jurusan") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(mapOf("name" to name, "description" to description.ifBlank { null }))
            }) { Text("Simpan", color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        },
    )
}

@Composable
private fun DormitoryFormDialog(
    item: DormitoryItem?,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var building by remember(item) { mutableStateOf(item?.building ?: "") }
    var gender by remember(item) { mutableStateOf(item?.gender ?: "L") }
    var supervisorName by remember(item) { mutableStateOf(item?.supervisorName ?: "") }
    var description by remember(item) { mutableStateOf(item?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text(if (item == null) "Tambah Asrama" else "Ubah Asrama", color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama asrama") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = building, onValueChange = { building = it }, label = { Text("Bangunan") }, modifier = Modifier.fillMaxWidth())
                DeisaRadioGroup(
                    label = "Gender",
                    options = listOf("L" to "Putra", "P" to "Putri"),
                    selectedOption = gender,
                    onOptionSelected = { gender = it },
                )
                OutlinedTextField(value = supervisorName, onValueChange = { supervisorName = it }, label = { Text("Nama pembina") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    mapOf(
                        "name" to name,
                        "building" to building.ifBlank { null },
                        "gender" to gender,
                        "supervisor_name" to supervisorName.ifBlank { null },
                        "description" to description.ifBlank { null },
                    ),
                )
            }) { Text("Simpan", color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        },
    )
}

private fun MasterSection.items(state: MasterDataState): List<Any> = when (this) {
    MasterSection.CLASS -> state.classes
    MasterSection.MAJOR -> state.majors
    MasterSection.DORMITORY -> state.dormitories
}
