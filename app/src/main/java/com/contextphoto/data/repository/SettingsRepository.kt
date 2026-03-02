package com.contextphoto.data.repository

import android.util.Log
import com.contextphoto.data.datasource.SettingsSource
import com.contextphoto.db.Comment
import com.contextphoto.utils.FunctionsFiles.createExportFile
import com.contextphoto.utils.FunctionsFiles.importCommentsFromFile
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsRepository
    @Inject
    constructor(
        private val settingsSource: SettingsSource
    )
{
    suspend fun exportCommentsToStorage(): List<String> {
        createExportFile()
        val listComments = settingsSource.getAllComments()
        return listComments.first().map { Gson().toJson(it) }
    }

    suspend fun importCommentsFromStorage(fileText: List<String>): Boolean {
//        try {
            val listComments = mutableListOf<Comment>()
            fileText.forEach {
                val comment = Gson().fromJson(it, Comment::class.java)
                Log.d("resultString", comment.toString())
                listComments.add(comment)
            }
            settingsSource.importCommentsToDatabase(listComments)
            return true
//        }
//        catch (e: Exception) {return false}
    }

    suspend fun exportCommentsToFirestore() {
        settingsSource.exportCommentsToFirestore()
    }

    suspend fun importCommentsFromFirestore() {
        settingsSource.importCommentsFromFirestore()
    }

}