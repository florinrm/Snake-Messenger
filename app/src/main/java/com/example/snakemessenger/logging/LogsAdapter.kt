package com.example.snakemessenger.logging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.database.Converters
import com.example.snakemessenger.databinding.LogItemBinding
import com.example.snakemessenger.models.MessageExchangeLog

class LogsAdapter(var logs: List<MessageExchangeLog>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = LogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LogsItemViewHolder).bind(position)
    }

    override fun getItemCount() = logs.size

    internal inner class LogsItemViewHolder(private val viewBinding: LogItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int) {
            val log = logs[position]
            viewBinding.destinationContactNameItem.text = log.destinationContactId
            viewBinding.sourceContactNameItem.text = log.sourceContactId
            viewBinding.destinationTimestamp.text = Converters.fromTimestamp(log.destinationTimestamp).toString()
            viewBinding.sourceTimestamp.text = Converters.fromTimestamp(log.sourceTimestamp).toString()
        }
    }
}