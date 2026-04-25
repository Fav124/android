package com.example.deisaapplication.ui.screens.referral

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.ReferralRequest
import com.example.deisaapplication.data.model.SantriRef
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*
import java.time.LocalDate

@Composable
fun HospitalReferralFormScreen(
    id: Int?,
    viewModel: HospitalReferralViewModel,
    onBack: () -> Unit,
) {
    val referral by viewModel.selectedReferral.collectAsState()
    val lookups by viewModel.lookups.collectAsState()
    val state by viewModel.state.collectAsState()

    // Form states
    var selectedSantri by remember { mutableStateOf<SantriRef?>(null) }
    var hospitalName by remember { mutableStateOf("") }
    var referralDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var complaint by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var transport by remember { mutableStateOf("Ambulans Pondok") }
    var companionName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("referred") }
    var notes by remember { mutableStateOf("") }
    var notifyGuardian by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadLookups()
        if (id != null) {
            viewModel.loadDetail(id)
        }
    }

    LaunchedEffect(referral) {
        referral?.let { r ->
            selectedSantri = r.santri
            hospitalName = r.hospitalName
            referralDate = r.referralDate ?: LocalDate.now().toString()
            complaint = r.complaint
            diagnosis = r.diagnosis ?: ""
            transport = r.transport ?: "Ambulans Pondok"
            companionName = r.companionName ?: ""
            status = r.status
            notes = r.notes ?: ""
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = if (id == null) "Tambah Rujukan" else "Ubah Rujukan", onBack = onBack) },
        containerColor = AppBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedSantri == null || hospitalName.isBlank()) return@FloatingActionButton
                    
                    val req = ReferralRequest(
                        santriId = selectedSantri!!.id,
                        hospitalName = hospitalName,
                        referralDate = referralDate,
                        complaint = complaint,
                        diagnosis = diagnosis.takeIf { it.isNotBlank() },
                        transport = transport.takeIf { it.isNotBlank() },
                        companionName = companionName.takeIf { it.isNotBlank() },
                        status = status,
                        notes = notes.takeIf { it.isNotBlank() },
                        notifyGuardian = notifyGuardian
                    )
                    viewModel.save(id, req) { success -> if (success) onBack() }
                },
                containerColor = Primary,
                contentColor = AppBackground
            ) {
                Icon(Icons.Filled.Save, null)
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeisaSearchableDropdown(
                label = "Santri",
                items = lookups.santris,
                selectedItem = selectedSantri,
                onItemSelected = { selectedSantri = it },
                itemLabel = { "${it.name} (${it.nis ?: "-"})" }
            )

            OutlinedTextField(
                value = hospitalName, onValueChange = { hospitalName = it },
                label = { Text("Nama Rumah Sakit / Klinik") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = referralDate, onValueChange = { referralDate = it },
                label = { Text("Tanggal Rujukan (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = complaint, onValueChange = { complaint = it },
                label = { Text("Alasan Rujukan / Keluhan") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = diagnosis, onValueChange = { diagnosis = it },
                label = { Text("Diagnosa (Jika ada)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = transport, onValueChange = { transport = it },
                label = { Text("Kendaraan / Transportasi") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = companionName, onValueChange = { companionName = it },
                label = { Text("Nama Pendamping") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            DeisaRadioGroup(
                label = "Status",
                options = listOf(
                    "referred" to "Dirujuk",
                    "returned" to "Sudah Kembali",
                    "treated" to "Dalam Perawatan"
                ),
                selectedOption = status,
                onOptionSelected = { status = it }
            )

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Catatan Tambahan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = notifyGuardian,
                    onCheckedChange = { notifyGuardian = it },
                    colors = CheckboxDefaults.colors(checkedColor = Primary, uncheckedColor = MutedText)
                )
                Text("Kirim Notifikasi WhatsApp ke Wali", color = OnAppBackground, fontSize = 14.sp)
            }

            Spacer(Modifier.height(80.dp))
        }

        if (state.isLoading) {
            LoadingBox()
        }
    }
}
