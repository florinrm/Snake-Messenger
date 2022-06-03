package com.example.snakemessenger.chats

import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.databinding.ChatSentFileMessageBinding

class CurrentUserFileMessageViewHolder(private val binding: ChatSentFileMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getSenderNameTextView() = binding.senderName

    fun getFileNameTextView() = binding.fileName

    fun getMessageStatusImageView() = binding.messageStatus

    fun getTimestampTextView() = binding.messageTimestamp

    fun getSenderProfilePictureImageView() = binding.senderProfilePic
}