package com.example.snakemessenger.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.snakemessenger.models.MessageExchangeLog

@Dao
interface MessageExchangeLogDao {
    @Query("SELECT * FROM logs")
    fun getMessageExchangeLogs(): List<MessageExchangeLog>

    @Query("SELECT * FROM logs")
    fun getLiveMessageExchangeLogs(): LiveData<List<MessageExchangeLog>>

    @Insert
    fun addMessageExchangeLog(messageExchangeLog: MessageExchangeLog)

    @Update
    fun updateMessageExchangeLog(messageExchangeLog: MessageExchangeLog)

    @Delete
    fun deleteMessageExchangeLog(messageExchangeLog: MessageExchangeLog)
}