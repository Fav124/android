package com.example.deisaapplication.ui.screens.bed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.InfirmaryBed
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfirmaryBedScreen(
    viewModel: InfirmaryBedViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
    onAddNew: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var showFormDialog by remember { mutableStateOf(false) }
    var bedToEdit by remember { mutableStateOf<InfirmaryBed?>(null) }
    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Kasur UKS",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { viewModel.loadList() }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (canManageData) {
                FloatingActionButton(onClick = { 
                    bedToEdit = null
                    showFormDialog = true 
                }, containerColor = Primary, contentColor = AppBackground) {
                    Icon(Icons.Filled.Add, "Tambah Kasur")
                }
            }
        },
        containerColor = AppBackground,
    ) { pv ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadList() },
            modifier = Modifier.fillMaxSize().padding(pv),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                state.isLoading && state.beds.isEmpty() -> LoadingBox()
                state.error != null && state.beds.isEmpty() -> ErrorBox(state.error!!, { viewModel.loadList() })
                else -> {
                    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        Spacer(Modifier.height(16.dp))
                        
                        // Summary
                        val total = state.beds.size
                        val available = state.beds.count { it.status == "available" }
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total Kasur: $total", color = MutedText, fontSize = 14.sp)
                            Text("Tersedia: $available", color = Primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        Spacer(Modifier.height(16.dp))

                        LazyColumn(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (state.beds.isEmpty()) {
                                item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada data.", color = MutedText) } }
                            }
                            items(state.beds, key = { it.id }) { bed ->
                                BedItem(
                                    bed = bed, 
                                    canManageData = canManageData, 
                                    onEdit = { 
                                        bedToEdit = bed
                                        showFormDialog = true
                                    },
                                    onDelete = { viewModel.delete(bed.id) }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        BedFormDialog(
            bed = bedToEdit,
            onDismiss = { showFormDialog = false },
            onSave = { data ->
                viewModel.save(bedToEdit?.id, data) { success ->
                    if (success) showFormDialog = false
                }
            }
        )
    }
}


@Composable
private fun BedItem(bed: InfirmaryBed, canManageData: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DeisaCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).background(if (bed.status == "available") Primary.copy(alpha=0.15f) else AppError.copy(alpha=0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Bed, null, tint = if (bed.status == "available") Primary else AppError)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(bed.code, fontWeight = FontWeight.Bold, color = OnAppBackground, fontSize = 16.sp)
                    StatusBadge(bed.status, bed.statusLabel)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MeetingRoom, null, tint = MutedText, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(bed.roomName, fontSize = 12.sp, color = MutedText)
                }
                if (bed.status == "occupied" && bed.occupantName != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Person, null, tint = AppError, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(bed.occupantName, fontSize = 12.sp, color = AppError, fontWeight = FontWeight.Medium)
                    }
                }
            }
            if (canManageData) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, "Ubah", tint = Secondary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, "Hapus", tint = AppError, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = AppSurface,
            title = { Text("Hapus Kasur?", color = OnAppBackground) },
            text = { Text("Apakah Anda yakin ingin menghapus kasur ${bed.code}?", color = MutedText) },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete()
                    showDeleteDialog = false 
                }) { Text("Hapus", color = AppError) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = MutedText) }
            }
        )
    }
}

@Composable
fun BedFormDialog(
    bed: InfirmaryBed?,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var code by remember { mutableStateOf(bed?.code ?: "") }
    var roomName by remember { mutableStateOf(bed?.roomName ?: "UKS") }
    var status by remember { mutableStateOf(bed?.status ?: "available") }
    var occupantName by remember { mutableStateOf(bed?.occupantName ?: "") }
    var notes by remember { mutableStateOf(bed?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text(if (bed == null) "Tambah Kasur" else "Ubah Kasur", color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = code, onValueChange = { code = it },
                    label = { Text("Kode Kasur (Contoh: K-01)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = roomName, onValueChange = { roomName = it },
                    label = { Text("Nama Ruangan") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                DeisaRadioGroup(
                    label = "Status",
                    options = listOf("available" to "Tersedia", "occupied" to "Terisi"),
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )
                if (status == "occupied") {
                    OutlinedTextField(
                        value = occupantName, onValueChange = { occupantName = it },
                        label = { Text("Nama Penghuni") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )
                }
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Catatan") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(mapOf(
                    "code" to code,
                    "room_name" to roomName,
                    "status" to status,
                    "occupant_name" to if (status == "occupied") occupantName else null,
                    "notes" to notes,
                ))
            }) { Text("Simpan", color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        }
    )
}
