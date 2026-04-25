package com.example.deisaapplication.ui.screens.santri

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.Santri
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@Composable
fun SantriDetailScreen(
    id: Int,
    viewModel: SantriViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    val santri by viewModel.selectedSantri.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Detail Santri",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(id) }) {
                        Icon(Icons.Filled.Edit, "Ubah", tint = Primary)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        if (santri == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            SantriDetailContent(santri!!, Modifier.padding(pv))
        }
    }
}

@Composable
private fun SantriDetailContent(santri: Santri, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        DeisaCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(72.dp).background(if(santri.gender == "L") Primary.copy(0.1f) else AppError.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        (santri.name.take(1)).uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        color = if(santri.gender == "L") Primary else AppError,
                        fontSize = 32.sp,
                    )
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text(santri.name, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = OnAppBackground)
                    Text("NIS: ${santri.nis ?: "-"}", color = MutedText, fontSize = 14.sp)
                    Surface(shape = RoundedCornerShape(8.dp), color = AppSurfaceVariant.copy(0.5f)) {
                        Text(santri.genderLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, color = MutedText)
                    }
                }
            }
        }

        // Academic Info
        SectionHeader("Informasi Akademik")
        DeisaCard {
            DetailItem(Icons.Filled.School, "Kelas", santri.schoolClass ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Category, "Jurusan", santri.major ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Home, "Asrama", santri.dormitory ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.MeetingRoom, "Kamar", santri.dormRoom ?: "-")
        }

        // Guardian Info
        SectionHeader("Data Wali Santri")
        DeisaCard {
            DetailItem(Icons.Filled.Person, "Nama Wali", santri.guardianName ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Phone, "No. WhatsApp", santri.guardianPhone ?: "-")
        }

        // Sickness History
        if (!santri.recentSickness.isNullOrEmpty()) {
            SectionHeader("Riwayat Penyakit")
            santri.recentSickness.forEach { sick ->
                DeisaCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(sick.complaint, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnAppBackground)
                            Text(sick.visitDate ?: "-", fontSize = 12.sp, color = MutedText)
                        }
                        StatusBadge(sick.status, sick.status.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }

        // Referral History
        if (!santri.recentReferrals.isNullOrEmpty()) {
            SectionHeader("Riwayat Rujukan")
            santri.recentReferrals.forEach { ref ->
                DeisaCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(ref.hospitalName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnAppBackground)
                            Text(ref.referralDate ?: "-", fontSize = 12.sp, color = MutedText)
                        }
                        StatusBadge(ref.status, ref.status.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }

        // Additional Info
        SectionHeader("Lainnya")
        DeisaCard {
            DetailItem(Icons.Filled.LocationOn, "Tempat Lahir", santri.birthPlace ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.CalendarMonth, "Tanggal Lahir", santri.birthDate ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Notes, "Catatan Medis", santri.notes ?: "Tidak ada catatan.")
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(32.dp).background(Primary.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnAppBackground)
        }
    }
}
