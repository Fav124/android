package com.example.deisaapplication.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.DashboardData
import com.example.deisaapplication.data.repository.DashboardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(private val repo: DashboardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { load() }

    fun load(startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            repo.getDashboard(startDate, endDate)
                .onSuccess { resp ->
                    val data = resp.body()?.data
                    if (resp.isSuccessful && data != null)
                        _uiState.value = DashboardUiState.Success(data)
                    else
                        _uiState.value = DashboardUiState.Error("Gagal memuat data dashboard.")
                }
                .onFailure { _uiState.value = DashboardUiState.Error(it.message ?: "Koneksi gagal.") }
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(DashboardRepository()) as T
        }
    }
}
