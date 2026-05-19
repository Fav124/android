package com.example.deisaapplication.data.repository

import com.example.deisaapplication.data.local.SessionManager
import com.example.deisaapplication.data.model.*
import com.example.deisaapplication.data.remote.ApiService
import com.example.deisaapplication.data.remote.RetrofitClient

class AuthRepository(private val session: SessionManager) {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun login(email: String, password: String): Result<LoginData> = runCatching {
        val resp = api.login(LoginRequest(email, password))
        if (resp.isSuccessful) {
            val data = resp.body()?.data ?: error("Data kosong dari server.")
            session.saveToken(data.token)
            session.saveUser(data.user)
            RetrofitClient.init(session.getPrefs())
            data
        } else {
            error(parseError(resp.errorBody()?.string()))
        }
    }

    suspend fun register(request: RegisterRequest): Result<User> = runCatching {
        val resp = api.register(request)
        if (resp.isSuccessful) {
            resp.body()?.data ?: error("Data kosong dari server.")
        } else {
            error(parseError(resp.errorBody()?.string()))
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        api.logout()
        session.clear()
    }

    private fun parseError(body: String?): String {
        if (body == null) return "Terjadi kesalahan."
        return try {
            val obj = com.google.gson.JsonParser.parseString(body).asJsonObject
            obj["message"]?.asString ?: "Terjadi kesalahan."
        } catch (e: Exception) { "Terjadi kesalahan." }
    }
}

class SicknessCaseRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getList(status: String? = null, search: String? = null, page: Int = 1) =
        runCatching { api.getSicknessCases(status = status, search = search, page = page) }

    suspend fun getLookups() = runCatching { api.getSicknessLookups() }

    suspend fun getDetail(id: Int) = runCatching { api.getSicknessCase(id) }

    suspend fun create(req: SicknessRequest) = runCatching { api.createSicknessCase(req) }

    suspend fun update(id: Int, req: SicknessRequest) = runCatching { api.updateSicknessCase(id, req) }

    suspend fun delete(id: Int) = runCatching { api.deleteSicknessCase(id) }

    suspend fun markRecovered(id: Int) = runCatching { api.markRecovered(id) }

    suspend fun notifyGuardian(id: Int) = runCatching { api.notifySicknessGuardian(id) }

    suspend fun discharge(id: Int, body: Map<String, Any?>) = runCatching { api.dischargeSicknessCase(id, body) }

    suspend fun referToHospital(id: Int, body: Map<String, Any?>) = runCatching { api.referSicknessCase(id, body) }

    suspend fun assignBed(id: Int, bedId: Int) = runCatching { api.assignBed(id, mapOf("infirmary_bed_id" to bedId)) }
}

class MedicineRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getList(search: String? = null, lowStock: Boolean? = null, expired: Boolean? = null, page: Int = 1) =
        runCatching { api.getMedicines(search = search, lowStock = lowStock, expired = expired, page = page) }

    suspend fun getDetail(id: Int) = runCatching { api.getMedicine(id) }

    suspend fun create(body: Map<String, Any?>) = runCatching { api.createMedicine(body) }

    suspend fun update(id: Int, body: Map<String, Any?>) = runCatching { api.updateMedicine(id, body) }

    suspend fun delete(id: Int) = runCatching { api.deleteMedicine(id) }

    suspend fun recordMutation(body: Map<String, Any?>) = runCatching { api.recordStockMutation(body) }
}

class HospitalReferralRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getList(status: String? = null, search: String? = null, page: Int = 1) =
        runCatching { api.getReferrals(status = status, search = search, page = page) }

    suspend fun getDetail(id: Int) = runCatching { api.getReferral(id) }

    suspend fun create(req: ReferralRequest) = runCatching { api.createReferral(req) }

    suspend fun update(id: Int, body: Map<String, Any?>) = runCatching { api.updateReferral(id, body) }

    suspend fun delete(id: Int) = runCatching { api.deleteReferral(id) }

    suspend fun notifyGuardian(id: Int) = runCatching { api.notifyReferralGuardian(id) }
}

class SantriRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getList(search: String? = null, page: Int = 1) =
        runCatching { api.getSantris(search = search, page = page) }

    suspend fun getLookups() = runCatching { api.getSantriLookups() }

    suspend fun getDetail(id: Int) = runCatching { api.getSantri(id) }

    suspend fun create(body: Map<String, Any?>) = runCatching { api.createSantri(body) }

    suspend fun update(id: Int, body: Map<String, Any?>) = runCatching { api.updateSantri(id, body) }

    suspend fun delete(id: Int) = runCatching { api.deleteSantri(id) }

    // Guardian CRUD
    suspend fun getGuardians(santriId: Int) = runCatching { api.getSantriGuardians(santriId) }

    suspend fun addGuardian(santriId: Int, body: Map<String, Any?>) = runCatching { api.addSantriGuardian(santriId, body) }

    suspend fun updateGuardian(santriId: Int, guardianId: Int, body: Map<String, Any?>) =
        runCatching { api.updateSantriGuardian(santriId, guardianId, body) }

    suspend fun deleteGuardian(santriId: Int, guardianId: Int) = runCatching { api.deleteSantriGuardian(santriId, guardianId) }

    suspend fun notifyGuardian(santriId: Int, guardianId: Int) = runCatching { api.notifyGuardianDirect(santriId, guardianId) }
}

class DashboardRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getDashboard(startDate: String? = null, endDate: String? = null) =
        runCatching { api.getDashboard(startDate, endDate) }

    suspend fun getReport(startDate: String? = null, endDate: String? = null) =
        runCatching { api.getReportSummary(startDate, endDate) }
}

class MasterDataRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getClasses() = runCatching { api.getClasses() }
    suspend fun createClass(body: Map<String, Any?>) = runCatching { api.createClass(body) }
    suspend fun updateClass(id: Int, body: Map<String, Any?>) = runCatching { api.updateClass(id, body) }
    suspend fun deleteClass(id: Int) = runCatching { api.deleteClass(id) }

    suspend fun getMajors() = runCatching { api.getMajors() }
    suspend fun createMajor(body: Map<String, Any?>) = runCatching { api.createMajor(body) }
    suspend fun updateMajor(id: Int, body: Map<String, Any?>) = runCatching { api.updateMajor(id, body) }
    suspend fun deleteMajor(id: Int) = runCatching { api.deleteMajor(id) }

    suspend fun getDormitories() = runCatching { api.getDormitories() }
    suspend fun createDormitory(body: Map<String, Any?>) = runCatching { api.createDormitory(body) }
    suspend fun updateDormitory(id: Int, body: Map<String, Any?>) = runCatching { api.updateDormitory(id, body) }
    suspend fun deleteDormitory(id: Int) = runCatching { api.deleteDormitory(id) }
}

class AdminRepository {
    private val api: ApiService get() = RetrofitClient.apiService

    suspend fun getOverview() = runCatching { api.getAdminOverview() }
    suspend fun getUsers(status: String? = null, role: String? = null, search: String? = null) =
        runCatching { api.getAdminUsers(status = status, role = role, search = search) }

    suspend fun approveUser(id: Int) = runCatching { api.approveUser(id) }
    suspend fun rejectUser(id: Int, reason: String?) = runCatching {
        api.rejectUser(id, if (reason.isNullOrBlank()) emptyMap() else mapOf("rejection_reason" to reason))
    }
    suspend fun changeRole(id: Int, role: String) = runCatching { api.changeUserRole(id, mapOf("role" to role)) }
    suspend fun quickReset(id: Int) = runCatching { api.quickResetUser(id) }
    suspend fun deleteUser(id: Int) = runCatching { api.deleteUser(id) }
}
