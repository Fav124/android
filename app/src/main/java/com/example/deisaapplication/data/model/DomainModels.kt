package com.example.deisaapplication.data.model

import com.google.gson.annotations.SerializedName

// ─── Dashboard ─────────────────────────────────────────────────────────────

data class DashboardStats(
    @SerializedName("santri_total")       val santriTotal: Int = 0,
    @SerializedName("santri_l")           val santriL: Int = 0,
    @SerializedName("santri_p")           val santriP: Int = 0,
    @SerializedName("santri_sakit_aktif") val santriSakitAktif: Int = 0,
    @SerializedName("obat_menipis")       val obatMenipis: Int = 0,
    @SerializedName("obat_kadaluarsa")    val obatKadaluarsa: Int = 0,
    @SerializedName("rujukan")            val rujukan: Int = 0,
)

data class DashboardData(
    val stats: DashboardStats,
    @SerializedName("recent_cases")       val recentCases: List<SicknessCase>,
    @SerializedName("low_stock_medicines") val lowStockMedicines: List<Medicine>,
    @SerializedName("sickness_trends")    val sicknessTrends: List<SicknessTrend>,
    @SerializedName("case_distribution")  val caseDistribution: List<CaseDistribution>,
    @SerializedName("class_distribution") val classDistribution: List<ClassDistribution> = emptyList(),
    @SerializedName("major_distribution") val majorDistribution: List<MajorDistribution> = emptyList(),
    @SerializedName("frequent_medicines") val frequentMedicines: List<FrequentMedicine> = emptyList(),
    @SerializedName("alert_medicines")    val alertMedicines: List<AlertMedicine> = emptyList(),
    val filter: DateFilter,
)

data class SicknessTrend(val date: String, val count: Int)
data class CaseDistribution(val status: String, @SerializedName("status_label") val statusLabel: String, val count: Int)
data class ClassDistribution(
    @SerializedName("class_name") val className: String,
    val count: Int
)
data class MajorDistribution(
    @SerializedName("major_name") val majorName: String,
    val count: Int
)
data class FrequentMedicine(
    @SerializedName("medicine_name") val medicineName: String,
    val count: Int
)
data class AlertMedicine(
    val id: Int,
    val name: String,
    val status: String,
    val stock: Int,
    val unit: String?,
    @SerializedName("expiry_date") val expiryDate: String?
)
data class DateFilter(@SerializedName("start_date") val startDate: String, @SerializedName("end_date") val endDate: String)

// ─── Sickness Case ─────────────────────────────────────────────────────────

data class SicknessCase(
    val id: Int = 0,
    val santri: SantriRef? = null,
    val complaint: String? = null,
    val diagnosis: String? = null,
    @SerializedName("action_taken") val actionTaken: String? = null,
    val notes: String? = null,
    val status: String = "observed",
    @SerializedName("status_label") val statusLabel: String? = null,
    @SerializedName("visit_date") val visitDate: String? = null,
    @SerializedName("return_date") val returnDate: String? = null,
    @SerializedName("handled_by") val handledBy: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    val medicines: List<MedicineRef> = emptyList(),
    // Hospital referral fields
    @SerializedName("hospital_name") val hospitalName: String? = null,
    val transport: String? = null,
    @SerializedName("companion_name") val companionName: String? = null,
    // Discharge fields
    @SerializedName("picked_up_by") val pickedUpBy: String? = null,
    @SerializedName("picked_up_at") val pickedUpAt: String? = null,
    @SerializedName("discharge_notes") val dischargeNotes: String? = null,
)

data class SicknessRequest(
    @SerializedName("santri_ids")       val santriIds: List<Int>,
    @SerializedName("visit_date")       val visitDate: String,
    @SerializedName("diagnosa_ids")     val diagnosaIds: List<Int>? = null,
    @SerializedName("keluhan_ids")      val keluhanIds: List<Int>? = null,
    @SerializedName("tindakan_ids")     val tindakanIds: List<Int>? = null,
    val complaint: String? = null,
    val diagnosis: String? = null,
    @SerializedName("action_taken")     val actionTaken: String? = null,
    val notes: String? = null,
    val status: String = "observed",
    val medicines: List<MedicineInput>? = null,
    @SerializedName("photo_base64")     val photoBase64: String? = null,
    @SerializedName("notify_guardian")  val notifyGuardian: Boolean = false,
    // For referred status
    @SerializedName("hospital_name")   val hospitalName: String? = null,
    val transport: String? = null,
    @SerializedName("companion_name")  val companionName: String? = null,
    // For discharge
    @SerializedName("picked_up_by")    val pickedUpBy: String? = null,
    @SerializedName("discharge_guardian_id") val dischargeGuardianId: Int? = null,
    @SerializedName("discharge_notes") val dischargeNotes: String? = null,
)

