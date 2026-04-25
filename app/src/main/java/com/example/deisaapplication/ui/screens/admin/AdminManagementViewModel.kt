package com.example.deisaapplication.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.AdminStats
import com.example.deisaapplication.data.model.AdminUser
import com.example.deisaapplication.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminManagementState(
    val stats: AdminStats = AdminStats(),
    val users: List<AdminUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
    val lastGeneratedPassword: String? = null,
)

class AdminManagementViewModel(
    private val repo: AdminRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AdminManagementState())
    val state: StateFlow<AdminManagementState> = _state.asStateFlow()

    fun load(status: String? = null, role: String? = null, search: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, lastGeneratedPassword = null) }

            repo.getOverview()
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _state.update { it.copy(stats = response.body()?.data?.stats ?: AdminStats()) }
                    }
                }

            repo.getUsers(status = status, role = role, search = search)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(users = response.body()?.data ?: emptyList(), isLoading = false)
                        }
                    } else {
                        _state.update { it.copy(error = "Gagal memuat data user.", isLoading = false) }
                    }
                }
                .onFailure {
                    _state.update { state ->
                        state.copy(error = it.message ?: "Gagal memuat data user.", isLoading = false)
                    }
                }
        }
    }

    fun approve(id: Int, status: String? = null, role: String? = null, search: String? = null, onDone: () -> Unit = {}) = mutate({ repo.approveUser(id) }) {
        load(status = status, role = role, search = search)
        onDone()
    }

    fun reject(id: Int, reason: String?, status: String? = null, role: String? = null, search: String? = null, onDone: () -> Unit = {}) = mutate({ repo.rejectUser(id, reason) }) {
        load(status = status, role = role, search = search)
        onDone()
    }

    fun changeRole(id: Int, role: String, status: String? = null, currentRoleFilter: String? = null, search: String? = null, onDone: () -> Unit = {}) = mutate({ repo.changeRole(id, role) }) {
        load(status = status, role = currentRoleFilter, search = search)
        onDone()
    }

    fun quickReset(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repo.quickReset(id)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                toast = "Password berhasil di-reset.",
                                lastGeneratedPassword = response.body()?.data?.newPassword,
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, toast = "Gagal mereset password.") }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, toast = it.message ?: "Gagal mereset password.") }
                }
        }
    }

    fun delete(id: Int, status: String? = null, role: String? = null, search: String? = null, onDone: () -> Unit = {}) = mutate({ repo.deleteUser(id) }) {
        load(status = status, role = role, search = search)
        onDone()
    }

    fun clearToast() {
        _state.update { it.copy(toast = null, lastGeneratedPassword = null) }
    }

    private fun mutate(action: suspend () -> Result<retrofit2.Response<*>>, onSuccess: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, lastGeneratedPassword = null) }
            action()
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        _state.update { it.copy(isLoading = false, toast = "Perubahan berhasil disimpan.") }
                        onSuccess()
                    } else {
                        _state.update { it.copy(isLoading = false, toast = "Permintaan ditolak: ${response.message()}") }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, toast = it.message ?: "Aksi gagal dijalankan.") }
                }
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AdminManagementViewModel(AdminRepository()) as T
        }
    }
}
