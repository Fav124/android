package com.example.deisaapplication.ui.screens.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.Medicine
import com.example.deisaapplication.data.repository.MedicineRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MedicineListState(
    val medicines: List<Medicine> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class MedicineViewModel(private val repo: MedicineRepository) : ViewModel() {

    private val _state = MutableStateFlow(MedicineListState())
    val state: StateFlow<MedicineListState> = _state.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<Medicine?>(null)
    val selectedMedicine: StateFlow<Medicine?> = _selectedMedicine.asStateFlow()

    init { loadList() }

    fun loadList(search: String? = null, lowStock: Boolean? = null, expired: Boolean? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repo.getList(search = search, lowStock = lowStock, expired = expired)
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null)
                        _state.update { it.copy(medicines = resp.body()!!.data, isLoading = false) }
                    else _state.update { it.copy(isLoading = false, error = "Gagal memuat data obat.") }
                }
                .onFailure { _state.update { st -> st.copy(isLoading = false, error = it.message) } }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            repo.getDetail(id)
                .onSuccess { resp -> if (resp.isSuccessful) _selectedMedicine.value = resp.body()?.data }
                .onFailure { /* ignore */ }
        }
    }

    fun clearSelected() {
        _selectedMedicine.value = null
    }

    fun save(id: Int?, data: Map<String, Any?>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val res = if (id == null) {
                    repo.create(data)
                } else {
                    repo.update(id, data)
                }
                
                if (res.isSuccess && res.getOrNull()?.isSuccessful == true) {
                    _state.value = _state.value.copy(isLoading = false, toast = "Data obat disimpan")
                    loadList()
                    onResult(true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, toast = "Gagal: ${res.exceptionOrNull()?.message ?: res.getOrNull()?.message()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, toast = "Error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            repo.delete(id)
                .onSuccess { loadList(); _state.update { it.copy(toast = "Obat berhasil dihapus.") } }
                .onFailure { _state.update { st -> st.copy(toast = "Gagal menghapus: ${it.message}") } }
        }
    }

    fun clearToast() = _state.update { it.copy(toast = null) }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MedicineViewModel(MedicineRepository()) as T
        }
    }
}