data class MedicineInput(val id: Int, val quantity: Int)

// ─── Guardian (Wali) ──────────────────────────────────────────────────────

data class Guardian(
    val id: Int = 0,
    val name: String = "",
    val relationship: String = "",
    val phone: String = "",
    val address: String? = null,
    val job: String? = null,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    val notes: String? = null,
)

// ─── Santri ────────────────────────────────────────────────────────────────

data class Santri(
    val id: Int = 0,
    val name: String = "",
    val nis: String? = null,
    val gender: String = "L",
    @SerializedName("gender_label") val genderLabel: String = "Laki-laki",
    @SerializedName("class") val schoolClass: String? = null,
    val major: String? = null,
    // Primary guardian kept for backward compat
    @SerializedName("guardian_name")         val guardianName: String? = null,
    @SerializedName("guardian_relationship") val guardianRelationship: String? = null,
    @SerializedName("guardian_phone")        val guardianPhone: String? = null,
    @SerializedName("guardian_address")      val guardianAddress: String? = null,
    @SerializedName("guardian_job")          val guardianJob: String? = null,
    // Multiple guardians
    val guardians: List<Guardian> = emptyList(),
    @SerializedName("birth_place")           val birthPlace: String? = null,
    @SerializedName("birth_date")     val birthDate: String? = null,
    val notes: String? = null,
    @SerializedName("blood_type")       val bloodType: String? = null,
    val allergies: String? = null,
    @SerializedName("medical_history")  val medicalHistory: String? = null,
    @SerializedName("special_condition") val specialCondition: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    @SerializedName("blood_pressure")   val bloodPressure: String? = null,
    @SerializedName("recent_sickness")  val recentSickness: List<SicknessRef>? = null,
    @SerializedName("recent_referrals") val recentReferrals: List<ReferralRef>? = null,
)

data class SantriRef(val id: Int, val name: String, val nis: String? = null,
    val gender: String? = null,
    @SerializedName("class") val schoolClass: String? = null,
    @SerializedName("guardian_name") val guardianName: String? = null,
    @SerializedName("guardian_phone") val guardianPhone: String? = null,
    val guardians: List<Guardian> = emptyList())

// ─── Medicine ──────────────────────────────────────────────────────────────

data class StockHistoryItem(
    val id: Int = 0,
    val type: String = "",
    val amount: Int = 0,
    @SerializedName("stok_sebelum") val beforeStock: Int = 0,
    @SerializedName("stok_sesudah") val afterStock: Int = 0,
    val date: String = "",
    val notes: String? = null,
)

data class Medicine(
    val id: Int = 0,
    @SerializedName("kode_obat")    val code: String = "",
    val name: String = "",
    val kategori: String = "",
    @SerializedName("bentuk_sediaan") val formulation: String = "",
    val unit: String = "",
    val stock: Int = 0,
    @SerializedName("minimum_stock") val minimumStock: Int = 0,
    @SerializedName("expiry_date") val expiryDate: String? = null,
    @SerializedName("lokasi_penyimpanan") val location: String? = null,
    val description: String? = null,
    val status: String = "aman",
    @SerializedName("riwayat_stok") val stockHistory: List<StockHistoryItem> = emptyList(),
)

data class MedicineRef(val id: Int, val name: String, val unit: String? = null,
    val quantity: Int = 1, val status: String = "pending")

// ─── Hospital Referral ─────────────────────────────────────────────────────

data class HospitalReferral(
    val id: Int = 0,
    val santri: SantriRef? = null,
    @SerializedName("hospital_name") val hospitalName: String? = null,
    @SerializedName("referral_date") val referralDate: String? = null,
    val complaint: String? = null,
    val diagnosis: String? = null,
    val transport: String? = null,
    @SerializedName("companion_name") val companionName: String? = null,
    val status: String = "referred",
    @SerializedName("status_label") val statusLabel: String = "Dirujuk",
    val notes: String? = null,
    @SerializedName("referred_by") val referredBy: String? = null,
)

data class ReferralRequest(
    @SerializedName("santri_id")      val santriId: Int,
    @SerializedName("hospital_name")  val hospitalName: String,
    @SerializedName("referral_date")  val referralDate: String,
    val complaint: String,
    val diagnosis: String? = null,
    val transport: String? = null,
    @SerializedName("companion_name") val companionName: String? = null,
    val status: String = "referred",
    val notes: String? = null,
    @SerializedName("notify_guardian") val notifyGuardian: Boolean = false,
)

// ─── Report ────────────────────────────────────────────────────────────────

