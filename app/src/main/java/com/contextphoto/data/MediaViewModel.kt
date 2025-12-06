package com.contextphoto.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaViewModel : ViewModel() {
    private val _listPictures = MutableStateFlow<List<Picture>>(emptyList())
    private val _mediaPosition = MutableStateFlow<Int>(0)
    private val _loadMediaState = MutableStateFlow<Boolean>(true)
    val listPictures = _listPictures.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val loadMediaState = _loadMediaState.asStateFlow()

    fun addPicture(pic: Picture) {
        if (loadMediaState.value) {
            _listPictures.update { currentList ->
                currentList.toMutableList().apply {
                    add(pic)
                }
            }
        }
    }

    fun updateMediaPosition(pos: Int) {
        _mediaPosition.value = pos
    }

    fun deletePicture(pic: Picture) {
        _listPictures.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun resetMediaPosition() {
        _mediaPosition.value = 0
    }

    fun updatePicture(pic: Picture) {
        _listPictures.update { currentList ->
            currentList.map {
                if (it.bID == pic.bID) {
                    pic
                } else {
                    it
                }
            }
        }
    }

    fun changeState(state: Boolean = true) {
        if (state) {
            _loadMediaState.value = true
            _listPictures.value = emptyList()
        } else {
            _loadMediaState.value = false
        }
    }
}
