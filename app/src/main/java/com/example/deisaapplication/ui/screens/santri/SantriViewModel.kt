package com.example.deisaapplication.ui.screens.santri

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.Santri
import com.example.deisaapplication.data.model.SantriLookups
import com.example.deisaapplication.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SantriListState(
    val santris: List<Santri> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class SantriViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _state = MutableStateFlow(SantriListState())
    val state: StateFlow<SantriListState> = _state

    private val _selectedSantri = MutableStateFlow<Santri?>(null)
    val selectedSantri: StateFlow<Santri?> = _selectedSantri
    
    private val _lookups = MutableStateFlow(SantriLookups())
    val lookups: StateFlow<SantriLookups> = _lookups

    init {
        loadList()
    }

    fun loadList(search: String? = null, gender: String? = null, classId: Int? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val res = api.getSantris(search = search, gender = gender, classId = classId)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(santris = res.body()?.data ?: emptyList(), isLoading = false)
                } else {
                    _state.value = _state.value.copy(error = "Gagal memuat data: ${res.message()}", isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun loadDetail(id: Int) {
        viewModelScope.launch {
            try {
                val res = api.getSantri(id)
                if (res.isSuccessful) {
                    _selectedSantri.value = res.body()?.data
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun clearSelected() {
        _selectedSantri.value = null
    }

    fun loadLookups() {
        viewModelScope.launch {
            try {
                val res = api.getSantriLookups()
                if (res.isSuccessful) {
                    _lookups.value = res.body()?.data ?: SantriLookups()
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun save(id: Int?, data: Map<String, Any?>, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val res = if (id == null) api.createSantri(data) else api.updateSantri(id, data)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(isLoading = false, toast = if (id == null) "Santri berhasil ditambahkan" else "Data santri diperbarui")
                    loadList()
                    onResult(true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, toast = "Gagal menyimpan: ${res.message()}")
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
                val res = api.deleteSantri(id)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(toast = "Santri berhasil dihapus")
                    loadList()
                } else {
                    _state.value = _state.value.copy(toast = "Gagal menghapus santri")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(toast = "Error: ${e.message}")
            }
        }
    }

    fun clearToast() {
        _state.value = _state.value.copy(toast = null)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SantriViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SantriViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
