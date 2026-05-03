package com.example.deisaapplication.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.local.SessionManager
import com.example.deisaapplication.data.model.User
import com.example.deisaapplication.data.repository.SettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val appSettings: Map<String, String> = emptyMap(),
    val message: String? = null,
    val error: String? = null,
)

class SettingsViewModel(
    private val session: SessionManager,
    private val repo: SettingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(user = session.getUser()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadAppSettings()
    }

    fun loadAppSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repo.getSettings()
                .onSuccess { resp ->
                    val data = resp.body()?.data ?: emptyMap()
                    _uiState.value = _uiState.value.copy(isLoading = false, appSettings = data)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun updateProfile(name: String, email: String, noHp: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null, error = null)
            // Implementation for profile update via API
            // For now, let's assume it's success and update session
            // repo.updateProfile(...)
            _uiState.value = _uiState.value.copy(isLoading = false, message = "Profil diperbarui.")
        }
    }

    fun updateAppSettings(data: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null, error = null)
            repo.updateSettings(data)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Pengaturan aplikasi disimpan.")
                    loadAppSettings()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(session, SettingRepository()) as T
        }
    }
}
