package com.example.deisaapplication.ui.screens.report

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import com.example.deisaapplication.data.model.ReportData
import com.example.deisaapplication.data.model.MedicineReportData
import com.example.deisaapplication.data.model.MedicineReportItem
import com.example.deisaapplication.data.model.SicknessCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    onOpenDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Snackbar Host State
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toast) {
        state.toast?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DeisaTopBar(
                title = "Laporan & Cetak",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = {
                        val uri = if (state.selectedReportType == "sickness") {
                            state.sicknessReport?.let {
                                PdfReportGenerator.generateSicknessReportPdf(
                                    context = context,
                                    reportData = it,
                                    startDate = state.startDate,
                                    endDate = state.endDate
                                )
                            }
                        } else {
                            state.medicineReport?.let {
                                PdfReportGenerator.generateMedicineReportPdf(
                                    context = context,
                                    reportData = it,
                                    startDate = state.startDate,
                                    endDate = state.endDate
                                )
                            }
                        }

                        if (uri != null) {
                            viewModel.showToast("PDF berhasil disimpan ke folder Downloads/DEIHealth!")
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(Intent.createChooser(intent, "Buka Laporan PDF"))
                            } catch (e: Exception) {
                                viewModel.showToast("Gagal membuka viewer PDF. File tersimpan di Downloads.")
                            }
                        } else {
                            viewModel.showToast("Gagal membuat PDF. Cek kembali data laporan.")
                        }
                    }) {
                        Icon(Icons.Filled.PictureAsPdf, "Cetak PDF", tint = Primary)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            // --- TAB SELECTOR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppSurface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton(
                    label = "Santri Sakit",
                    selected = state.selectedReportType == "sickness",
                    icon = Icons.Filled.Sick,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.setReportType("sickness")
                }
                TabButton(
                    label = "Mutasi & Obat",
                    selected = state.selectedReportType == "medicine",
                    icon = Icons.Filled.Medication,
                    color = Secondary,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.setReportType("medicine")
                }
            }

            // --- FILTER DATE SECTION ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Filter Rentang Tanggal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = OnAppBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DatePickerField(
                            label = "Tanggal Mulai",
                            dateStr = state.startDate,
                            context = context,
                            modifier = Modifier.weight(1f)
                        ) { newDate ->
                            viewModel.setDateRange(newDate, state.endDate)
                        }

                        DatePickerField(
                            label = "Tanggal Selesai",
                            dateStr = state.endDate,
                            context = context,
                            modifier = Modifier.weight(1f)
                        ) { newDate ->
                            viewModel.setDateRange(state.startDate, newDate)
                        }
                    }
                }
            }

            // --- CONTENT AND PREVIEW AREA ---
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingBox()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorBox(message = state.error!!, onRetry = { viewModel.loadData() })
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preview Dokumen PDF (A4)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MutedText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    val uri = if (state.selectedReportType == "sickness") {
                                        state.sicknessReport?.let {
                                            PdfReportGenerator.generateSicknessReportPdf(
                                                context = context,
                                                reportData = it,
                                                startDate = state.startDate,
                                                endDate = state.endDate
                                            )
                                        }
                                    } else {
                                        state.medicineReport?.let {
                                            PdfReportGenerator.generateMedicineReportPdf(
                                                context = context,
                                                reportData = it,
                                                startDate = state.startDate,
                                                endDate = state.endDate
                                            )
                                        }
                                    }
                                    if (uri != null) {
                                        viewModel.showToast("Laporan PDF berhasil diunduh!")
                                    }
                                }
                                .padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Filled.Download, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Download", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Simulated A4 PDF Preview Page
                    A4PreviewContainer(state = state)

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun TabButton(
    label: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color.copy(alpha = 0.12f) else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) color else AppSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) color else MutedText,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) color else MutedText
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    dateStr: String,
    context: Context,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    // Parse current dateStr if possible
    try {
        val parsed = LocalDate.parse(dateStr)
        calendar.set(parsed.year, parsed.monthValue - 1, parsed.dayOfMonth)
    } catch (e: Exception) {}

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val monthStr = String.format("%02d", month + 1)
            val dayStr = String.format("%02d", dayOfMonth)
            onDateSelected("$year-$monthStr-$dayStr")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedTextField(
        value = dateStr,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Pilih Tanggal",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        },
        modifier = modifier.clickable { datePickerDialog.show() },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = AppSurfaceVariant,
            focusedTextColor = OnAppBackground,
            unfocusedTextColor = OnAppBackground
        )
    )
}

