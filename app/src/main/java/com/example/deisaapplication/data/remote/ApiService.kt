package com.example.deisaapplication.data.remote

import com.example.deisaapplication.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── Auth ────────────────────────────────────────────────────────────────

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("me")
    suspend fun me(): Response<ApiResponse<User>>

    // ─── Dashboard ───────────────────────────────────────────────────────────

    @GET("dashboard")
    suspend fun getDashboard(
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
    ): Response<ApiResponse<DashboardData>>

    // ─── Kasus Sakit ─────────────────────────────────────────────────────────

    @GET("sickness-cases/lookups")
    suspend fun getSicknessLookups(): Response<ApiResponse<SicknessLookups>>

    @GET("sickness-cases")
    suspend fun getSicknessCases(
        @Query("status")     status: String? = null,
        @Query("search")     search: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
        @Query("page")       page: Int = 1,
        @Query("per_page")   perPage: Int = 15,
    ): Response<PaginatedResponse<SicknessCase>>

    @GET("sickness-cases/{id}")
    suspend fun getSicknessCase(@Path("id") id: Int): Response<ApiResponse<SicknessCase>>

    @POST("sickness-cases")
    suspend fun createSicknessCase(@Body request: SicknessRequest): Response<ApiResponse<SicknessCase>>

    @PUT("sickness-cases/{id}")
    suspend fun updateSicknessCase(@Path("id") id: Int, @Body request: SicknessRequest): Response<ApiResponse<SicknessCase>>

    @DELETE("sickness-cases/{id}")
    suspend fun deleteSicknessCase(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("sickness-cases/{id}/mark-recovered")
    suspend fun markRecovered(@Path("id") id: Int): Response<ApiResponse<Map<String, String>>>

    @POST("sickness-cases/{id}/notify-guardian")
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

    @GET("medicines")
    suspend fun getMedicines(
        @Query("search")        search: String? = null,
        @Query("low_stock")     lowStock: Boolean? = null,
        @Query("expired")       expired: Boolean? = null,
        @Query("expiring_soon") expiringSoon: Boolean? = null,
        @Query("page")          page: Int = 1,
        @Query("per_page")      perPage: Int = 20,
    ): Response<PaginatedResponse<Medicine>>

    @GET("medicines/{id}")
    suspend fun getMedicine(@Path("id") id: Int): Response<ApiResponse<Medicine>>

    @POST("medicines")
    suspend fun createMedicine(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Medicine>>

    @PUT("medicines/{id}")
    suspend fun updateMedicine(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<Medicine>>

    @DELETE("medicines/{id}")
    suspend fun deleteMedicine(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Kasur UKS ───────────────────────────────────────────────────────────

    @GET("beds")
    suspend fun getBeds(@Query("status") status: String? = null): Response<ApiResponse<List<InfirmaryBed>>>

    @POST("beds")
    suspend fun createBed(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<InfirmaryBed>>

    @PUT("beds/{id}")
    suspend fun updateBed(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<InfirmaryBed>>

    @DELETE("beds/{id}")
    suspend fun deleteBed(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Master Data ────────────────────────────────────────────────────────

    @GET("master/classes")
    suspend fun getClasses(): Response<ApiResponse<ItemsResponse<SchoolClassItem>>>

    @POST("master/classes")
    suspend fun createClass(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<SchoolClassItem>>>

    @PUT("master/classes/{id}")
    suspend fun updateClass(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<SchoolClassItem>>>

    @DELETE("master/classes/{id}")
    suspend fun deleteClass(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("master/majors")
    suspend fun getMajors(): Response<ApiResponse<ItemsResponse<MajorItem>>>

    @POST("master/majors")
    suspend fun createMajor(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<MajorItem>>>

    @PUT("master/majors/{id}")
    suspend fun updateMajor(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<MajorItem>>>

    @DELETE("master/majors/{id}")
    suspend fun deleteMajor(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @GET("master/dormitories")
    suspend fun getDormitories(): Response<ApiResponse<ItemsResponse<DormitoryItem>>>

    @POST("master/dormitories")
    suspend fun createDormitory(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<DormitoryItem>>>

    @PUT("master/dormitories/{id}")
    suspend fun updateDormitory(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<ItemResponse<DormitoryItem>>>

    @DELETE("master/dormitories/{id}")
    suspend fun deleteDormitory(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Rujukan RS ──────────────────────────────────────────────────────────

    @GET("referrals")
    suspend fun getReferrals(
        @Query("status")     status: String? = null,
        @Query("search")     search: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
        @Query("page")       page: Int = 1,
        @Query("per_page")   perPage: Int = 15,
    ): Response<PaginatedResponse<HospitalReferral>>

    @GET("referrals/{id}")
    suspend fun getReferral(@Path("id") id: Int): Response<ApiResponse<HospitalReferral>>

    @POST("referrals")
    suspend fun createReferral(@Body request: ReferralRequest): Response<ApiResponse<HospitalReferral>>

    @PUT("referrals/{id}")
    suspend fun updateReferral(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any?>): Response<ApiResponse<HospitalReferral>>

    @DELETE("referrals/{id}")
    suspend fun deleteReferral(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @POST("referrals/{id}/notify-guardian")
    suspend fun notifyReferralGuardian(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ─── Laporan ─────────────────────────────────────────────────────────────

    @GET("reports/summary")
    suspend fun getReportSummary(
        @Query("start_date") startDate: String? = null,
        @Query("end_date")   endDate: String? = null,
    ): Response<ApiResponse<ReportData>>

    // ─── Admin ──────────────────────────────────────────────────────────────

    @GET("admin/overview")
    suspend fun getAdminOverview(): Response<ApiResponse<AdminOverviewData>>

    @GET("admin/users")
    suspend fun getAdminUsers(
        @Query("status") status: String? = null,
        @Query("role") role: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
    ): Response<PaginatedResponse<AdminUser>>

    @POST("admin/users/{id}/approve")
    suspend fun approveUser(@Path("id") id: Int): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("admin/users/{id}/reject")
    suspend fun rejectUser(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any?> = emptyMap(),
    ): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("admin/users/{id}/change-role")
    suspend fun changeUserRole(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any?>,
    ): Response<ApiResponse<ItemResponse<AdminUser>>>

    @POST("admin/users/{id}/quick-reset")
    suspend fun quickResetUser(@Path("id") id: Int): Response<ApiResponse<ItemResponse<AdminUser>>>

    @DELETE("admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
