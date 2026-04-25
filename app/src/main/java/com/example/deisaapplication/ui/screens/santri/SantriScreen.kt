package com.example.deisaapplication.ui.screens.santri

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.Santri
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantriScreen(
    viewModel: SantriViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
    onAddNew: () -> Unit,
    onViewDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val lookups by viewModel.lookups.collectAsState()
    var search by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<com.example.deisaapplication.data.model.LookupItem?>(null) }

    LaunchedEffect(state.toast) {
        if (state.toast != null) {
            kotlinx.coroutines.delay(3000L)
            viewModel.clearToast()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLookups()
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Data Santri",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { viewModel.loadList(search.takeIf { it.isNotBlank() }, classId = selectedClass?.id) }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (canManageData) {
                FloatingActionButton(onClick = onAddNew, containerColor = Primary, contentColor = AppBackground) {
                    Icon(Icons.Filled.Add, "Tambah Santri")
                }
            }
        },
        containerColor = AppBackground,
        snackbarHost = {
            if (state.toast != null) {
                Snackbar(modifier = Modifier.padding(16.dp), containerColor = AppSurface, contentColor = OnAppBackground) {
                    Text(state.toast!!)
                }
            }
        },
    ) { pv ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadList(search.takeIf { it.isNotBlank() }, classId = selectedClass?.id) },
            modifier = Modifier.fillMaxSize().padding(pv),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = search, onValueChange = { 
                        search = it
                        viewModel.loadList(it.takeIf { it.isNotBlank() }, classId = selectedClass?.id)
                    },
                    placeholder = { Text("Cari nama santri...", color = MutedText) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MutedText) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = ""; viewModel.loadList(classId = selectedClass?.id) }) {
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

                // Class Filter
                DeisaSearchableDropdown(
                    label = "Filter Kelas",
                    items = lookups.classes,
                    selectedItem = selectedClass,
                    onItemSelected = { 
                        selectedClass = it
                        viewModel.loadList(search.takeIf { it.isNotBlank() }, classId = it.id)
                    },
                    itemLabel = { it.name },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder = "Cari kelas..."
                )
                
                if (selectedClass != null) {
                    TextButton(
                        onClick = { 
                            selectedClass = null
                            viewModel.loadList(search.takeIf { it.isNotBlank() })
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Close, null, modifier = Modifier.size(14.dp), tint = AppError)
                            Spacer(Modifier.width(4.dp))
                            Text("Reset Filter Kelas", fontSize = 12.sp, color = AppError)
                        }
                    }
                }

                when {
                    state.isLoading && state.santris.isEmpty() -> LoadingBox()
                    state.error != null && state.santris.isEmpty() -> ErrorBox(state.error!!, { viewModel.loadList() })
                    else -> LazyColumn(
                        Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item { Spacer(Modifier.height(4.dp)) }
                        if (state.santris.isEmpty()) {
                            item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Tidak ada data.", color = MutedText) } }
                        }
                        items(state.santris, key = { it.id }) { santri ->
                            SantriItem(santri, canManageData, onViewDetail, onDelete = { viewModel.delete(santri.id) })
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SantriItem(santri: Santri, canManageData: Boolean, onClick: (Int) -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DeisaCard(modifier = Modifier.clickable { onClick(santri.id) }) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).background(if(santri.gender == "L") Color(0xFF0090E7).copy(alpha=0.15f) else Color(0xFFFC424A).copy(alpha=0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    (santri.name.take(1)).uppercase(),
                    fontWeight = FontWeight.Bold, 
                    color = if(santri.gender == "L") Color(0xFF0090E7) else Color(0xFFFC424A), 
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(santri.name, fontWeight = FontWeight.SemiBold, color = OnAppBackground, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.School, null, tint = MutedText, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(santri.schoolClass ?: "-", fontSize = 12.sp, color = MutedText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Home, null, tint = MutedText, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(santri.dormitory ?: "-", fontSize = 12.sp, color = MutedText)
                    }
                }
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
            title = { Text("Hapus Data Santri", color = OnAppBackground) },
            text = { Text("Hapus data '${santri.name}'?", color = MutedText) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) { Text("Hapus", color = AppError) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = MutedText) }
            }
        )
    }
}
