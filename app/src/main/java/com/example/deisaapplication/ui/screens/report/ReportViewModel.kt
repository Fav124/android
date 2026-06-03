package com.example.deisaapplication.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.DashboardData
import com.example.deisaapplication.data.model.ReportData
import com.example.deisaapplication.data.model.MedicineReportData
import com.example.deisaapplication.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ReportState(
    val dashboardData: DashboardData? = null,
    val sicknessReport: ReportData? = null,
    val medicineReport: MedicineReportData? = null,
    val selectedReportType: String = "sickness", // "sickness" or "medicine"
    val startDate: String = LocalDate.now().minusDays(30).toString(),
    val endDate: String = LocalDate.now().toString(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class ReportViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun setReportType(type: String) {
        _state.update { it.copy(selectedReportType = type) }
        loadData()
    }

    fun setDateRange(start: String, end: String) {
        _state.update { it.copy(startDate = start, endDate = end) }
        loadData()
    }

    fun loadData() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Always refresh dashboard statistics
                val dashRes = api.getDashboard(currentState.startDate, currentState.endDate)
                
                if (currentState.selectedReportType == "sickness") {
                    val reportRes = api.getSicknessReport(
                        startDate = currentState.startDate,
                        endDate = currentState.endDate
                    )
                    if (dashRes.isSuccessful && reportRes.isSuccessful) {
                        _state.update { it.copy(
                            dashboardData = dashRes.body()?.data,
                            sicknessReport = reportRes.body()?.data,
                            medicineReport = null,
                            isLoading = false
                        ) }
                    } else {
                        _state.update { it.copy(
                            error = "Gagal memuat laporan santri sakit: ${reportRes.message()}",
                            isLoading = false
                        ) }
                    }
                } else {
                    val reportRes = api.getMedicineReport(
                        startDate = currentState.startDate,
                        endDate = currentState.endDate
                    )
                    if (dashRes.isSuccessful && reportRes.isSuccessful) {
                        _state.update { it.copy(
                            dashboardData = dashRes.body()?.data,
                            sicknessReport = null,
                            medicineReport = reportRes.body()?.data,
                            isLoading = false
                        ) }
                    } else {
                        _state.update { it.copy(
                            error = "Gagal memuat laporan obat: ${reportRes.message()}",
                            isLoading = false
                        ) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Terjadi kesalahan koneksi", isLoading = false) }
            }
        }
    }

    fun showToast(message: String) {
        _state.update { it.copy(toast = message) }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReportViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
