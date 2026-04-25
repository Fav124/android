package com.example.deisaapplication.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("role_label")
    val roleLabel: String,
    val status: String,
) {
    fun isSuperAdmin() = role == "super_admin"
    fun isAdmin()      = role == "admin" || role == "super_admin"
    fun canManageData() = role in listOf("super_admin", "admin")
    fun canAccessHealth() = role in listOf("super_admin", "admin", "petugas_kesehatan")
}

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?,
)

data class LoginData(
    val token: String,
    @SerializedName("token_type")
    val tokenType: String,
    val user: User,
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
)

data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val meta: PaginationMeta?,
)

data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("last_page")
    val lastPage: Int,
    val total: Int,
)
