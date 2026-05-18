package com.example.deisaapplication.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.*
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    userName: String,
    onOpenDrawer: () -> Unit,
    onNavigateSicknessCase: () -> Unit,
    onNavigateMedicine: () -> Unit,
    onNavigateReferral: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DEIHealth", color = NavyBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = NavyBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppSurface)
            )
        },
        containerColor = AppBackground,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> DeisaLoadingBar()
                is DashboardUiState.Error   -> ErrorBox(state.message, { viewModel.load() })
                is DashboardUiState.Success -> DashboardContent(
                    data = state.data,
                    userName = userName,
                    onNavigateSicknessCase = onNavigateSicknessCase,
                    onNavigateMedicine = onNavigateMedicine
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    userName: String,
    onNavigateSicknessCase: () -> Unit,
    onNavigateMedicine: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 1. Header & Welcome message
        item {
            Column {
                Text(
                    text = "Selamat datang,",
                    fontSize = 16.sp,
                    color = MutedText
                )
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            }
        }

        // 2. Alert Section for Low Stock / Expired Medicine
        if (data.alertMedicines.isNotEmpty()) {
            item {
                MedicineAlertsSection(
                    alerts = data.alertMedicines,
                    onNavigateMedicine = onNavigateMedicine
                )
            }
        }

        // 3. Status summary grid (4 metric cards arranged beautifully)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusSummaryCard(
                        title = "Sakit Aktif",
                        value = data.stats.santriSakitAktif.toString(),
                        icon = Icons.Default.Sick,
                        color = AppError,
                        modifier = Modifier.weight(1f)
                    )
                    StatusSummaryCard(
                        title = "Total Kunjungan",
                        value = data.stats.santriTotal.toString(),
                        icon = Icons.Default.Person,
                        color = Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusSummaryCard(
                        title = "Santri Dirujuk",
                        value = data.stats.rujukan.toString(),
                        icon = Icons.Default.EventNote,
                        color = Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatusSummaryCard(
                        title = "Masalah Obat",
                        value = (data.stats.obatMenipis + data.stats.obatKadaluarsa).toString(),
                        icon = Icons.Default.Warning,
                        color = AppWarning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Sickness Trends (Line chart)
        item {
            SicknessTrendLineChart(trends = data.sicknessTrends)
        }

        // 5. Case Distribution (Circular Donut Chart)
        item {
            CaseDistributionDonutChart(distributions = data.caseDistribution)
        }

        // 6. Sickness by Class distribution (Horizontal Bar Chart)
        item {
            val classBars = data.classDistribution.map { it.className to it.count }
            HorizontalDistributionBarChart(
                title = "Distribusi Kasus per Kelas",
                subtitle = "Berdasarkan jumlah kunjungan sakit santri per kelas",
                items = classBars,
                color = Primary
            )
        }

        // 7. Sickness by Major distribution (Horizontal Bar Chart)
        item {
            val majorBars = data.majorDistribution.map { it.majorName to it.count }
            HorizontalDistributionBarChart(
                title = "Distribusi Kasus per Jurusan",
                subtitle = "Berdasarkan jumlah kunjungan sakit santri per jurusan",
                items = majorBars,
                color = Secondary
            )
        }

        // 8. Top used medicines (Horizontal Bar Chart)
        item {
            val medicineBars = data.frequentMedicines.map { it.medicineName to it.count }
            HorizontalDistributionBarChart(
                title = "Penggunaan Obat Terbanyak",
                subtitle = "Berdasarkan frekuensi pemberian obat ke santri",
                items = medicineBars,
                color = NavyBlue
            )
        }

        // 9. Recent Cases Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Santri Sakit Terakhir",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnAppBackground
                )
                TextButton(onClick = onNavigateSicknessCase) {
                    Text("Lihat Semua", color = LightBlue)
                }
            }
        }

        // 10. Recent cases list
        if (data.recentCases.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data santri sakit.", color = MutedText)
                }
            }
        } else {
            items(data.recentCases) { case ->
                SantriCardComponent(
                    case = case,
                    onClick = { /* Detail navigation if needed */ }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun StatusSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnAppBackground
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MedicineAlertsSection(
    alerts: List<AlertMedicine>,
    onNavigateMedicine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AppError,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Peringatan Obat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnAppBackground
                )
            }
            TextButton(onClick = onNavigateMedicine) {
                Text("Kelola Obat", color = LightBlue, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrollable alert cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            alerts.forEach { alert ->
                val isExp = alert.status == "expired"
                val isNearExp = alert.status == "hampir_kadaluarsa"
                val themeColor = if (isExp) AppError else AppWarning
                val badgeText = when (alert.status) {
                    "expired" -> "KADALUARSA"
                    "hampir_kadaluarsa" -> "HAMPIR KADALUARSA"
                    else -> "STOK MENIPIS"
                }

                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .padding(bottom = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    border = BorderStroke(1.dp, themeColor.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(themeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = themeColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isExp || isNearExp) Icons.Default.EventNote else Icons.Default.Warning,
                                contentDescription = null,
                                tint = themeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = alert.name,
                            fontWeight = FontWeight.Bold,
                            color = OnAppBackground,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (alert.status == "menipis") {
                            Text(
                                text = "Sisa stok: ${alert.stock} ${alert.unit ?: "satuan"}",
                                color = AppError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Tgl Kedaluwarsa: ${alert.expiryDate ?: "-"}",
                                color = themeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SicknessTrendLineChart(
    trends: List<SicknessTrend>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tren Kasus Sakit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnAppBackground
                    )
                    Text(
                        text = "Kunjungan sakit 14 hari terakhir",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("14 Hari", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (trends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data tren.", color = MutedText)
                }
            } else {
                val maxCount = trends.maxOf { it.count }.coerceAtLeast(1)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    val pointsCount = trends.size
                    val xInterval = width / (pointsCount - 1).coerceAtLeast(1)

                    val path = Path()
                    val fillPath = Path()

                    trends.forEachIndexed { index, trend ->
                        val x = index * xInterval
                        val y = height - (trend.count.toFloat() / maxCount.toFloat() * (height - 30f)) - 15f

                        if (index == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }

                        if (index == pointsCount - 1) {
                            fillPath.lineTo(x, height)
                            fillPath.close()
                        }
                    }

                    // Draw area gradient fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Primary.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw path outline
                    drawPath(
                        path = path,
                        color = Primary,
                        style = Stroke(width = 6f)
                    )

                    // Draw highlight circles
                    trends.forEachIndexed { index, trend ->
                        val x = index * xInterval
                        val y = height - (trend.count.toFloat() / maxCount.toFloat() * (height - 30f)) - 15f

                        drawCircle(
                            color = AppSurface,
                            radius = 8f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Primary,
                            radius = 5f,
                            center = Offset(x, y)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dates labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(trends.first().date, color = MutedText, fontSize = 10.sp)
                    Text(trends.last().date, color = MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun CaseDistributionDonutChart(
    distributions: List<CaseDistribution>,
    modifier: Modifier = Modifier
) {
    if (distributions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Tidak ada data distribusi kasus.", color = MutedText, fontSize = 14.sp)
        }
        return
    }

    val totalCount = distributions.sumOf { it.count }
    if (totalCount == 0) return

    val colors = listOf(
        Primary,
        Secondary,
        NavyBlue,
        Color(0xFF8B5CF6),
        AppWarning
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Status Kunjungan Santri",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnAppBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Canvas
                Canvas(
                    modifier = Modifier
                        .size(120.dp)
                        .weight(1f)
                ) {
                    var startAngle = 0f
                    distributions.forEachIndexed { index, dist ->
                        val sweepAngle = (dist.count.toFloat() / totalCount.toFloat()) * 360f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 24f)
                        )
                        startAngle += sweepAngle
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Legends listing
                Column(
                    modifier = Modifier.weight(1.5f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    distributions.forEachIndexed { index, dist ->
                        val percent = ((dist.count.toFloat() / totalCount.toFloat()) * 100).toInt()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(colors[index % colors.size], RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = dist.statusLabel ?: dist.status,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnAppBackground
                                )
                                Text(
                                    text = "${dist.count} kasus ($percent%)",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalDistributionBarChart(
    title: String,
    subtitle: String,
    items: List<Pair<String, Int>>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnAppBackground
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MutedText
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data distribusi.", color = MutedText, fontSize = 14.sp)
                }
            } else {
                val maxVal = items.maxOf { it.second }.coerceAtLeast(1)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.forEach { (label, value) ->
                        val progress = value.toFloat() / maxVal.toFloat()
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = OnAppBackground
                                )
                                Text(
                                    text = "$value kasus",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Sleek progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(AppSurfaceVariant, RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(color.copy(alpha = 0.7f), color)
                                            ),
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
