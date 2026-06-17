package com.example.deisaapplication.ui.screens.master

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.deisaapplication.ui.theme.AppSurfaceVariant
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
    val chunkedClasses = remember(state.classes) { state.classes.chunked(2) }
    var showClassDialog by remember { mutableStateOf(false) }
    var showMajorDialog by remember { mutableStateOf(false) }
    var editingClass by remember { mutableStateOf<SchoolClassItem?>(null) }
    var editingMajor by remember { mutableStateOf<MajorItem?>(null) }
    var detailClass by remember { mutableStateOf<SchoolClassItem?>(null) }

    LaunchedEffect(section) {
        viewModel.load(section)
        if (section == MasterSection.CLASS) {
            viewModel.loadReferenceData()
        }
    }

    when {
        showClassDialog -> {
            ClassFormScreen(
                item = editingClass,
                majors = state.majors,
                onDismiss = { showClassDialog = false },
                onSave = { body ->
                    viewModel.saveClass(editingClass?.id, body) { success ->
                        if (success) showClassDialog = false
                    }
                }
            )
        }
        showMajorDialog -> {
            MajorFormScreen(
                item = editingMajor,
                onDismiss = { showMajorDialog = false },
                onSave = { body ->
                    viewModel.saveMajor(editingMajor?.id, body) { success ->
                        if (success) showMajorDialog = false
                    }
                }
            )
        }
        detailClass != null -> {
            ClassDetailScreen(
                item = detailClass!!,
                canManageData = canManageData,
                onDismiss = { detailClass = null },
                onEdit = {
                    val item = detailClass!!
                    detailClass = null
                    editingClass = item
                    showClassDialog = true
                },
                onDelete = {
                    viewModel.deleteClass(detailClass!!.id)
                    detailClass = null
                }
            )
        }
        else -> {
            Scaffold(
                topBar = {
                    DeisaTopBar(
                        title = section.title,
                        onOpenDrawer = onOpenDrawer,
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
                        state.isLoading && section.items(state).isEmpty() -> {
                            LoadingBox()
                        }
                        state.error != null && section.items(state).isEmpty() -> {
                            ErrorBox(
                                message = state.error!!,
                                onRetry = { viewModel.load(section) },
                            )
                        }
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
                                    MasterSection.CLASS -> {
                                        items(chunkedClasses.size) { index ->
                                            val rowItems = chunkedClasses[index]
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                rowItems.forEach { item ->
                                                    ClassGridCard(
                                                        item = item,
                                                        onClick = { detailClass = item },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                                if (rowItems.size < 2) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
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
                                }
                                item { Spacer(Modifier.height(96.dp)) }
                            }
                        }
                    }
                }
            }
        }
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassFormScreen(
    item: SchoolClassItem?,
    majors: List<MajorItem>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var description by remember(item) { mutableStateOf(item?.description ?: "") }
    var selectedMajors by remember(item) { mutableStateOf(item?.majorIds?.toSet() ?: emptySet()) }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = if (item == null) "Tambah Kelas" else "Ubah Kelas",
                onBack = onDismiss
            )
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Kelas") },
                        placeholder = { Text("Contoh: 12, 11, atau 10") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        placeholder = { Text("Deskripsi singkat mengenai kelas ini") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Jurusan Terkait", color = OnAppBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(
                        mapOf(
                            "nama_kelas" to name,
                            "deskripsi" to description.ifBlank { null },
                            "major_ids" to selectedMajors.toList(),
                        )
                    )
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Simpan Perubahan", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun MajorFormScreen(
    item: MajorItem?,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit,
) {
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var description by remember(item) { mutableStateOf(item?.description ?: "") }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = if (item == null) "Tambah Jurusan" else "Ubah Jurusan",
                onBack = onDismiss
            )
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Jurusan") },
                        placeholder = { Text("Contoh: Rekayasa Perangkat Lunak") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        placeholder = { Text("Deskripsi singkat mengenai jurusan ini") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(mapOf("nama_jurusan" to name, "deskripsi" to description.ifBlank { null }))
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("Simpan Perubahan", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

private fun MasterSection.items(state: MasterDataState): List<Any> = when (this) {
    MasterSection.CLASS -> state.classes
    MasterSection.MAJOR -> state.majors
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassGridCard(
    item: SchoolClassItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Primary.copy(alpha = 0.06f))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Class,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.name,
                        color = OnAppBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = item.description ?: "Kelas Santri",
                        color = MutedText,
                        fontSize = 12.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (item.majorNames.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item.majorNames.take(2).forEach { major ->
                                Text(
                                    text = major,
                                    color = Primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (item.majorNames.size > 2) {
                                Text(
                                    text = "+${item.majorNames.size - 2}",
                                    color = MutedText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(AppSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Umum",
                            color = MutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(AppSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassDetailScreen(
    item: SchoolClassItem,
    canManageData: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Detail Kelas",
                onBack = onDismiss
            )
        },
        containerColor = AppBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Primary.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Class,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = item.name,
                            color = OnAppBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Tingkatan Kelas",
                            color = MutedText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Deskripsi Kelas",
                    color = OnAppBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface)
                ) {
                    Text(
                        text = item.description ?: "Tidak ada deskripsi untuk kelas ini.",
                        color = MutedText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Jurusan Terkait",
                    color = OnAppBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (item.majorNames.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item.majorNames.forEach { major ->
                                    Text(
                                        text = major,
                                        color = Primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Umum (Tidak terikat jurusan khusus)",
                                color = MutedText,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Santri Terdaftar",
                        color = OnAppBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${item.santris.size} Santri",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (item.santris.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item.santris.forEach { santri ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AppSurface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Primary.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = santri.name.take(1).uppercase(),
                                            color = Primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = santri.name,
                                            color = OnAppBackground,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "NIS: ${santri.nis ?: '-'}",
                                            color = MutedText,
                                            fontSize = 12.sp
                                        )
                                    }
                                    if (!santri.major.isNullOrBlank()) {
                                        Text(
                                            text = santri.major,
                                            color = Secondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Secondary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppSurface)
                    ) {
                        Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Belum ada santri terdaftar di kelas ini.",
                                color = MutedText,
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            if (canManageData) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = Secondary.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ubah Kelas", color = Secondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    ConfirmDeleteClassButton(onDelete = onDelete, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteClassButton(onDelete: () -> Unit, modifier: Modifier = Modifier) {
    var showConfirm by remember { mutableStateOf(false) }
    
    Button(
        onClick = { showConfirm = true },
        colors = ButtonDefaults.buttonColors(containerColor = AppError.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = AppError,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Hapus Kelas", color = AppError, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor = AppSurface,
            title = { Text("Hapus Kelas?", color = OnAppBackground, fontWeight = FontWeight.Bold) },
            text = { Text("Data kelas ini akan dihapus secara permanen.", color = MutedText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showConfirm = false
                    }
                ) { Text("Hapus", color = AppError, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Batal", color = MutedText) }
            },
        )
    }
}
