package com.example.deisaapplication.ui.screens.report

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.deisaapplication.data.model.ReportData
import com.example.deisaapplication.data.model.MedicineReportData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object PdfReportGenerator {

    fun generateSicknessReportPdf(
        context: Context,
        reportData: ReportData,
        startDate: String,
        endDate: String
    ): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isAntiAlias = true
        }

        // --- DRAW HEADER ---
        paint.color = Color.rgb(26, 115, 232) // Primary Blue
        canvas.drawRect(0f, 0f, 595f, 15f, paint)

        // Title
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        canvas.drawText("DEIHealth – Laporan Santri Sakit", 40f, 50f, paint)

        // Subtitle & Dates
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("Pondok Pesantren Daar El-Ilmi", 40f, 68f, paint)
        canvas.drawText("Periode: $startDate s/d $endDate", 40f, 84f, paint)
        canvas.drawText("Waktu Unduh: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}", 40f, 100f, paint)

        // Divider Line
        paint.color = Color.LTGRAY
        canvas.drawLine(40f, 115f, 555f, 115f, paint)

        // --- DRAW SUMMARY ---
        paint.color = Color.rgb(240, 244, 255)
        canvas.drawRoundRect(40f, 130f, 555f, 190f, 8f, 8f, paint)

        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        canvas.drawText("RINGKASAN LAPORAN", 55f, 150f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        val summary = reportData.summary
        canvas.drawText("Total Terdata Sakit : ${summary.santriSakit}", 55f, 175f, paint)
        canvas.drawText("Sembuh : ${summary.sembuh}", 220f, 175f, paint)
        canvas.drawText("Dirujuk RS : ${summary.rujukanRs}", 380f, 175f, paint)

        // --- DRAW CASES TABLE ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        paint.color = Color.BLACK
        canvas.drawText("DAFTAR KASUS SAKIT SANTRI", 40f, 220f, paint)

        // Table Header
        var startY = 240f
        paint.color = Color.rgb(26, 115, 232)
        canvas.drawRect(40f, startY, 555f, startY + 24f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("No", 45f, startY + 16f, paint)
        canvas.drawText("Nama Santri", 70f, startY + 16f, paint)
        canvas.drawText("Keluhan", 200f, startY + 16f, paint)
        canvas.drawText("Diagnosa", 320f, startY + 16f, paint)
        canvas.drawText("Status", 440f, startY + 16f, paint)
        canvas.drawText("Tgl Masuk", 500f, startY + 16f, paint)

        // Table Body
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var currentY = startY + 24f
        val cases = reportData.sicknessCases

        if (cases.isEmpty()) {
            paint.color = Color.DKGRAY
            canvas.drawText("Tidak ada data dalam periode ini.", 210f, currentY + 30f, paint)
        } else {
            cases.take(15).forEachIndexed { idx, item ->
                // Alternating row background
                if (idx % 2 == 1) {
                    paint.color = Color.rgb(245, 247, 250)
                    canvas.drawRect(40f, currentY, 555f, currentY + 24f, paint)
                }
                
                paint.color = Color.BLACK
                canvas.drawText((idx + 1).toString(), 45f, currentY + 16f, paint)
                canvas.drawText(item.santri?.name?.take(22) ?: "-", 70f, currentY + 16f, paint)
                canvas.drawText(item.complaint?.take(20) ?: "-", 200f, currentY + 16f, paint)
                canvas.drawText(item.diagnosis?.take(20) ?: "-", 320f, currentY + 16f, paint)
                
                val statusLabel = when (item.status) {
                    "observed" -> "Observasi"
                    "rawat_inap" -> "Rawat Inap"
                    "referred" -> "Dirujuk"
                    "recovered" -> "Sembuh"
                    else -> item.status
                }
                canvas.drawText(statusLabel, 440f, currentY + 16f, paint)
                canvas.drawText(item.visitDate?.take(10) ?: "-", 500f, currentY + 16f, paint)
                
                currentY += 24f
            }
        }

        // Footer signature block
        paint.color = Color.DKGRAY
        paint.textSize = 9f
        canvas.drawText("Petugas UKS Daar El-Ilmi", 400f, 750f, paint)
        canvas.drawText("(_______________________)", 400f, 800f, paint)

        pdfDocument.finishPage(page)
        val resultUri = savePdfToDownloads(context, pdfDocument, "Laporan_Sakit_${startDate}_to_${endDate}.pdf")
        pdfDocument.close()
        return resultUri
    }

    fun generateMedicineReportPdf(
        context: Context,
        reportData: MedicineReportData,
        startDate: String,
        endDate: String
    ): Uri? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()

        // --- DRAW HEADER ---
        paint.color = Color.rgb(242, 133, 0) // Secondary Orange
        canvas.drawRect(0f, 0f, 595f, 15f, paint)

        // Title
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        canvas.drawText("DEIHealth – Laporan Inventori Obat", 40f, 50f, paint)

        // Subtitle & Dates
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("Pondok Pesantren Daar El-Ilmi", 40f, 68f, paint)
        canvas.drawText("Periode: $startDate s/d $endDate", 40f, 84f, paint)
        canvas.drawText("Waktu Unduh: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}", 40f, 100f, paint)

        // Divider Line
        paint.color = Color.LTGRAY
        canvas.drawLine(40f, 115f, 555f, 115f, paint)

        // --- DRAW SUMMARY ---
        paint.color = Color.rgb(255, 248, 240)
        canvas.drawRoundRect(40f, 130f, 555f, 190f, 8f, 8f, paint)

        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        canvas.drawText("RINGKASAN MUTASI & STOK OBAT", 55f, 150f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        val summary = reportData.summary
        canvas.drawText("Total Varian Obat : ${summary.totalObat}", 55f, 175f, paint)
        canvas.drawText("Stok Menipis : ${summary.obatMenipis}", 220f, 175f, paint)
        canvas.drawText("Mutasi Terjadi : ${summary.totalMutasi}", 380f, 175f, paint)

        // --- DRAW MEDICINES TABLE ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        paint.color = Color.BLACK
        canvas.drawText("DAFTAR DETAIL INVENTORI OBAT", 40f, 220f, paint)

        // Table Header
        var startY = 240f
        paint.color = Color.rgb(242, 133, 0)
        canvas.drawRect(40f, startY, 555f, startY + 24f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("No", 45f, startY + 16f, paint)
        canvas.drawText("Kode", 70f, startY + 16f, paint)
        canvas.drawText("Nama Obat", 130f, startY + 16f, paint)
        canvas.drawText("Kategori", 250f, startY + 16f, paint)
        canvas.drawText("Stok", 350f, startY + 16f, paint)
        canvas.drawText("Masuk (+)", 410f, startY + 16f, paint)
        canvas.drawText("Keluar (-)", 470f, startY + 16f, paint)
        canvas.drawText("Status", 520f, startY + 16f, paint)

        // Table Body
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var currentY = startY + 24f
        val meds = reportData.medicines

        if (meds.isEmpty()) {
            paint.color = Color.DKGRAY
            canvas.drawText("Tidak ada data dalam periode ini.", 210f, currentY + 30f, paint)
        } else {
            meds.take(15).forEachIndexed { idx, item ->
                if (idx % 2 == 1) {
                    paint.color = Color.rgb(255, 252, 248)
                    canvas.drawRect(40f, currentY, 555f, currentY + 24f, paint)
                }

                paint.color = Color.BLACK
                canvas.drawText((idx + 1).toString(), 45f, currentY + 16f, paint)
                canvas.drawText(item.code, 70f, currentY + 16f, paint)
                canvas.drawText(item.name.take(20), 130f, currentY + 16f, paint)
                canvas.drawText(item.kategori.take(18), 250f, currentY + 16f, paint)
                canvas.drawText("${item.stock} ${item.unit}", 350f, currentY + 16f, paint)
                canvas.drawText(item.totalIn.toString(), 410f, currentY + 16f, paint)
                canvas.drawText(item.totalOut.toString(), 470f, currentY + 16f, paint)
                canvas.drawText(item.status.uppercase(), 520f, currentY + 16f, paint)

                currentY += 24f
            }
        }

        // Footer signature block
        paint.color = Color.DKGRAY
        paint.textSize = 9f
        canvas.drawText("Penanggung Jawab Inventori UKS", 380f, 750f, paint)
        canvas.drawText("(_______________________)", 380f, 800f, paint)

        pdfDocument.finishPage(page)
        val resultUri = savePdfToDownloads(context, pdfDocument, "Laporan_Obat_${startDate}_to_${endDate}.pdf")
        pdfDocument.close()
        return resultUri
    }

    private fun savePdfToDownloads(context: Context, pdfDocument: PdfDocument, filename: String): Uri? {
        val resolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/DEIHealth")
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                try {
                    resolver.openOutputStream(uri)?.use { outStream ->
                        pdfDocument.writeTo(outStream)
                    }
                    return uri
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val deiDir = File(downloadDir, "DEIHealth")
            if (!deiDir.exists()) deiDir.mkdirs()
            val file = File(deiDir, filename)
            try {
                FileOutputStream(file).use { outStream ->
                    pdfDocument.writeTo(outStream)
                }
                return Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
