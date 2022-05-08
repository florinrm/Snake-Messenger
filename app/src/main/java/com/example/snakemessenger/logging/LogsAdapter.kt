package com.example.snakemessenger.logging

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.models.MessageExchangeLog

class LogsAdapter(var logs: List<MessageExchangeLog>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = logs.size
}