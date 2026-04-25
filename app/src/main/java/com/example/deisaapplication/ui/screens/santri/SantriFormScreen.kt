package com.example.deisaapplication.ui.screens.santri

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deisaapplication.data.model.LookupItem
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@Composable
fun SantriFormScreen(
    id: Int?,
    viewModel: SantriViewModel,
    onBack: () -> Unit,
) {
    val santri by viewModel.selectedSantri.collectAsState()
    val lookups by viewModel.lookups.collectAsState()
    val state by viewModel.state.collectAsState()

    // Form states
    var name by remember { mutableStateOf("") }
    var nis by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("L") }
    var selectedClass by remember { mutableStateOf<LookupItem?>(null) }
    var selectedMajor by remember { mutableStateOf<LookupItem?>(null) }
    var selectedDormitory by remember { mutableStateOf<LookupItem?>(null) }
    var dormRoom by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var guardianPhone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.loadLookups()
        if (id != null) {
            viewModel.loadDetail(id)
        }
    }

    LaunchedEffect(santri) {
        santri?.let { s ->
            name = s.name
            nis = s.nis ?: ""
            gender = s.gender
            dormRoom = s.dormRoom ?: ""
            guardianName = s.guardianName ?: ""
            guardianPhone = s.guardianPhone ?: ""
            notes = s.notes ?: ""
            // Map labels back to lookup items if possible (or wait for lookups)
        }
    }
    
    // Auto-select lookups when they arrive
    LaunchedEffect(lookups, santri) {
        if (santri != null) {
            selectedClass = lookups.classes.find { it.name == santri?.schoolClass }
            selectedMajor = lookups.majors.find { it.name == santri?.major }
            selectedDormitory = lookups.dormitories.find { it.name == santri?.dormitory }
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = if (id == null) "Tambah Santri" else "Ubah Santri", onBack = onBack) },
        containerColor = AppBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val data = mapOf(
                        "name" to name,
                        "nis" to nis.takeIf { it.isNotBlank() },
                        "gender" to gender,
                        "class_id" to selectedClass?.id,
                        "major_id" to selectedMajor?.id,
                        "dormitory_id" to selectedDormitory?.id,
                        "dorm_room" to dormRoom.takeIf { it.isNotBlank() },
                        "guardian_name" to guardianName.takeIf { it.isNotBlank() },
                        "guardian_phone" to guardianPhone.takeIf { it.isNotBlank() },
                        "notes" to notes.takeIf { it.isNotBlank() },
                    )
                    viewModel.save(id, data) { success -> if (success) onBack() }
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
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = nis, onValueChange = { nis = it },
                label = { Text("NIS (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            DeisaRadioGroup(
                label = "Jenis Kelamin",
                options = listOf("L" to "Laki-laki", "P" to "Perempuan"),
                selectedOption = gender,
                onOptionSelected = { gender = it }
            )

            DeisaDropdown(
                label = "Kelas",
                items = lookups.classes,
                selectedItem = selectedClass,
                onItemSelected = { selectedClass = it },
                itemLabel = { it.name }
            )

            DeisaDropdown(
                label = "Jurusan",
                items = lookups.majors,
                selectedItem = selectedMajor,
                onItemSelected = { selectedMajor = it },
                itemLabel = { it.name }
            )

            DeisaDropdown(
                label = "Asrama",
                items = lookups.dormitories,
                selectedItem = selectedDormitory,
                onItemSelected = { selectedDormitory = it },
                itemLabel = { it.name }
            )

            OutlinedTextField(
                value = dormRoom, onValueChange = { dormRoom = it },
                label = { Text("Kamar Asrama") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = guardianName, onValueChange = { guardianName = it },
                label = { Text("Nama Wali") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = guardianPhone, onValueChange = { guardianPhone = it },
                label = { Text("No. WhatsApp Wali") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Catatan Medis") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )
            
            Spacer(Modifier.height(80.dp))
        }
        
        if (state.isLoading) {
            LoadingBox()
        }
    }
}
