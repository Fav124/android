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
    @SerializedName("kasur_tersedia")     val kasurTersedia: Int = 0,
    @SerializedName("kasur_total")        val kasurTotal: Int = 0,
    @SerializedName("rujukan")            val rujukan: Int = 0,
)

data class DashboardData(
    val stats: DashboardStats,
    @SerializedName("recent_cases")       val recentCases: List<SicknessCase>,
    @SerializedName("low_stock_medicines") val lowStockMedicines: List<Medicine>,
    @SerializedName("sickness_trends")    val sicknessTrends: List<SicknessTrend>,
    @SerializedName("case_distribution")  val caseDistribution: List<CaseDistribution>,
    val filter: DateFilter,
)

data class SicknessTrend(val date: String, val count: Int)
data class CaseDistribution(val status: String, @SerializedName("status_label") val statusLabel: String, val count: Int)
data class DateFilter(@SerializedName("start_date") val startDate: String, @SerializedName("end_date") val endDate: String)

// ─── Sickness Case ─────────────────────────────────────────────────────────

data class SicknessCase(
    val id: Int = 0,
    val santri: SantriRef? = null,
    val complaint: String = "",
    val diagnosis: String? = null,
    @SerializedName("action_taken") val actionTaken: String? = null,
    val notes: String? = null,
    val status: String = "observed",
    @SerializedName("status_label") val statusLabel: String = "Observasi",
    @SerializedName("visit_date") val visitDate: String? = null,
    @SerializedName("return_date") val returnDate: String? = null,
    @SerializedName("handled_by") val handledBy: String? = null,
    val bed: BedRef? = null,
    val medicines: List<MedicineRef> = emptyList(),
)

data class SicknessRequest(
    @SerializedName("santri_id")        val santriId: Int,
    @SerializedName("infirmary_bed_id") val infirmaryBedId: Int? = null,
    @SerializedName("visit_date")       val visitDate: String,
    val complaint: String,
    val diagnosis: String? = null,
    @SerializedName("action_taken")     val actionTaken: String? = null,
    val notes: String? = null,
    val status: String = "observed",
    val medicines: List<MedicineInput>? = null,
    @SerializedName("notify_guardian")  val notifyGuardian: Boolean = false,
)

data class MedicineInput(val id: Int, val quantity: Int)

// ─── Santri ────────────────────────────────────────────────────────────────

data class Santri(
    val id: Int = 0,
    val name: String = "",
    val nis: String? = null,
    val gender: String = "L",
    @SerializedName("gender_label") val genderLabel: String = "Laki-laki",
    @SerializedName("class") val schoolClass: String? = null,
    val major: String? = null,
    val dormitory: String? = null,
    @SerializedName("dorm_room")      val dormRoom: String? = null,
    @SerializedName("guardian_name")  val guardianName: String? = null,
    @SerializedName("guardian_phone") val guardianPhone: String? = null,
    @SerializedName("birth_place")    val birthPlace: String? = null,
    @SerializedName("birth_date")     val birthDate: String? = null,
    val notes: String? = null,
    @SerializedName("recent_sickness")  val recentSickness: List<SicknessRef>? = null,
    @SerializedName("recent_referrals") val recentReferrals: List<ReferralRef>? = null,
)

data class SantriRef(val id: Int, val name: String, val nis: String? = null,
    val gender: String? = null, val dormitory: String? = null,
    @SerializedName("class") val schoolClass: String? = null,
    @SerializedName("guardian_name") val guardianName: String? = null,
    @SerializedName("guardian_phone") val guardianPhone: String? = null)

// ─── Medicine ──────────────────────────────────────────────────────────────

data class Medicine(
    val id: Int = 0,
    val name: String = "",
    val unit: String = "",
    val stock: Int = 0,
    @SerializedName("minimum_stock") val minimumStock: Int = 0,
    @SerializedName("expiry_date")   val expiryDate: String? = null,
    val description: String? = null,
    val status: String = "aman",
)

data class MedicineRef(val id: Int, val name: String, val unit: String? = null,
    val quantity: Int = 1, val status: String = "pending")

// ─── Hospital Referral ─────────────────────────────────────────────────────

data class HospitalReferral(
    val id: Int = 0,
    val santri: SantriRef? = null,
    @SerializedName("hospital_name") val hospitalName: String = "",
    @SerializedName("referral_date") val referralDate: String? = null,
    val complaint: String = "",
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

// ─── Bed ───────────────────────────────────────────────────────────────────

data class InfirmaryBed(
    val id: Int = 0,
    val code: String = "",
    @SerializedName("room_name")     val roomName: String = "UKS",
    val status: String = "available",
    @SerializedName("status_label")  val statusLabel: String = "Tersedia",
    @SerializedName("occupant_name") val occupantName: String? = null,
    val notes: String? = null,
)

data class BedRef(val id: Int, val code: String, val room: String? = null)

// ─── Report ────────────────────────────────────────────────────────────────

data class ReportSummary(
    @SerializedName("total_santri")  val totalSantri: Int = 0,
    @SerializedName("santri_sakit")  val santriSakit: Int = 0,
    @SerializedName("rujukan_rs")    val rujukanRs: Int = 0,
    @SerializedName("obat_menipis")  val obatMenipis: Int = 0,
    @SerializedName("kasur_tersedia") val kasurTersedia: Int = 0,
)

data class ReportData(
    val summary: ReportSummary,
    @SerializedName("top_diagnoses") val topDiagnoses: List<DiagnosisCount>,
    val filter: DateFilter,
)

data class DiagnosisCount(val diagnosis: String?, val total: Int)

// ─── Small refs ────────────────────────────────────────────────────────────

data class SicknessRef(val id: Int, val complaint: String, val status: String, @SerializedName("visit_date") val visitDate: String?)
data class ReferralRef(val id: Int, @SerializedName("hospital_name") val hospitalName: String, @SerializedName("referral_date") val referralDate: String?, val status: String)

// ─── Lookups ──────────────────────────────────────────────────────────────

data class LookupItem(val id: Int, val name: String)

data class SantriLookups(
    val classes: List<LookupItem> = emptyList(),
    val majors: List<LookupItem> = emptyList(),
    val dormitories: List<LookupItem> = emptyList()
)

data class SicknessLookups(
    val santris: List<SantriRef> = emptyList(),
    val beds: List<BedRef> = emptyList(),
    val medicines: List<MedicineLookupRef> = emptyList()
)

data class MedicineLookupRef(val id: Int, val name: String, val unit: String? = null, val stock: Int = 0)

// ─── Master Data ───────────────────────────────────────────────────────────

data class SchoolClassItem(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null,
    @SerializedName("major_ids") val majorIds: List<Int> = emptyList(),
    @SerializedName("major_names") val majorNames: List<String> = emptyList(),
)

data class MajorItem(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null,
)

data class DormitoryItem(
    val id: Int = 0,
    val name: String = "",
    val building: String? = null,
    val gender: String = "L",
    @SerializedName("supervisor_name") val supervisorName: String? = null,
    val description: String? = null,
    @SerializedName("santri_count") val santriCount: Int = 0,
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
