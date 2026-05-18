package com.example.deisaapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.SicknessCase
import com.example.deisaapplication.ui.theme.*

@Composable
fun SantriCardComponent(
    case: SicknessCase,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (case.status) {
        "recovered", "discharged" -> Color(0xFF10B981) // Green
        "referred" -> Color(0xFFEF4444) // Red
        else -> Color(0xFFF59E0B) // Orange/Warning
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator on the edge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(statusColor)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = case.santri?.name ?: "Tanpa Nama",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OnAppBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Kelas: ${case.santri?.schoolClass ?: "-"}",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sakit: ${case.visitDate ?: "-"}",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                }
            }

            // Status Badge
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = case.statusLabel ?: "Observasi",
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
