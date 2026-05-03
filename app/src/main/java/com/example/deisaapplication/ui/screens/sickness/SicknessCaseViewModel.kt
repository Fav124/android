package com.example.deisaapplication.ui.screens.sickness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.SicknessCase
import com.example.deisaapplication.data.model.SicknessLookups
import com.example.deisaapplication.data.model.SicknessRequest
import com.example.deisaapplication.data.repository.SicknessCaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SicknessCaseListState(
    val cases: List<SicknessCase> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val toast: String? = null,
)

class SicknessCaseViewModel(private val repo: SicknessCaseRepository) : ViewModel() {

    private val _listState = MutableStateFlow(SicknessCaseListState())
    val listState: StateFlow<SicknessCaseListState> = _listState.asStateFlow()

    private val _selectedCase = MutableStateFlow<SicknessCase?>(null)
    val selectedCase: StateFlow<SicknessCase?> = _selectedCase.asStateFlow()

    private val _lookups = MutableStateFlow(SicknessLookups())
    val lookups: StateFlow<SicknessLookups> = _lookups.asStateFlow()

    private val _actionLoading = MutableStateFlow(false)
    val actionLoading: StateFlow<Boolean> = _actionLoading.asStateFlow()

    init { loadList() }

    fun loadList(status: String? = null, search: String? = null, page: Int = 1) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repo.getList(status = status, search = search, page = page)
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null) {
                        val body = resp.body()!!
                        _listState.update { it.copy(
                            cases = if (page == 1) body.data else it.cases + body.data,
                            isLoading = false,
                            currentPage = body.meta?.currentPage ?: 1,
                            lastPage = body.meta?.lastPage ?: 1,
                        ) }
                    } else _listState.update { it.copy(isLoading = false, error = "Gagal memuat data.") }
                }
                .onFailure { _listState.update { st -> st.copy(isLoading = false, error = it.message) } }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            repo.getDetail(id)
                .onSuccess { resp -> if (resp.isSuccessful) _selectedCase.value = resp.body()?.data }
                .onFailure { /* ignore */ }
        }
    }

    fun clearSelected() {
        _selectedCase.value = null
    }

    fun loadLookups() {
        viewModelScope.launch {
            repo.getLookups()
                .onSuccess { resp -> if (resp.isSuccessful) _lookups.value = resp.body()?.data ?: SicknessLookups() }
                .onFailure { /* ignore */ }
        }
    }

    fun save(id: Int?, req: SicknessRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _actionLoading.value = true
            val res = if (id == null) repo.create(req) else repo.update(id, req)
            res.onSuccess {
                if (it.isSuccessful) {
                    _listState.update { st -> st.copy(toast = if (id == null) "Kasus sakit berhasil disimpan" else "Data diperbarui") }
                    loadList()
                    onResult(true)
                } else {
                    _listState.update { st -> st.copy(toast = "Gagal: ${it.message()}") }
                    onResult(false)
                }
            }.onFailure {
                _listState.update { st -> st.copy(toast = "Error: ${it.message}") }
                onResult(false)
            }
            _actionLoading.value = false
        }
    }

    fun markRecovered(id: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            _actionLoading.value = true
            repo.markRecovered(id)
                .onSuccess { loadList(); onDone() }
                .onFailure { _listState.update { st -> st.copy(toast = "Gagal: ${it.message}") } }
            _actionLoading.value = false
        }
    }

    fun notifyGuardian(id: Int) {
        viewModelScope.launch {
            _actionLoading.value = true
            repo.notifyGuardian(id)
                .onSuccess { resp ->
                    val msg = if (resp.isSuccessful == true) "Notifikasi WA berhasil dikirim."
                              else "Gagal mengirim notifikasi."
                    _listState.update { it.copy(toast = msg) }
                }
                .onFailure { _listState.update { st -> st.copy(toast = "Gagal: ${it.message}") } }
            _actionLoading.value = false
        }
    }

    fun clearToast() = _listState.update { it.copy(toast = null) }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SicknessCaseViewModel(SicknessCaseRepository()) as T
        }
    }
}
