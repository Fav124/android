package com.example.deisaapplication.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.deisaapplication.data.model.AdminUser
import com.example.deisaapplication.ui.components.DeisaCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagementScreen(
    title: String,
    defaultStatus: String?,
    viewModel: AdminManagementViewModel,
    onOpenDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var search by remember { mutableStateOf("") }
    var statusFilter by remember(defaultStatus) { mutableStateOf(defaultStatus) }
    var roleFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(defaultStatus) {
        viewModel.load(status = defaultStatus)
    }

    LaunchedEffect(search, statusFilter, roleFilter) {
        if (search.isNotBlank()) {
            kotlinx.coroutines.delay(2000L)
        }
        viewModel.load(status = statusFilter, role = roleFilter, search = search.takeIf { it.isNotBlank() })
    }

    LaunchedEffect(state.toast) {
        if (state.toast != null) {
            kotlinx.coroutines.delay(3500)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = title,
                onOpenDrawer = onOpenDrawer,
            )
        },
        containerColor = AppBackground,
        snackbarHost = {
            state.toast?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = AppSurface,
                    contentColor = OnAppBackground,
                ) {
                    val suffix = state.lastGeneratedPassword?.let { pwd -> " Password baru: $pwd" }.orEmpty()
                    Text(it + suffix)
                }
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.load(status = statusFilter, role = roleFilter, search = search.takeIf { it.isNotBlank() }) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                state.isLoading && state.users.isEmpty() -> LoadingBox()
                state.error != null && state.users.isEmpty() -> ErrorBox(state.error!!, {
                    viewModel.load(status = statusFilter, role = roleFilter, search = search.takeIf { it.isNotBlank() })
                })
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ModernStatCard("User Aktif", state.stats.approved.toString(), Icons.Default.VerifiedUser, Primary, Modifier.weight(1f))
                                ModernStatCard("Pending", state.stats.pending.toString(), Icons.Default.Approval, Secondary, Modifier.weight(1f))
                            }
                        }
                        item {
                            AdminFilters(
                                search = search,
                                onSearchChange = { search = it },
                                statusFilter = statusFilter,
                                onStatusChange = { statusFilter = it },
                                roleFilter = roleFilter,
                                onRoleChange = { roleFilter = it },
                                onApply = {
                                    viewModel.load(status = statusFilter, role = roleFilter, search = search.takeIf { it.isNotBlank() })
                                },
                            )
                        }
                        if (state.users.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("Tidak ada user yang cocok.", color = MutedText)
                                }
                            }
                        }
                        items(state.users, key = { it.id }) { user ->
                            AdminUserCard(
                                user = user,
                                defaultStatus = defaultStatus,
                                onApprove = {
                                    viewModel.approve(
                                        id = user.id,
                                        status = statusFilter,
                                        role = roleFilter,
                                        search = search.takeIf { it.isNotBlank() },
                                    )
                                },
                                onReject = { reason ->
                                    viewModel.reject(
                                        id = user.id,
                                        reason = reason,
                                        status = statusFilter,
                                        role = roleFilter,
                                        search = search.takeIf { it.isNotBlank() },
                                    )
                                },
                                onChangeRole = { role ->
                                    viewModel.changeRole(
                                        id = user.id,
                                        role = role,
                                        status = statusFilter,
                                        currentRoleFilter = roleFilter,
                                        search = search.takeIf { it.isNotBlank() },
                                    )
                                },
                                onQuickReset = { viewModel.quickReset(user.id) },
                                onDelete = {
                                    viewModel.delete(
                                        id = user.id,
                                        status = statusFilter,
                                        role = roleFilter,
                                        search = search.takeIf { it.isNotBlank() },
                                    )
                                },
                            )
                        }
                        item { Spacer(Modifier.height(96.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFilters(
    search: String,
    onSearchChange: (String) -> Unit,
    statusFilter: String?,
    onStatusChange: (String?) -> Unit,
    roleFilter: String?,
    onRoleChange: (String?) -> Unit,
    onApply: () -> Unit,
) {
    DeisaCard {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cari user") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SimpleDropdown(
                modifier = Modifier.weight(1f),
                label = "Status",
                selectedValue = statusFilter ?: "Semua",
                options = listOf("Semua" to null, "Pending" to "pending", "Disetujui" to "approved", "Ditolak" to "rejected"),
                onSelected = onStatusChange,
            )
            SimpleDropdown(
                modifier = Modifier.weight(1f),
                label = "Role",
                selectedValue = when (roleFilter) {
                    "admin" -> "Admin"
                    "petugas_kesehatan" -> "Petugas"
                    else -> "Semua"
                },
                options = listOf("Semua" to null, "Admin" to "admin", "Petugas" to "petugas_kesehatan"),
                onSelected = onRoleChange,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selectedValue: String,
    options: List<Pair<String, String?>>,
    onSelected: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (title, value) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: AdminUser,
    defaultStatus: String?,
    onApprove: () -> Unit,
    onReject: (String?) -> Unit,
    onChangeRole: (String) -> Unit,
    onQuickReset: () -> Unit,
    onDelete: () -> Unit,
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    DeisaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Primary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(user.name, color = OnAppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.email, color = MutedText, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(user.status, user.statusLabel)
                    StatusBadge(user.role, user.roleLabel)
                }
                if (!user.rejectionReason.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Alasan: ${user.rejectionReason}", color = AppError, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (user.status == "pending" || defaultStatus == "pending") {
                TextButton(onClick = onApprove) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Primary)
                    Text("Setujui", color = Primary, modifier = Modifier.padding(start = 6.dp))
                }
                TextButton(onClick = { showRejectDialog = true }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = AppError)
                    Text("Tolak", color = AppError, modifier = Modifier.padding(start = 6.dp))
                }
            }
            TextButton(onClick = { showRoleDialog = true }) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Secondary)
                Text("Role", color = Secondary, modifier = Modifier.padding(start = 6.dp))
            }
            TextButton(onClick = onQuickReset) {
                Icon(Icons.Default.Key, contentDescription = null, tint = Primary)
                Text("Reset", color = Primary, modifier = Modifier.padding(start = 6.dp))
            }
            TextButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = AppError)
                Text("Hapus", color = AppError, modifier = Modifier.padding(start = 6.dp))
            }
        }
    }

    if (showRejectDialog) {
        RejectDialog(
            onDismiss = { showRejectDialog = false },
            onSubmit = {
                onReject(it)
                showRejectDialog = false
            },
        )
    }

    if (showRoleDialog) {
        ChangeRoleDialog(
            currentRole = user.role,
            onDismiss = { showRoleDialog = false },
            onSubmit = {
                onChangeRole(it)
                showRoleDialog = false
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = AppSurface,
            title = { Text("Hapus user?", color = OnAppBackground) },
            text = { Text("User ${user.name} akan dihapus permanen.", color = MutedText) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Hapus", color = AppError) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = MutedText) }
            },
        )
    }
}

@Composable
private fun RejectDialog(onDismiss: () -> Unit, onSubmit: (String?) -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text("Tolak akun", color = OnAppBackground) },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Alasan penolakan") },
            )
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(reason.ifBlank { null }) }) { Text("Tolak", color = AppError) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        },
    )
}

@Composable
private fun ChangeRoleDialog(currentRole: String, onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var selectedRole by remember { mutableStateOf(currentRole) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text("Ubah role", color = OnAppBackground) },
        text = {
            Column {
                listOf(
                    "admin" to "Admin",
                    "petugas_kesehatan" to "Petugas Kesehatan",
                ).forEach { (role, label) ->
                    TextButton(onClick = { selectedRole = role }) {
                        Icon(
                            if (selectedRole == role) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selectedRole == role) Primary else MutedText,
                        )
                        Text(label, color = OnAppBackground, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(selectedRole) }) { Text("Simpan", color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) }
        },
    )
}
