package com.example.deisaapplication.ui.screens.bed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.InfirmaryBed
import com.example.deisaapplication.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BedListState(
    val beds: List<InfirmaryBed> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class InfirmaryBedViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _state = MutableStateFlow(BedListState())
    val state: StateFlow<BedListState> = _state

    init {
        loadList()
    }

    fun loadList(status: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val res = api.getBeds(status = status)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(beds = res.body()?.data ?: emptyList(), isLoading = false)
                } else {
                    _state.value = _state.value.copy(error = "Gagal memuat data kasur", isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun clearToast() {
        _state.value = _state.value.copy(toast = null)
    }

    fun save(id: Int?, data: Map<String, Any?>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Backend lacks POST /beds in ApiService.kt currently, I should add it if needed
                // But for now let's assume update is priority.
                val res = if (id == null) {
                    api.createBed(data)
                } else {
                    api.updateBed(id, data)
                }
                
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(isLoading = false, toast = if (id == null) "Kasur berhasil ditambah" else "Data kasur diperbarui")
                    loadList()
                    onResult(true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, toast = "Gagal: ${res.message()}")
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
            try {
                val res = api.deleteBed(id)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(toast = "Kasur berhasil dihapus")
                    loadList()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(toast = "Gagal menghapus: ${e.message}")
            }
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InfirmaryBedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InfirmaryBedViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
