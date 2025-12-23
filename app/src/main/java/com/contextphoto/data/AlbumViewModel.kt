package com.contextphoto.data

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AlbumRepository
) : ViewModel() { // TODO видимо все функции должны быть в репозитории, тут у меня остаётся 1 функция и переменные которые она устанавливает, в View я слушаю изменилось ли состояние флага который показывает завершилась ли работа с альбомами, и когда он меняется - беру список
    private val _albumList = MutableStateFlow<List<Album>>(emptyList())
    private val _loadAlbumState = MutableStateFlow(true)
    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val albumList = _albumList.asStateFlow()
    val loadAlbums = _loadAlbumState.asStateFlow()
    val selectedAlbum = _selectedAlbum.asStateFlow()

    fun loadAlbumList(context: Context) {
        if (loadAlbums.value) {
            _albumList.update { currentList ->
                currentList.toMutableList().apply {
                    viewModelScope.launch {addAll(repository.loadAlbumList(context))}
                }
            }
        }

        _loadAlbumState.value = false
    }

    fun addAlbum(album: Album) {
        viewModelScope.launch {repository.addAlbum(_albumList as List<Album>, album)}
    }

    fun deleteAlbum(album: Album?) {
        viewModelScope.launch {
            repository.deleteAlbum(_albumList as List<Album>, album)
        _selectedAlbum.value = null
        }
    }

    fun updateAlbum(album: Album) {
        viewModelScope.launch {
            repository.updateAlbum(_albumList as List<Album>, album)
            _selectedAlbum.value = null
        }
    }

    fun selectAlbum(album: Album) {
        _selectedAlbum.value = album
    }

    fun changeStateAlbum(state: Boolean = true) {
        _loadAlbumState.value = state
    }
}
