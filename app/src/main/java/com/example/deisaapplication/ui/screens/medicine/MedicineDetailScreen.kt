package com.example.deisaapplication.ui.screens.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.Medicine
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.screens.santri.DetailItem
import com.example.deisaapplication.ui.theme.*

@Composable
fun MedicineDetailScreen(
    id: Int,
    viewModel: MedicineViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    val medicine by viewModel.selectedMedicine.collectAsState()
    var showMutateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Detail Obat",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(id) }) {
                        Icon(Icons.Filled.Edit, "Ubah", tint = OnAppBackground)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        if (medicine == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            MedicineDetailContent(
                medicine = medicine!!,
                onMutateClick = { showMutateDialog = true },
                modifier = Modifier.padding(pv)
            )
        }
    }

    if (showMutateDialog && medicine != null) {
        MutateStockDialog(
            onDismiss = { showMutateDialog = false },
            onSubmit = { type, amount, notes ->
                viewModel.mutateStock(medicine!!.id, type, amount, notes) { success ->
                    if (success) showMutateDialog = false
                }
            }
        )
    }
}

@Composable
private fun MedicineDetailContent(
    medicine: Medicine,
    onMutateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name & Status
        DeisaCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(medicine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OnAppBackground)
                    Text("Kode: ${medicine.code}", fontSize = 13.sp, color = MutedText)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = AppSurfaceVariant.copy(alpha = 0.5f)) {
                        Text(
                            "${medicine.kategori} • ${medicine.formulation}", 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp, 
                            color = OnAppBackground
                        )
                    }
                }
                StatusBadge(medicine.status, when (medicine.status) {
                    "aman"              -> "Aman"
                    "stok_kritis"       -> "Stok Kritis"
                    "kadaluarsa"        -> "Kadaluarsa"
                    "segera_kadaluarsa" -> "Segera Exp"
                    "stok_menipis"      -> "Stok Menipis"
                    "hampir_kadaluarsa"  -> "Hampir Exp"
                    "habis"             -> "Habis"
                    else                -> medicine.status
                })
            }
        }

        // Stock Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SectionHeader("Informasi Stok & Lokasi")
            Button(
                onClick = onMutateClick,
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Filled.SwapHoriz, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Mutasi Stok", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        DeisaCard {
            DetailItem(Icons.Filled.Inventory, "Stok Saat Ini", "${medicine.stock} ${medicine.unit}")
            DeisaDivider()
            DetailItem(Icons.Filled.ReportProblem, "Stok Minimum", "${medicine.minimumStock} ${medicine.unit}")
            DeisaDivider()
            DetailItem(Icons.Filled.LocationOn, "Lokasi Penyimpanan", medicine.location ?: "Tidak ditentukan")
            DeisaDivider()
            DetailItem(Icons.Filled.Event, "Tanggal Kadaluarsa", medicine.expiryDate ?: "-")
        }

        // Description
        if (!medicine.description.isNullOrEmpty()) {
            SectionHeader("Deskripsi / Catatan")
            DeisaCard {
                Text(medicine.description ?: "", fontSize = 14.sp, color = OnAppBackground, lineHeight = 20.sp)
            }
        }

        // Timeline: Riwayat Mutasi Stok
        SectionHeader("Riwayat Mutasi Stok")
        if (medicine.stockHistory.isNotEmpty()) {
            DeisaCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    medicine.stockHistory.forEach { history ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isPositive = history.type in listOf("masuk", "retur", "penyesuaian")
                            val icon = if (isPositive) Icons.Default.AddCircle else Icons.Default.RemoveCircle
                            val iconTint = if (isPositive) Primary else AppError
                            
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(iconTint.copy(alpha = 0.08f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = when (history.type) {
                                        "masuk" -> "Stok Masuk"
                                        "keluar" -> "Stok Keluar"
                                        "penyesuaian" -> "Penyesuaian"
                                        "rusak" -> "Obat Rusak"
                                        "kadaluarsa" -> "Kadaluarsa"
                                        "retur" -> "Retur Obat"
                                        else -> history.type.replaceFirstChar { it.uppercase() }
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = OnAppBackground
                                )
                                Text(
                                    text = history.notes ?: "Tidak ada catatan.",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isPositive) "+" else "-"}${history.amount}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = iconTint
                                )
                                Text(
                                    text = history.date,
                                    fontSize = 9.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada riwayat mutasi stok untuk obat ini.",
                    fontSize = 12.sp,
                    color = MutedText,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun MutateStockDialog(
    onDismiss: () -> Unit,
    onSubmit: (type: String, amount: Int, notes: String?) -> Unit
) {
    var type by remember { mutableStateOf("masuk") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val types = listOf(
        "masuk" to "Stok Masuk",
        "keluar" to "Stok Keluar",
        "penyesuaian" to "Penyesuaian",
        "rusak" to "Obat Rusak",
        "kadaluarsa" to "Kadaluarsa",
        "retur" to "Retur Obat"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text("Pencatatan Mutasi Stok", fontWeight = FontWeight.Bold, color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Pilih Jenis Mutasi:", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(130.dp)
                ) {
                    items(types.size) { idx ->
                        val item = types[idx]
                        val selected = type == item.first
                        val chipColor = if (selected) Secondary else AppBackground
                        val textColor = if (selected) Color.White else OnAppBackground
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { type = item.first },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = chipColor)
                        ) {
                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                Text(item.second, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah (Satuan)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Keterangan") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amtVal = amount.toIntOrNull() ?: 0
                    if (amtVal > 0) {
                        onSubmit(type, amtVal, notes.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
            ) {
                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MutedText)
            }
        }
    )
}
