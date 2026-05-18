package com.example.deisaapplication.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.RegisterRequest
import com.example.deisaapplication.data.local.SessionManager
import com.example.deisaapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val message: String? = null,
)

class AuthViewModel(
    private val repo: AuthRepository,
    val session: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Email dan password tidak boleh kosong.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repo.login(email.trim(), password.trim())
                .onSuccess { _uiState.value = AuthUiState(isSuccess = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Login gagal.") }
        }
    }

    fun register(name: String, email: String, phone: String, pass: String, passConfirm: String, role: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState(error = "Semua field harus diisi.")
            return
        }
        if (pass != passConfirm) {
            _uiState.value = AuthUiState(error = "Konfirmasi password tidak cocok.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repo.register(RegisterRequest(name, email, phone, pass, passConfirm, role))
                .onSuccess { 
                    _uiState.value = AuthUiState(
                        isSuccess = true, 
                        message = "Pendaftaran berhasil! Tunggu persetujuan admin."
                    ) 
                }
                .onFailure { _uiState.value = AuthUiState(error = it.message ?: "Pendaftaran gagal.") }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(session), session) as T
        }
    }
}
