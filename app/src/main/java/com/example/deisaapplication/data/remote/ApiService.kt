package com.example.deisaapplication.data.remote

import com.example.deisaapplication.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── Auth ────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun me(): Response<ApiResponse<User>>

    // ─── Dashboard ───────────────────────────────────────────────────────────

    @GET("dashboard/summary")
    suspend fun getDashboard(
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
    ): Response<ApiResponse<DashboardData>>

    // ─── Kasus Sakit (Kunjungan) ─────────────────────────────────────────────

    @GET("kunjungan-form-data")
    suspend fun getSicknessLookups(): Response<ApiResponse<SicknessLookups>>

    @GET("kunjungan")
    suspend fun getSicknessCases(
        @Query("status")     status: String? = null,
        @Query("search")     search: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
        @Query("page")       page: Int = 1,
        @Query("per_page")   perPage: Int = 15,
    ): Response<PaginatedResponse<SicknessCase>>

    @GET("kunjungan/{id}")
    suspend fun getSicknessCase(@Path("id") id: Int): Response<ApiResponse<SicknessCase>>

    @POST("kunjungan")
    suspend fun createSicknessCase(@Body request: SicknessRequest): Response<ApiResponse<SicknessCase>>

    @PUT("kunjungan/{id}")
    suspend fun updateSicknessCase(@Path("id") id: Int, @Body request: SicknessRequest): Response<ApiResponse<SicknessCase>>

    @DELETE("kunjungan/{id}")
    suspend fun deleteSicknessCase(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("monitoring/{id}/selesai")
    suspend fun markRecovered(@Path("id") id: Int): Response<ApiResponse<Map<String, String>>>

    @POST("kunjungan/{id}/notify-guardian")
    suspend fun notifySicknessGuardian(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Santri ──────────────────────────────────────────────────────────────

    @GET("santri/lookups")
    suspend fun getSantriLookups(): Response<ApiResponse<SantriLookups>>

    @GET("santri")
    suspend fun getSantris(
        @Query("search")       search: String? = null,
        @Query("gender")       gender: String? = null,
        @Query("class_id")     classId: Int? = null,
        @Query("dormitory_id") dormitoryId: Int? = null,
        @Query("page")         page: Int = 1,
        @Query("per_page")     perPage: Int = 20,
    ): Response<PaginatedResponse<Santri>>

    @GET("santri/{id}")
    suspend fun getSantri(@Path("id") id: Int): Response<ApiResponse<Santri>>

    @POST("santri")
    suspend fun createSantri(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Santri>>

    @PUT("santri/{id}")
    suspend fun updateSantri(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Santri>>

    @DELETE("santri/{id}")
    suspend fun deleteSantri(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Obat ────────────────────────────────────────────────────────────────

    @GET("obat")
    suspend fun getMedicines(
        @Query("search")        search: String? = null,
        @Query("low_stock")     lowStock: Boolean? = null,
        @Query("expired")       expired: Boolean? = null,
        @Query("expiring_soon") expiringSoon: Boolean? = null,
        @Query("page")          page: Int = 1,
        @Query("per_page")      perPage: Int = 20,
    ): Response<PaginatedResponse<Medicine>>

    @GET("obat/{id}")
    suspend fun getMedicine(@Path("id") id: Int): Response<ApiResponse<Medicine>>

    @POST("obat")
    suspend fun createMedicine(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Medicine>>

    @PUT("obat/{id}")
    suspend fun updateMedicine(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Medicine>>

    @DELETE("obat/{id}")
    suspend fun deleteMedicine(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Kasur UKS ───────────────────────────────────────────────────────────

    @GET("rawat-inap")
    suspend fun getBeds(@Query("status") status: String? = null): Response<ApiResponse<List<InfirmaryBed>>>

    @POST("rawat-inap")
    suspend fun createBed(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<InfirmaryBed>>

    @PUT("rawat-inap/{id}")
    suspend fun updateBed(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<InfirmaryBed>>

    @DELETE("rawat-inap/{id}")
    suspend fun deleteBed(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Master Data ────────────────────────────────────────────────────────

    @GET("master/kelas")
    suspend fun getClasses(): Response<ApiResponse<ItemsResponse<SchoolClassItem>>>

    @POST("master/kelas")
    suspend fun createClass(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<SchoolClassItem>>>

    @PUT("master/kelas/{id}")
    suspend fun updateClass(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<SchoolClassItem>>>

    @DELETE("master/kelas/{id}")
    suspend fun deleteClass(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("master/jurusan")
    suspend fun getMajors(): Response<ApiResponse<ItemsResponse<MajorItem>>>

    @POST("master/jurusan")
    suspend fun createMajor(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<MajorItem>>>

    @PUT("master/jurusan/{id}")
    suspend fun updateMajor(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<MajorItem>>>

    @DELETE("master/jurusan/{id}")
    suspend fun deleteMajor(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("master/kamar")
    suspend fun getDormitories(): Response<ApiResponse<ItemsResponse<DormitoryItem>>>

    @POST("master/kamar")
    suspend fun createDormitory(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<DormitoryItem>>>

    @PUT("master/kamar/{id}")
    suspend fun updateDormitory(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<DormitoryItem>>>

    @DELETE("master/kamar/{id}")
    suspend fun deleteDormitory(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Rujukan RS ──────────────────────────────────────────────────────────

    @GET("rujukan")
    suspend fun getReferrals(
        @Query("status")     status: String? = null,
        @Query("search")     search: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
        @Query("page")       page: Int = 1,
        @Query("per_page")   perPage: Int = 15,
    ): Response<PaginatedResponse<HospitalReferral>>

    @GET("rujukan/{id}")
    suspend fun getReferral(@Path("id") id: Int): Response<ApiResponse<HospitalReferral>>

    @POST("rujukan")
    suspend fun createReferral(@Body request: ReferralRequest): Response<ApiResponse<HospitalReferral>>

    @PUT("rujukan/{id}")
    suspend fun updateReferral(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<HospitalReferral>>

    @DELETE("rujukan/{id}")
    suspend fun deleteReferral(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("rujukan/{id}/notify-guardian")
    suspend fun notifyReferralGuardian(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Laporan ─────────────────────────────────────────────────────────────

    @GET("reports/daily-summary")
    suspend fun getReportSummary(
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
    ): Response<ApiResponse<ReportData>>

    // ─── Admin ──────────────────────────────────────────────────────────────

    @GET("approvals")
    suspend fun getAdminOverview(): Response<ApiResponse<AdminOverviewData>>

    @GET("approvals")
    suspend fun getAdminUsers(
        @Query("status") status: String? = null,
        @Query("role") role: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
    ): Response<PaginatedResponse<AdminUser>>

    @POST("approvals/{id}/approve")
    suspend fun approveUser(@Path("id") id: Int): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("approvals/{id}/reject")
    suspend fun rejectUser(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any?> = emptyMap(),
    ): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("auth/change-role")
    suspend fun changeUserRole(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any?>,
    ): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("auth/quick-reset")
    suspend fun quickResetUser(@Path("id") id: Int): Response<ApiResponse<ItemResponse<AdminUser>>>

    @DELETE("auth/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Settings ───────────────────────────────────────────────────────────

    @GET("settings")
    suspend fun getSettings(): Response<ApiResponse<Map<String, String>>>

    @POST("settings")
    suspend fun updateSettings(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Unit>>
}
