package com.example.snakemessenger.chats

import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.databinding.ChatReceivedVideoMessageBinding

class OtherUserVideoMessageViewHolder(private val binding: ChatReceivedVideoMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getSenderProfilePictureImageView() = binding.senderProfilePic

    fun getSenderNameTextView() = binding.senderName

    fun getMessageContentVideoView() = binding.messageContent

    fun getTimestampTextView() = binding.messageTimestamp
}