data class ReportSummary(
    @SerializedName("total_santri")   val totalSantri: Int = 0,
    @SerializedName("santri_sakit")   val santriSakit: Int = 0,
    @SerializedName("rujukan_rs")     val rujukanRs: Int = 0,
    @SerializedName("obat_menipis")   val obatMenipis: Int = 0,
    @SerializedName("rawat_inap")     val rawatInap: Int = 0,
    @SerializedName("sembuh")         val sembuh: Int = 0,
)

data class ReportData(
    val summary: ReportSummary,
    @SerializedName("top_diagnoses")  val topDiagnoses: List<DiagnosisCount> = emptyList(),
    @SerializedName("sickness_cases") val sicknessCases: List<SicknessCase> = emptyList(),
    val filter: DateFilter,
)

data class DiagnosisCount(val diagnosis: String?, val total: Int)

data class MedicineReportItem(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("kode_obat")    val code: String = "",
    val kategori: String = "",
    val stock: Int = 0,
    @SerializedName("minimum_stock") val minimumStock: Int = 0,
    val unit: String = "",
    val status: String = "aman",
    @SerializedName("expiry_date")  val expiryDate: String? = null,
    @SerializedName("total_out")    val totalOut: Int = 0,
    @SerializedName("total_in")     val totalIn: Int = 0,
)

data class MedicineReportData(
    val medicines: List<MedicineReportItem> = emptyList(),
    val summary: MedicineReportSummary = MedicineReportSummary(),
    val filter: DateFilter = DateFilter("", ""),
)

data class MedicineReportSummary(
    @SerializedName("total_obat")    val totalObat: Int = 0,
    @SerializedName("obat_menipis")  val obatMenipis: Int = 0,
    @SerializedName("obat_kadaluarsa") val obatKadaluarsa: Int = 0,
    @SerializedName("total_mutasi")  val totalMutasi: Int = 0,
)

// ─── Small refs ────────────────────────────────────────────────────────────

data class SicknessRef(val id: Int, val complaint: String, val status: String, @SerializedName("visit_date") val visitDate: String?)
data class ReferralRef(val id: Int, @SerializedName("hospital_name") val hospitalName: String, @SerializedName("referral_date") val referralDate: String?, val status: String)

// ─── Lookups ──────────────────────────────────────────────────────────────

data class LookupItem(val id: Int, val name: String)

data class SantriLookups(
    val classes: List<LookupItem> = emptyList(),
    val majors: List<LookupItem> = emptyList(),
)

data class SicknessLookups(
    val santris: List<SantriRef> = emptyList(),
    val medicines: List<MedicineLookupRef> = emptyList(),
    val diagnoses: List<LookupItem> = emptyList(),
    val keluhans: List<LookupItem> = emptyList(),
    val tindakans: List<LookupItem> = emptyList()
)

data class MedicineLookupRef(val id: Int, val name: String, val unit: String? = null, val stock: Int = 0)

// ─── Master Data ───────────────────────────────────────────────────────────

data class SchoolClassItem(
    val id: Int = 0,
    @SerializedName("nama_kelas") val name: String = "",
    @SerializedName("deskripsi") val description: String? = null,
    @SerializedName("major_ids") val majorIds: List<Int> = emptyList(),
    @SerializedName("major_names") val majorNames: List<String> = emptyList(),
    @SerializedName("santris_list") val santris: List<SantriClassRef> = emptyList(),
)

data class SantriClassRef(
    val id: Int = 0,
    val name: String = "",
    val nis: String? = null,
    val gender: String? = null,
    @SerializedName("gender_label") val genderLabel: String? = null,
    val major: String? = null,
)

data class MajorItem(
    val id: Int = 0,
    @SerializedName("nama_jurusan") val name: String = "",
    @SerializedName("deskripsi") val description: String? = null,
)

data class ItemsResponse<T>(
    val items: List<T> = emptyList(),
)

data class ItemResponse<T>(
    val item: T? = null,
    @SerializedName("new_password") val newPassword: String? = null,
)

// ─── Admin ─────────────────────────────────────────────────────────────────

data class AdminStats(
    @SerializedName("total_users") val totalUsers: Int = 0,
    val pending: Int = 0,
    val approved: Int = 0,
    val rejected: Int = 0,
    val petugas: Int = 0,
    val admin: Int = 0,
)

data class AdminOverviewData(
    val stats: AdminStats = AdminStats(),
)

data class AdminUser(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    @SerializedName("role_label") val roleLabel: String = "",
    val status: String = "",
    @SerializedName("status_label") val statusLabel: String = "",
    @SerializedName("rejection_reason") val rejectionReason: String? = null,
    @SerializedName("approved_at") val approvedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
)
