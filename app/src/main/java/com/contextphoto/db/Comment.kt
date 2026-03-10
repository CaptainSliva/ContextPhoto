package com.contextphoto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Entity(
    tableName = "comments",
    indices = [Index("id")],
)
data class Comment(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true) val id: Int,
    @SerializedName("image_uri")
    @ColumnInfo(name = "image_uri") val image_uri: String,
    @SerializedName("image_hash")
    @ColumnInfo(name = "image_hash") val image_hash: String,
    @SerializedName("image_comment")
    @ColumnInfo(name = "image_comment") val image_comment: String,
)
