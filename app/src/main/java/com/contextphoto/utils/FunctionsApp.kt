package com.contextphoto.utils

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import com.contextphoto.data.Album
import org.w3c.dom.Comment
import kotlin.jvm.java

object FunctionsApp {

    fun durationTranslate(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
            else -> "$minutes:${secs.toString().padStart(2, '0')}"
        }
    }
}