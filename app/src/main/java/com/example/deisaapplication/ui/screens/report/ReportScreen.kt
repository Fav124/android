package com.example.deisaapplication.ui.screens.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.SicknessTrend
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    onOpenDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Laporan & Grafik",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { /* TODO: Generate PDF */ }) {
                        Icon(Icons.Filled.PictureAsPdf, "Download PDF", tint = Primary)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        Box(Modifier.fillMaxSize().padding(pv)) {
            when {
                state.isLoading && state.dashboardData == null -> LoadingBox()
                state.error != null -> ErrorBox(state.error!!, { viewModel.loadData() })
                else -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Summary Stats
                        state.reportData?.summary?.let { summary ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ModernStatCard("Total Santri", summary.totalSantri.toString(), icon = Icons.Filled.Person, color = Primary, modifier = Modifier.weight(1f))
                                ModernStatCard("Sakit", summary.santriSakit.toString(), icon = Icons.Filled.LocalHospital, color = AppError, modifier = Modifier.weight(1f))
                            }
                        }

                        // Trend Graph
                        state.dashboardData?.sicknessTrends?.let { trends ->
                            DeisaCard {
                                Text("Tren Kasus Sakit (7 Hari Terakhir)", fontWeight = FontWeight.Bold, color = OnAppBackground, fontSize = 14.sp)
                                Spacer(Modifier.height(24.dp))
                                SicknessTrendChart(trends, Modifier.height(200.dp).fillMaxWidth())
                            }
                        }

                        // Top Diagnoses
                        state.reportData?.topDiagnoses?.let { diagnoses ->
                            DeisaCard {
                                Text("Top 5 Diagnosa Terbanyak", fontWeight = FontWeight.Bold, color = OnAppBackground, fontSize = 14.sp)
                                Spacer(Modifier.height(16.dp))
                                diagnoses.take(5).forEach { diag ->
                                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(diag.diagnosis ?: "Tidak Diketahui", fontSize = 13.sp, color = OnAppBackground)
                                        Text("${diag.total} Kasus", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
                                    }
                                    LinearProgressIndicator(
                                        progress = { diag.total.toFloat() / (diagnoses.firstOrNull()?.total ?: 1) },
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = Primary,
                                        trackColor = AppSurfaceVariant
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SicknessTrendChart(trends: List<SicknessTrend>, modifier: Modifier = Modifier) {
    if (trends.isEmpty()) return

    val maxVal = (trends.maxOfOrNull { it.count } ?: 0).coerceAtLeast(5).toFloat()
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (trends.size - 1).coerceAtLeast(1)
        
        val points = trends.mapIndexed { index, trend ->
            Offset(
                x = index * spacing,
                y = height - (trend.count / maxVal * height)
            )
        }

        // Draw Grid Lines
        val gridCount = 5
        for (i in 0..gridCount) {
            val y = height - (i * height / gridCount)
            drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, y), Offset(width, y), 1.dp.toPx())
        }

        // Draw Path
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }

        drawPath(path, color = Primary, style = Stroke(width = 3.dp.toPx()))

        // Draw Points
        points.forEach { point ->
            drawCircle(Primary, 4.dp.toPx(), point)
            drawCircle(Color.White, 2.dp.toPx(), point)
        }
        
        // Draw Labels (Simple native canvas)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        trends.forEachIndexed { index, trend ->
            val dateLabel = trend.date.takeLast(2) // Just day
            drawContext.canvas.nativeCanvas.drawText(
                dateLabel,
                index * spacing,
                height + 30f,
                paint
            )
        }
    }
}
