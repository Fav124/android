package com.example.deisaapplication.ui.screens.referral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.HospitalReferral
import com.example.deisaapplication.data.model.ReferralRequest
import com.example.deisaapplication.data.model.SicknessLookups
import com.example.deisaapplication.data.remote.RetrofitClient
import com.example.deisaapplication.data.repository.HospitalReferralRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ReferralListState(
    val referrals: List<HospitalReferral> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class HospitalReferralViewModel(private val repo: HospitalReferralRepository) : ViewModel() {

    private val _state = MutableStateFlow(ReferralListState())
    val state: StateFlow<ReferralListState> = _state.asStateFlow()

    private val _selectedReferral = MutableStateFlow<HospitalReferral?>(null)
    val selectedReferral: StateFlow<HospitalReferral?> = _selectedReferral.asStateFlow()

    private val _lookups = MutableStateFlow(SicknessLookups()) // Can reuse SicknessLookups for Santris
    val lookups: StateFlow<SicknessLookups> = _lookups.asStateFlow()

    init { loadList() }

    fun loadList(status: String? = null, search: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getList(status = status, search = search)
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null)
                        _state.update { it.copy(referrals = resp.body()!!.data, isLoading = false) }
                    else _state.update { it.copy(isLoading = false, error = "Gagal memuat data rujukan.") }
                }
                .onFailure { _state.update { st -> st.copy(isLoading = false, error = it.message) } }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            repo.getDetail(id)
                .onSuccess { resp -> if (resp.isSuccessful) _selectedReferral.value = resp.body()?.data }
                .onFailure { /* ignore */ }
        }
    }

    fun clearSelected() {
        _selectedReferral.value = null
    }

    fun loadLookups() {
        viewModelScope.launch {
            // We can use the sickness lookups to get santris
            RetrofitClient.apiService.getSicknessLookups()
                .let { if (it.isSuccessful) _lookups.value = it.body()?.data ?: SicknessLookups() }
        }
    }

    fun save(id: Int?, req: ReferralRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val res = if (id == null) repo.create(req) else repo.update(id, mapOf(
                "santri_id" to req.santriId,
                "hospital_name" to req.hospitalName,
                "referral_date" to req.referralDate,
                "complaint" to req.complaint,
                "diagnosis" to req.diagnosis,
                "transport" to req.transport,
                "companion_name" to req.companionName,
                "status" to req.status,
                "notes" to req.notes,
            ))
            res.onSuccess {
                if (it.isSuccessful) {
                    _state.update { st -> st.copy(toast = if (id == null) "Rujukan berhasil disimpan" else "Data diperbarui") }
                    loadList()
                    onResult(true)
                } else {
                    _state.update { st -> st.copy(toast = "Gagal: ${it.message()}") }
                    onResult(false)
                }
            }.onFailure {
                _state.update { st -> st.copy(toast = "Error: ${it.message}") }
                onResult(false)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateStatus(id: Int, status: String) {
        viewModelScope.launch {
            repo.updateStatus(id, status)
                .onSuccess { resp ->
                    if (resp.isSuccessful) {
                        _state.update { it.copy(toast = resp.body()?.message ?: "Status diperbarui.") }
                        loadList()
                        if (_selectedReferral.value?.id == id) loadDetail(id)
                    } else {
                        _state.update { it.copy(toast = "Gagal mengubah status.") }
                    }
                }
                .onFailure { _state.update { st -> st.copy(toast = "Gagal: ${it.message}") } }
        }
    }

    fun notifyGuardian(id: Int) {
        viewModelScope.launch {
            repo.notifyGuardian(id)
                .onSuccess { resp ->
                    _state.update { it.copy(toast = if (resp.isSuccessful == true) "Notifikasi WA terkirim." else "Gagal mengirim notifikasi.") }
                }
                .onFailure { _state.update { st -> st.copy(toast = "Gagal: ${it.message}") } }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            repo.delete(id)
                .onSuccess { loadList(); _state.update { it.copy(toast = "Rujukan berhasil dihapus.") } }
                .onFailure { _state.update { st -> st.copy(toast = "Gagal: ${it.message}") } }
        }
    }

    fun clearToast() = _state.update { it.copy(toast = null) }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HospitalReferralViewModel(HospitalReferralRepository()) as T
        }
    }
}
