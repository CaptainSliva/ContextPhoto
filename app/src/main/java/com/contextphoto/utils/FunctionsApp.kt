package com.contextphoto.utils

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import com.contextphoto.data.Album
import com.contextphoto.data.allAlbums
import com.contextphoto.data.listpicture
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

    fun handleMenuItemClick(
        childFragmentManager: FragmentManager,
        context: Context,
        item: MenuItem,
        album: Album,
    ) {
        when (item.itemId) {
//            R.id.action_rename_album -> RenameAlbumDialogFragment(context, album).show(childFragmentManager, "RENAME_ALBUM")
//            R.id.action_delete_album -> DeleteDialogFragment(context, album).show(childFragmentManager, "DELETE_ALBUM")
        }
    }

//    fun changeAlbumPhotoAmount(
//        albumName: String,
//        decr: Boolean = false,
//    ) {
//        if (listpicture.isNotEmpty()) {
//            for (element in allAlbums) {
//                if (element.name == albumName && decr) element.itemsCount -= listpicture.size
//                if (element.name == albumName && !decr) element.itemsCount += listpicture.size
//            }
//        }
//    }

//    fun connectToDB(context: Context): CommentsDao {
//        val db =
//            Room
//                .databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "CommentsDB",
//                ).build()
//        return db.commentsDao()
//    }
}