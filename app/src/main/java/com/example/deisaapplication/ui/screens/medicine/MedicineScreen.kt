package com.example.deisaapplication.ui.screens.medicine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.Medicine
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
    onAddNew: () -> Unit,
    onViewDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var search by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Inventori Obat",
                onOpenDrawer = onOpenDrawer,
            )
        },
        floatingActionButton = {
            if (canManageData) {
                FloatingActionButton(onClick = onAddNew, containerColor = Primary, contentColor = AppBackground) {
                    Icon(Icons.Filled.Add, "Tambah Obat")
                }
            }
        },
        containerColor = AppBackground,
    ) { pv ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { 
                viewModel.loadList(
                    search   = search.takeIf { it.isNotBlank() },
                    lowStock = if (activeFilter == "low_stock") true else null,
                    expired  = if (activeFilter == "expired") true else null,
                )
            },
            modifier = Modifier.fillMaxSize().padding(pv),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    DeisaLoadingBar()
                }
                // Summary strip
                val meds = state.medicines
                val kritis  = meds.count { it.status == "stok_kritis" }
                val expired = meds.count { it.status == "kadaluarsa" }
                val segera  = meds.count { it.status == "segera_kadaluarsa" }
                if (kritis > 0 || expired > 0 || segera > 0) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (expired > 0) SummaryChip("$expired Kadaluarsa", AppError)
                        if (kritis > 0) SummaryChip("$kritis Stok Kritis", AppWarning)
                        if (segera > 0) SummaryChip("$segera Segera Exp", AppWarning)
                    }
                }

                // Search
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Cari nama obat...", color = MutedText) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MutedText) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = ""; viewModel.loadList() }) {
                                Icon(Icons.Filled.Clear, null, tint = MutedText)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Primary,
                        unfocusedBorderColor = AppSurfaceVariant,
                        focusedTextColor     = OnAppBackground,
                        unfocusedTextColor   = OnAppBackground,
                    ),
                )

                // Filter chips
                val filters = listOf(null to "Semua", "low_stock" to "Stok Kritis", "expired" to "Kadaluarsa", "expiring_soon" to "Segera Exp")
                LazyRow(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = activeFilter == key,
                            onClick = {
                                activeFilter = key
                                viewModel.loadList(
                                    search   = search.takeIf { it.isNotBlank() },
                                    lowStock = if (key == "low_stock") true else null,
                                    expired  = if (key == "expired") true else null,
                                )
                            },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor     = AppBackground,
                                containerColor         = AppSurfaceVariant,
                                labelColor             = OnAppBackground,
                            ),
                        )
                    }
                }

                when {
                    state.isLoading -> LoadingBox()
                    state.error != null -> ErrorBox(state.error!!, { viewModel.loadList() })
                    else -> LazyColumn(
                        Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item { Spacer(Modifier.height(4.dp)) }
                        if (state.medicines.isEmpty()) {
                            item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada data.", color = MutedText) } }
                        }
                        items(state.medicines, key = { it.id }) { med ->
                            MedicineItem(
                                med = med,
                                canManageData = canManageData,
                                onClick = { onViewDetail(med.id) },
                                onDelete = { viewModel.delete(med.id) }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicineItem(med: Medicine, canManageData: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DeisaCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(med.name, fontWeight = FontWeight.SemiBold, color = OnAppBackground, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Inventory, null, tint = MutedText, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Stok: ${med.stock} ${med.unit}", fontSize = 12.sp, color = MutedText)
                    }
                    if (med.expiryDate != null) {
                        Text("Exp: ${med.expiryDate}", fontSize = 12.sp, color = MutedText)
                    }
                }
                Spacer(Modifier.height(8.dp))
                StatusBadge(med.status, when (med.status) {
                    "aman"              -> "Aman"
                    "stok_kritis"       -> "Stok Kritis"
                    "kadaluarsa"        -> "Kadaluarsa"
                    "segera_kadaluarsa" -> "Segera Exp"
                    else                -> med.status
                })
            }
            if (canManageData) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.DeleteOutline, "Hapus", tint = AppError)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = AppSurface,
            textContentColor = OnAppBackground,
            title = { Text("Hapus Obat", color = OnAppBackground) },
            text = { Text("Hapus '${med.name}' dari inventori?", color = MutedText) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Hapus", color = AppError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = MutedText)
                }
            }
        )
    }
}

@Composable
private fun SummaryChip(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
        Text(
            label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}
