package com.example.snakemessenger.chats

import androidx.recyclerview.widget.RecyclerView
import com.example.snakemessenger.databinding.ChatReceivedAudioMessageBinding

class OtherUserAudioMessageViewHolder(private val binding: ChatReceivedAudioMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun getSenderProfilePictureImageView() = binding.senderProfilePic

    fun getSenderNameTextView() = binding.senderName

    fun getMessageContentVideoView() = binding.messageContent

    fun getTimestampTextView() = binding.messageTimestamp
}