package com.example.deisaapplication.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.local.SessionManager
import com.example.deisaapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

class AuthViewModel(
    private val repo: AuthRepository,
    val session: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Email dan password tidak boleh kosong.")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            repo.login(email.trim(), password.trim())
                .onSuccess { _uiState.value = LoginUiState(isSuccess = true) }
                .onFailure { _uiState.value = LoginUiState(error = it.message ?: "Login gagal.") }
        }
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(AuthRepository(session), session) as T
        }
    }
}
