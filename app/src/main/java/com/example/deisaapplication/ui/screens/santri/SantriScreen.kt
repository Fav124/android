package com.example.deisaapplication.ui.screens.santri

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    LaunchedEffect(search, selectedClass) {
        if (search.isNotBlank()) {
            kotlinx.coroutines.delay(2000L)
        }
        viewModel.loadList(search = search.takeIf { it.isNotBlank() }, classId = selectedClass?.id)
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Data Santri",
                onOpenDrawer = onOpenDrawer,
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
                if (state.isLoading) {
                    DeisaLoadingBar()
                }
                OutlinedTextField(
                    value = search, 
                    onValueChange = { search = it },
                    placeholder = { Text("Cari nama santri...", color = MutedText) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MutedText) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = "" }) {
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
private fun SantriItem(
    santri: Santri, 
    canManageData: Boolean, 
    onClick: (Int) -> Unit, 
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val genderColor = if (santri.gender == "L") LightBlue else Color(0xFFE91E63)
    val genderBg = genderColor.copy(alpha = 0.08f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(santri.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppSurfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter avatar with gorgeous frame
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(genderBg, CircleShape)
                    .border(2.dp, genderColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = santri.name.take(1).uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    color = genderColor,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name
                Text(
                    text = santri.name,
                    fontWeight = FontWeight.Bold,
                    color = OnAppBackground,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // NIS Tag if exists
                if (!santri.nis.isNullOrBlank()) {
                    Text(
                        text = "NIS: ${santri.nis}",
                        color = MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Badges Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Class Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Primary.copy(alpha = 0.08f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.School,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = santri.schoolClass ?: "-",
                                fontSize = 11.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = AppSurface,
            title = { Text("Hapus Data Santri", color = OnAppBackground, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus data '${santri.name}'?", color = MutedText) },
            confirmButton = {
                TextButton(
                    onClick = { 
                        onDelete()
                        showDeleteDialog = false 
                    }
                ) { 
                    Text("Hapus", color = AppError, fontWeight = FontWeight.Bold) 
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
