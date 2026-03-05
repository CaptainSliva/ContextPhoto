package com.contextphoto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.contextphoto.data.COMMENT_DATABASE
import com.contextphoto.data.VERSION_DB

@Database(
    entities = [Comment::class],
    version = VERSION_DB,
    exportSchema = false,
)
abstract class CommentDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: CommentDatabase? = null

        fun getDatabse(context: Context): CommentDatabase =
            INSTANCE ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            CommentDatabase::class.java,
                            COMMENT_DATABASE,
                        ).build()
                INSTANCE = instance
                instance
            }
    }
}
