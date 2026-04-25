package com.example.deisaapplication.ui.screens.master

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deisaapplication.data.model.DormitoryItem
import com.example.deisaapplication.data.model.MajorItem
import com.example.deisaapplication.data.model.SchoolClassItem
import com.example.deisaapplication.data.repository.MasterDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

enum class MasterSection(val title: String) {
    CLASS("Data Kelas"),
    MAJOR("Data Jurusan"),
    DORMITORY("Data Asrama"),
}

data class MasterDataState(
    val classes: List<SchoolClassItem> = emptyList(),
    val majors: List<MajorItem> = emptyList(),
    val dormitories: List<DormitoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toast: String? = null,
)

class MasterDataViewModel(
    private val repo: MasterDataRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(MasterDataState())
    val state: StateFlow<MasterDataState> = _state.asStateFlow()

    fun load(section: MasterSection) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (section) {
                MasterSection.CLASS -> loadClasses()
                MasterSection.MAJOR -> loadMajors()
                MasterSection.DORMITORY -> loadDormitories()
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun loadReferenceData() {
        viewModelScope.launch {
            loadMajors()
        }
    }

    fun saveClass(id: Int?, body: Map<String, Any?>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (id == null) repo.createClass(body) else repo.updateClass(id, body)
            handleMutation(result, "Data kelas berhasil disimpan.") {
                loadClasses()
                onDone(true)
            } onError@{
                onDone(false)
            }
        }
    }

    fun deleteClass(id: Int) {
        viewModelScope.launch {
            handleMutation(repo.deleteClass(id), "Data kelas berhasil dihapus.") { loadClasses() }
        }
    }

    fun saveMajor(id: Int?, body: Map<String, Any?>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (id == null) repo.createMajor(body) else repo.updateMajor(id, body)
            handleMutation(result, "Data jurusan berhasil disimpan.") {
                loadMajors()
                onDone(true)
            } onError@{
                onDone(false)
            }
        }
    }

    fun deleteMajor(id: Int) {
        viewModelScope.launch {
            handleMutation(repo.deleteMajor(id), "Data jurusan berhasil dihapus.") { loadMajors() }
        }
    }

    fun saveDormitory(id: Int?, body: Map<String, Any?>, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (id == null) repo.createDormitory(body) else repo.updateDormitory(id, body)
            handleMutation(result, "Data asrama berhasil disimpan.") {
                loadDormitories()
                onDone(true)
            } onError@{
                onDone(false)
            }
        }
    }

    fun deleteDormitory(id: Int) {
        viewModelScope.launch {
            handleMutation(repo.deleteDormitory(id), "Data asrama berhasil dihapus.") { loadDormitories() }
        }
    }

    fun clearToast() {
        _state.update { it.copy(toast = null) }
    }

    private suspend fun loadClasses() {
        repo.getClasses()
            .onSuccess { response ->
                if (response.isSuccessful) {
                    _state.update {
                        it.copy(classes = response.body()?.data?.items ?: emptyList(), error = null)
                    }
                } else {
                    _state.update { it.copy(error = "Gagal memuat data kelas.") }
                }
            }
            .onFailure { _state.update { state -> state.copy(error = it.message ?: "Gagal memuat data kelas.") } }
    }

    private suspend fun loadMajors() {
        repo.getMajors()
            .onSuccess { response ->
                if (response.isSuccessful) {
                    _state.update {
                        it.copy(majors = response.body()?.data?.items ?: emptyList(), error = null)
                    }
                } else {
                    _state.update { it.copy(error = "Gagal memuat data jurusan.") }
                }
            }
            .onFailure { _state.update { state -> state.copy(error = it.message ?: "Gagal memuat data jurusan.") } }
    }

    private suspend fun loadDormitories() {
        repo.getDormitories()
            .onSuccess { response ->
                if (response.isSuccessful) {
                    _state.update {
                        it.copy(dormitories = response.body()?.data?.items ?: emptyList(), error = null)
                    }
                } else {
                    _state.update { it.copy(error = "Gagal memuat data asrama.") }
                }
            }
            .onFailure { _state.update { state -> state.copy(error = it.message ?: "Gagal memuat data asrama.") } }
    }

    private suspend fun handleMutation(
        result: Result<Response<*>>,
        successMessage: String,
        onSuccess: suspend () -> Unit,
        onError: suspend () -> Unit = {},
    ) {
        result.onSuccess {
            if (it.isSuccessful) {
                _state.update { state -> state.copy(isLoading = false, toast = successMessage, error = null) }
                onSuccess()
            } else {
                _state.update { state -> state.copy(isLoading = false, toast = "Permintaan gagal: ${it.message()}") }
                onError()
            }
        }.onFailure {
            _state.update { state -> state.copy(isLoading = false, toast = it.message ?: "Aksi gagal dijalankan.") }
            onError()
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MasterDataViewModel(MasterDataRepository()) as T
        }
    }
}
