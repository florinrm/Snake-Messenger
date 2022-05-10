package com.example.snakemessenger.logging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.snakemessenger.database.AppDatabase
import com.example.snakemessenger.databinding.ActivityLogsBinding
import com.example.snakemessenger.general.Constants

class LogsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogsBinding
    private lateinit var db: AppDatabase
    private lateinit var logsAdapter: LogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        logsAdapter = LogsAdapter(listOf())
        binding.logsRecyclerView.adapter = logsAdapter
        db.messageExchangeLogDao.getLiveMessageExchangeLogs().observe(this, { logs ->
            val mutableLogs = logs.toMutableList()
            mutableLogs.sortBy { it.sourceTimestamp }
            logsAdapter.logs = mutableLogs
            binding.logsRecyclerView.scrollToPosition(logs.size - 1)
        })
    }
}