@Composable
fun A4PreviewContainer(state: ReportState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray)
            .padding(16.dp)
    ) {
        if (state.selectedReportType == "sickness") {
            state.sicknessReport?.let { sicknessReport ->
                SicknessReportPreview(report = sicknessReport, start = state.startDate, end = state.endDate)
            } ?: Box(Modifier.height(300.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Tidak ada data laporan", color = Color.Gray)
            }
        } else {
            state.medicineReport?.let { medicineReport ->
                MedicineReportPreview(report = medicineReport, start = state.startDate, end = state.endDate)
            } ?: Box(Modifier.height(300.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Tidak ada data laporan", color = Color.Gray)
            }
        }
    }
}

@Composable
fun SicknessReportPreview(
    report: ReportData,
    start: String,
    end: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // A4 Blue Header Accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Primary)
        )
        Spacer(Modifier.height(12.dp))

        // Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEIHealth",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Pondok Pesantren Daar El-Ilmi",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
            Text(
                text = "LAPORAN SAKIT",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Primary
            )
        }

        Spacer(Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(Modifier.height(8.dp))

        // Info Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Periode Laporan:", fontSize = 10.sp, color = Color.Gray)
                Text("$start s/d $end", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tanggal Unduh:", fontSize = 10.sp, color = Color.Gray)
                Text(LocalDate.now().toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Summary Stats Grid
        Text(
            text = "RINGKASAN STATUS KESEHATAN SANTRI",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewStatBox(label = "Total Sakit", value = report.summary.santriSakit.toString(), modifier = Modifier.weight(1f))
            PreviewStatBox(label = "Sembuh", value = report.summary.sembuh.toString(), modifier = Modifier.weight(1f))
            PreviewStatBox(label = "Dirujuk RS", value = report.summary.rujukanRs.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Table Header
        Text(
            text = "DAFTAR KUNJUNGAN SAKIT SANTRI",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Simple Simulated Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                Text("Nama", modifier = Modifier.weight(3f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Keluhan", modifier = Modifier.weight(3f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Diagnosa", modifier = Modifier.weight(3f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Status", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.End)
            }

            // Data Rows
            if (report.sicknessCases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada kunjungan tercatat.", fontSize = 11.sp, color = Color.Gray)
                }
            } else {
                report.sicknessCases.take(10).forEach { item ->
                    Divider(color = Color.LightGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Text(item.santri?.name ?: "-", modifier = Modifier.weight(3f), fontSize = 10.sp, color = Color.Black)
                        Text(item.complaint ?: "-", modifier = Modifier.weight(3f), fontSize = 10.sp, color = Color.Black)
                        Text(item.diagnosis ?: "-", modifier = Modifier.weight(3f), fontSize = 10.sp, color = Color.Black)
                        
                        val statusText = when (item.status) {
                            "observed" -> "Observasi"
                            "rawat_inap" -> "Rawat Inap"
                            "referred" -> "Dirujuk"
                            "recovered" -> "Sembuh"
                            else -> item.status
                        }
                        Text(statusText, modifier = Modifier.weight(2f), fontSize = 10.sp, color = Color.Black, textAlign = TextAlign.End)
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // Signature Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Petugas UKS Daar El-Ilmi,", fontSize = 10.sp, color = Color.DarkGray)
                Spacer(Modifier.height(48.dp))
                Text("( ______________________ )", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun MedicineReportPreview(
    report: MedicineReportData,
    start: String,
    end: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // A4 Orange Header Accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Secondary)
        )
        Spacer(Modifier.height(12.dp))

        // Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEIHealth",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Pondok Pesantren Daar El-Ilmi",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
            Text(
                text = "LAPORAN INVENTORI OBAT",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Secondary
            )
        }

        Spacer(Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(Modifier.height(8.dp))

        // Info Block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Periode Laporan:", fontSize = 10.sp, color = Color.Gray)
                Text("$start s/d $end", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tanggal Unduh:", fontSize = 10.sp, color = Color.Gray)
                Text(LocalDate.now().toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Summary Stats Grid
        Text(
            text = "RINGKASAN MUTASI & STOK OBAT",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewStatBox(label = "Total Obat", value = report.summary.totalObat.toString(), modifier = Modifier.weight(1f))
            PreviewStatBox(label = "Stok Kritis", value = report.summary.obatMenipis.toString(), modifier = Modifier.weight(1f))
            PreviewStatBox(label = "Mutasi Terjadi", value = report.summary.totalMutasi.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Table Header
        Text(
            text = "DAFTAR DETAIL INVENTORI OBAT",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Simple Simulated Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                Text("Kode", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Nama Obat", modifier = Modifier.weight(4f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Stok Akhir", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Masuk", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.End)
                Text("Keluar", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.End)
            }

            // Data Rows
            if (report.medicines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada mutasi obat tercatat.", fontSize = 11.sp, color = Color.Gray)
                }
            } else {
                report.medicines.take(10).forEach { item ->
                    Divider(color = Color.LightGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Text(item.code, modifier = Modifier.weight(2f), fontSize = 10.sp, color = Color.Black)
                        Text(item.name, modifier = Modifier.weight(4f), fontSize = 10.sp, color = Color.Black)
                        Text("${item.stock} ${item.unit}", modifier = Modifier.weight(2f), fontSize = 10.sp, color = Color.Black)
                        Text(item.totalIn.toString(), modifier = Modifier.weight(2f), fontSize = 10.sp, color = Color.Black, textAlign = TextAlign.End)
                        Text(item.totalOut.toString(), modifier = Modifier.weight(2f), fontSize = 10.sp, color = Color.Black, textAlign = TextAlign.End)
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // Signature Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Penanggung Jawab Inventori UKS,", fontSize = 10.sp, color = Color.DarkGray)
                Spacer(Modifier.height(48.dp))
                Text("( ______________________ )", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun PreviewStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
            .background(Color(0xFFFCFCFC))
            .padding(8.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = Color.Gray)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}
