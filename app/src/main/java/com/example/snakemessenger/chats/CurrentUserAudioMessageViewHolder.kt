package com.example.snakemessenger.chats

import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.databinding.ChatSentAudioMessageBinding

class CurrentUserAudioMessageViewHolder(private val binding: ChatSentAudioMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getSenderProfilePictureImageView() = binding.senderProfilePic

    fun getSenderNameTextView() = binding.senderName

    fun getMessageContentVideoView() = binding.messageContent

    fun getTimestampTextView() = binding.messageTimestamp

    fun getMessageStatusImageView() = binding.messageStatus
}