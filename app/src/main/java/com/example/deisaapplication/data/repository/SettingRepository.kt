package com.example.deisaapplication.data.repository

import com.example.deisaapplication.data.remote.RetrofitClient
import com.example.deisaapplication.data.model.ApiResponse
import retrofit2.Response

class SettingRepository {
    private val api = RetrofitClient.instance

    suspend fun getSettings(): Result<Response<ApiResponse<Map<String, String>>>> {
        return try {
            Result.success(api.getSettings())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSettings(data: Map<String, Any?>): Result<Response<ApiResponse<Unit>>> {
        return try {
            Result.success(api.updateSettings(data))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
