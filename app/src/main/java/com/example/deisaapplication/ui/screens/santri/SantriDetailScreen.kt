package com.example.deisaapplication.ui.screens.santri

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
    canManageData: Boolean,
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
                    if (canManageData) {
                        IconButton(onClick = { onEdit(id) }) {
                            Icon(Icons.Filled.Edit, "Ubah", tint = Primary)
                        }
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

private enum class DetailTab(val label: String, val icon: ImageVector) {
    PRIBADI("Pribadi", Icons.Filled.Person),
    AKADEMIK("Akademik", Icons.Filled.School),
    KESEHATAN("Kesehatan", Icons.Filled.MedicalServices)
}

@Composable
private fun SantriDetailContent(santri: Santri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(DetailTab.PRIBADI) }

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
                    Text(santri.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OnAppBackground)
                    Text("NIS: ${santri.nis ?: "-"}", color = MutedText, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = AppSurfaceVariant.copy(0.5f)) {
                        Text(santri.genderLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, color = MutedText)
                    }
                }
            }
        }

        // Pill Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppSurfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DetailTab.values().forEach { tab ->
                val selected = activeTab == tab
                val bg by animateColorAsState(
                    targetValue = if (selected) Primary else Color.Transparent,
                    animationSpec = tween(durationMillis = 300)
                )
                val contentColor by animateColorAsState(
                    targetValue = if (selected) Color.White else MutedText,
                    animationSpec = tween(durationMillis = 300)
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .clickable { activeTab = tab },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.label,
                        color = contentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tab Content
        when (activeTab) {
            DetailTab.PRIBADI -> {
                // Section 1: Profil Dasar
                SectionHeader("Profil Dasar")
                DeisaCard {
                    DetailItem(Icons.Filled.LocationOn, "Tempat Lahir", santri.birthPlace ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.CalendarMonth, "Tanggal Lahir", santri.birthDate ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Wc, "Jenis Kelamin", santri.genderLabel)
                }

                // Section 2: Kontak Wali
                SectionHeader("Data Wali Santri")
                DeisaCard {
                    DetailItem(Icons.Filled.Person, "Nama Wali", santri.guardianName ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.FamilyRestroom, "Hubungan", santri.guardianRelationship ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Work, "Pekerjaan Wali", santri.guardianJob ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Map, "Alamat Wali", santri.guardianAddress ?: "-")
                    DeisaDivider()
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            DetailItem(Icons.Filled.Phone, "No. WhatsApp", santri.guardianPhone ?: "-")
                        }
                        if (!santri.guardianPhone.isNullOrBlank()) {
                            IconButton(
                                onClick = {
                                    val phone = santri.guardianPhone.replace("[^0-9]".toRegex(), "")
                                    val finalPhone = if (phone.startsWith("0")) "62" + phone.substring(1) else phone
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$finalPhone"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.background(Color(0xFF25D366), CircleShape).size(36.dp)
                            ) {
                                Icon(Icons.Filled.Message, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            DetailTab.AKADEMIK -> {
                SectionHeader("Informasi Akademik & Kelas")
                DeisaCard {
                    DetailItem(Icons.Filled.School, "Kelas", santri.schoolClass ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Category, "Jurusan", santri.major ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Home, "Asrama", santri.dormitory ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.MeetingRoom, "Kamar", santri.dormRoom ?: "-")
                }
            }

            DetailTab.KESEHATAN -> {
                SectionHeader("Informasi Kesehatan Lengkap")
                DeisaCard {
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            DetailItem(Icons.Filled.Bloodtype, "Gol. Darah", santri.bloodType ?: "-")
                        }
                        Column(Modifier.weight(1f)) {
                            DetailItem(Icons.Filled.MonitorWeight, "BB / TB", "${santri.weight ?: "-"} kg / ${santri.height ?: "-"} cm")
                        }
                    }
                    DeisaDivider()
                    DetailItem(Icons.Filled.Speed, "Tekanan Darah", santri.bloodPressure ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.ReportProblem, "Alergi", santri.allergies ?: "Tidak ada alergi.")
                    DeisaDivider()
                    DetailItem(Icons.Filled.History, "Riwayat Penyakit", santri.medicalHistory ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Sick, "Kondisi Khusus", santri.specialCondition ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Notes, "Catatan Tambahan", santri.notes ?: "-")
                }

                // Sickness History
                if (!santri.recentSickness.isNullOrEmpty()) {
                    SectionHeader("Riwayat Kunjungan UKS")
                    santri.recentSickness.forEach { sick ->
                        DeisaCard(Modifier.padding(vertical = 4.dp)) {
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
                    SectionHeader("Riwayat Rujukan Rumah Sakit")
                    santri.recentReferrals.forEach { ref ->
                        DeisaCard(Modifier.padding(vertical = 4.dp)) {
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
            }
        }

        // Others
        SectionHeader("Lainnya")
        DeisaCard {
            DetailItem(Icons.Filled.LocationOn, "Tempat Lahir", santri.birthPlace ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.CalendarMonth, "Tanggal Lahir", santri.birthDate ?: "-")
        }
        
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
