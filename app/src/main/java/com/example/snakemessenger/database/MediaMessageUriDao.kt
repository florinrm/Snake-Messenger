package com.example.snakemessenger.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.snakemessenger.models.MediaMessageUri
import com.example.snakemessenger.models.Message

@Dao
interface MediaMessageUriDao {
    @Query("SELECT * FROM mediamessageuri WHERE messageId = :messageId LIMIT 1")
    fun findByMessageId(messageId: String): MediaMessageUri

    @Insert
    fun addMediaMessageUri(mediaMessageUri: MediaMessageUri)
}