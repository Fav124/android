package com.example.deisaapplication.ui.screens.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
                        Icon(androidx.compose.material.icons.Icons.Filled.Edit, "Ubah", tint = OnAppBackground)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        if (medicine == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            MedicineDetailContent(medicine!!, Modifier.padding(pv))
        }
    }
}

@Composable
private fun MedicineDetailContent(medicine: Medicine, modifier: Modifier = Modifier) {
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
                Column {
                    Text(medicine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = OnAppBackground)
                    Text("Unit: ${medicine.unit}", fontSize = 14.sp, color = MutedText)
                }
                StatusBadge(medicine.status, when (medicine.status) {
                    "aman"              -> "Aman"
                    "stok_kritis"       -> "Stok Kritis"
                    "kadaluarsa"        -> "Kadaluarsa"
                    "segera_kadaluarsa" -> "Segera Exp"
                    else                -> medicine.status
                })
            }
        }

        // Stock Info
        SectionHeader("Informasi Stok")
        DeisaCard {
            DetailItem(Icons.Filled.Inventory, "Stok Saat Ini", "${medicine.stock} ${medicine.unit}")
            DeisaDivider()
            DetailItem(Icons.Filled.ReportProblem, "Stok Minimum", "${medicine.minimumStock} ${medicine.unit}")
            DeisaDivider()
            DetailItem(Icons.Filled.Event, "Tanggal Kadaluarsa", medicine.expiryDate ?: "-")
        }

        // Description
        if (!medicine.description.isNullOrEmpty()) {
            SectionHeader("Deskripsi / Catatan")
            DeisaCard {
                Text(medicine.description, fontSize = 14.sp, color = OnAppBackground)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
