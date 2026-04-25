package com.example.deisaapplication.ui.screens.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.DashboardData
import com.example.deisaapplication.data.model.ReportData
import com.example.deisaapplication.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportState(
    val dashboardData: DashboardData? = null,
    val reportData: ReportData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ReportViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val dashRes = api.getDashboard()
                val reportRes = api.getReportSummary()
                
                if (dashRes.isSuccessful && reportRes.isSuccessful) {
                    _state.value = _state.value.copy(
                        dashboardData = dashRes.body()?.data,
                        reportData = reportRes.body()?.data,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(error = "Gagal memuat laporan", isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
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
