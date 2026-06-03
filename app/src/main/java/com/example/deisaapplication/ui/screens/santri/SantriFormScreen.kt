package com.example.deisaapplication.ui.screens.santri

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Form states - Profil
    var name by remember { mutableStateOf("") }
    var nis by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("L") }
    var birthPlace by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    
    // Akademik
    var selectedClass by remember { mutableStateOf<LookupItem?>(null) }
    var selectedMajor by remember { mutableStateOf<LookupItem?>(null) }
    var selectedDormitory by remember { mutableStateOf<LookupItem?>(null) }
    var dormRoom by remember { mutableStateOf("") }
    
    // Guardian Data
    var guardianName by remember { mutableStateOf("") }
    var guardianRelationship by remember { mutableStateOf("") }
    var guardianPhone by remember { mutableStateOf("") }
    var guardianJob by remember { mutableStateOf("") }
    var guardianAddress by remember { mutableStateOf("") }
    
    // Health Data
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var medicalHistory by remember { mutableStateOf("") }
    var specialCondition by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var photoBase64 by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedImageUri = uri
        uri?.let {
            try {
                val bytes = context.contentResolver.openInputStream(it)?.readBytes()
                if (bytes != null) {
                    photoBase64 = "data:image/jpeg;base64," + android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(id) {
        viewModel.loadLookups()
        if (id != null) {
            viewModel.loadDetail(id)
        } else {
            viewModel.clearSelected()
        }
    }

    LaunchedEffect(santri) {
        santri?.let { s ->
            name = s.name
            nis = s.nis ?: ""
            gender = s.gender
            birthPlace = s.birthPlace ?: ""
            birthDate = s.birthDate ?: ""
            dormRoom = s.dormRoom ?: ""
            guardianName = s.guardianName ?: ""
            guardianRelationship = s.guardianRelationship ?: ""
            guardianPhone = s.guardianPhone ?: ""
            guardianJob = s.guardianJob ?: ""
            guardianAddress = s.guardianAddress ?: ""
            
            bloodType = s.bloodType ?: ""
            allergies = s.allergies ?: ""
            medicalHistory = s.medicalHistory ?: ""
            specialCondition = s.specialCondition ?: ""
            height = s.height?.toString() ?: ""
            weight = s.weight?.toString() ?: ""
            bloodPressure = s.bloodPressure ?: ""
            notes = s.notes ?: ""
        }
    }
    
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
                        "birth_place" to birthPlace.takeIf { it.isNotBlank() },
                        "birth_date" to birthDate.takeIf { it.isNotBlank() },
                        "class_id" to selectedClass?.id,
                        "major_id" to selectedMajor?.id,
                        "dormitory_id" to selectedDormitory?.id,
                        "dorm_room" to dormRoom.takeIf { it.isNotBlank() },
                        "guardian_name" to guardianName.takeIf { it.isNotBlank() },
                        "guardian_relationship" to guardianRelationship.takeIf { it.isNotBlank() },
                        "guardian_phone" to guardianPhone.takeIf { it.isNotBlank() },
                        "guardian_job" to guardianJob.takeIf { it.isNotBlank() },
                        "guardian_address" to guardianAddress.takeIf { it.isNotBlank() },
                        "blood_type" to bloodType.takeIf { it.isNotBlank() },
                        "allergies" to allergies.takeIf { it.isNotBlank() },
                        "medical_history" to medicalHistory.takeIf { it.isNotBlank() },
                        "special_condition" to specialCondition.takeIf { it.isNotBlank() },
                        "height" to height.toDoubleOrNull(),
                        "weight" to weight.toDoubleOrNull(),
                        "blood_pressure" to bloodPressure.takeIf { it.isNotBlank() },
                        "notes" to notes.takeIf { it.isNotBlank() },
                        "photo_base64" to photoBase64
                    )
                    viewModel.save(id, data) { success -> if (success) onBack() }
                },
                containerColor = Primary,
                contentColor = OnPrimary
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Profil
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, tint = Primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Profil Dasar", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            if (selectedImageUri != null) {
                                Text("Foto profil dipilih", fontSize = 12.sp, color = Primary, modifier = Modifier.padding(bottom = 8.dp))
                                TextButton(onClick = { selectedImageUri = null; photoBase64 = null }) {
                                    Text("Hapus Foto", color = AppError)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.AccountCircle, null, tint = Primary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Pilih Foto Profil Santri", color = Primary)
                                    }
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            label = { Text("Nama Lengkap") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        OutlinedTextField(
                            value = nis, onValueChange = { nis = it },
                            label = { Text("NIS") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        DeisaRadioGroup(
                            label = "Jenis Kelamin",
                            options = listOf("L" to "Laki-laki", "P" to "Perempuan"),
                            selectedOption = gender,
                            onOptionSelected = { gender = it }
                        )
                        OutlinedTextField(
                            value = birthPlace, onValueChange = { birthPlace = it },
                            label = { Text("Tempat Lahir") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        DeisaDatePicker(
                            label = "Tanggal Lahir",
                            value = birthDate,
                            onValueChange = { birthDate = it }
                        )
                    }
                }
            }

            // Section 2: Akademik
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.School, null, tint = Secondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Pendidikan & Hunian", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            label = { Text("Nomor Kamar") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant)
                        )
                    }
                }
            }

            // Section 3: Wali
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.FamilyRestroom, null, tint = AppWarning, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kontak Wali", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = guardianName, onValueChange = { guardianName = it },
                            label = { Text("Nama Wali") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = guardianRelationship, onValueChange = { guardianRelationship = it },
                                label = { Text("Hubungan") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant)
                            )
                            OutlinedTextField(
                                value = guardianJob, onValueChange = { guardianJob = it },
                                label = { Text("Pekerjaan") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant)
                            )
                        }
                        OutlinedTextField(
                            value = guardianPhone, onValueChange = { guardianPhone = it },
                            label = { Text("WhatsApp (e.g. 0812...)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        OutlinedTextField(
                            value = guardianAddress, onValueChange = { guardianAddress = it },
                            label = { Text("Alamat") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant)
                        )
                    }
                }
            }

            // Section 4: Kesehatan
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MedicalInformation, null, tint = AppError, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Data Kesehatan", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = bloodType, onValueChange = { bloodType = it },
                                label = { Text("Gol. Darah") },
                                modifier = Modifier.weight(0.4f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                            )
                            OutlinedTextField(
                                value = bloodPressure, onValueChange = { bloodPressure = it },
                                label = { Text("Tekanan Darah") },
                                modifier = Modifier.weight(0.6f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = height, onValueChange = { height = it },
                                label = { Text("Tinggi (cm)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                            )
                            OutlinedTextField(
                                value = weight, onValueChange = { weight = it },
                                label = { Text("Berat (kg)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                            )
                        }
                        OutlinedTextField(
                            value = allergies, onValueChange = { allergies = it },
                            label = { Text("Alergi") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        OutlinedTextField(
                            value = medicalHistory, onValueChange = { medicalHistory = it },
                            label = { Text("Riwayat Penyakit") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        OutlinedTextField(
                            value = specialCondition, onValueChange = { specialCondition = it },
                            label = { Text("Kondisi Khusus") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                        )
                        OutlinedTextField(
                            value = notes, onValueChange = { notes = it },
                            label = { Text("Catatan Kesehatan Tambahan") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppError, unfocusedBorderColor = AppSurfaceVariant)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(100.dp))
        }
        
        if (state.isLoading) LoadingBox()
    }
}
