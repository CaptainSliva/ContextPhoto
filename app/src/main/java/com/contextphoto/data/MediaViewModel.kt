package com.contextphoto.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MediaViewModel : ViewModel() {
    private val _listMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _listSelectedMedia = MutableStateFlow<List<Picture>>(emptyList())
    private val _mediaPosition = MutableStateFlow(0)
    private val _loadMediaState = MutableStateFlow(true)
    private val _bottomMenuVisible = MutableStateFlow(false)
//    private val _selectProcess = MutableStateFlow(false)
    private val _checkboxVisible = MutableStateFlow(false)
    private val _albumBid = MutableStateFlow("")
    val listMedia = _listMedia.asStateFlow()
    val listSelectedMedia = _listSelectedMedia.asStateFlow()
    val mediaPosition = _mediaPosition.asStateFlow()
    val loadMediaState = _loadMediaState.asStateFlow()
    val bottomMenuVisible = _bottomMenuVisible.asStateFlow()
//    val selectProcess = _selectProcess.asStateFlow()
    val checkboxVisible = _checkboxVisible.asStateFlow()
    val albumBid = _albumBid.asStateFlow()


    fun addMedia(pic: Picture) {
        if (loadMediaState.value) {
            _listMedia.update { currentList ->
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
        _listMedia.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun resetMediaPosition() {
        _mediaPosition.value = 0
    }

    fun updatePicture(pic: Picture) {
        _listMedia.update { currentList ->
            currentList.map {
                if (it.bID == pic.bID) {
                    pic
                } else {
                    it
                }
            }
        }
    }


    fun selectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                add(pic)
            }
        }
    }

    fun removeSelectMedia(pic: Picture) {
        _listSelectedMedia.update { currentList ->
            currentList.toMutableList().apply {
                remove(pic)
            }
        }
    }

    fun clearSelectedMedia() {
        _listSelectedMedia.value = emptyList()
    }


    fun changeState(state: Boolean = true) {
        if (state) {
            _loadMediaState.value = true
            _listMedia.value = emptyList()
        } else {
            _loadMediaState.value = false
        }
    }

    fun changeStateBottomMenu(state: Boolean? = null) {
        if (state != null) {
            _bottomMenuVisible.value = state
        }
        else {
            _bottomMenuVisible.value = !bottomMenuVisible.value
        }

//        _selectProcess.value = !selectProcess.value
    }

    fun changeStateCheckBox(state: Boolean? = null) {
        if (state != null) {
            _checkboxVisible.value = state
        }
        else {
            _checkboxVisible.value = !checkboxVisible.value
        }
    }

    fun changeAlbumBid(Bid: String) {
        _albumBid.value = Bid
    }
}
