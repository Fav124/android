package com.example.deisaapplication.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onOpenDrawer: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message, state.error) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = "Pengaturan", onOpenDrawer = onOpenDrawer) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBackground,
    ) { pv ->
        Column(Modifier.fillMaxSize().padding(pv)) {
            if (state.isLoading) DeisaLoadingBar()

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // User Profile Section
                SettingsSection("Profil Saya", Icons.Default.Person) {
                    val user = state.user
                    DeisaCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoItem(Icons.Default.Badge, "Nama", user?.name ?: "-")
                            DeisaDivider()
                            InfoItem(Icons.Default.Email, "Email", user?.email ?: "-")
                            DeisaDivider()
                            InfoItem(Icons.Default.Phone, "No. WhatsApp", user?.noHp ?: "-")
                            DeisaDivider()
                            InfoItem(Icons.Default.Security, "Role", user?.roleLabel ?: "-")
                        }
                    }
                }

                // App Configuration (Admin Only)
                if (state.user?.isAdmin() == true) {
                    SettingsSection("Konfigurasi Aplikasi", Icons.Default.SettingsApplications) {
                        AppConfigContent(state.appSettings) { updates ->
                            viewModel.updateAppSettings(updates)
                        }
                    }
                }

                // Danger Zone / Account
                SettingsSection("Akun", Icons.Default.AccountCircle) {
                    DeisaCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { /* Change Password */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                            ) {
                                Icon(Icons.Default.Lock, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Ganti Password")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
        }
        content()
    }
}

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = MutedText, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MutedText)
            Text(value, fontSize = 14.sp, color = OnAppBackground, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AppConfigContent(settings: Map<String, String>, onSave: (Map<String, Any>) -> Unit) {
    var appName by remember(settings) { mutableStateOf(settings["app_name"] ?: "") }
    var instName by remember(settings) { mutableStateOf(settings["institution_name"] ?: "") }
    var minStock by remember(settings) { mutableStateOf(settings["default_min_stock"] ?: "5") }
    var expWarning by remember(settings) { mutableStateOf(settings["batas_hampir_kadaluarsa_hari"] ?: "90") }

    DeisaCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = appName, onValueChange = { appName = it },
                label = { Text("Nama Aplikasi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
            )
            OutlinedTextField(
                value = instName, onValueChange = { instName = it },
                label = { Text("Nama Pondok / Institusi") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minStock, onValueChange = { minStock = it },
                    label = { Text("Min. Stok") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
                )
                OutlinedTextField(
                    value = expWarning, onValueChange = { expWarning = it },
                    label = { Text("Warn. Exp (Hari)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
                )
            }
            Button(
                onClick = {
                    onSave(mapOf(
                        "app_name" to appName,
                        "institution_name" to instName,
                        "default_min_stock" to (minStock.toIntOrNull() ?: 5),
                        "batas_hampir_kadaluarsa_hari" to (expWarning.toIntOrNull() ?: 90)
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Konfigurasi", fontWeight = FontWeight.Bold)
            }
        }
    }
}
