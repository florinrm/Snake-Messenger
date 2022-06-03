package com.example.snakemessenger.chats

import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.databinding.ChatReceivedFileMessageBinding

class OtherUserFileMessageViewHolder(private val binding: ChatReceivedFileMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getSenderNameTextView() = binding.senderName

    fun getFileNameTextView() = binding.fileName

    fun getTimestampTextView() = binding.messageTimestamp

    fun getSenderProfilePictureImageView() = binding.senderProfilePic
}