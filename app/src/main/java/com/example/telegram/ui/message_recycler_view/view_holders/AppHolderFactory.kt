package com.example.telegram.ui.message_recycler_view.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.databinding.ItemFileMessageBinding
import com.example.telegram.databinding.ItemImageMessageBinding
import com.example.telegram.databinding.ItemTextMessageBinding
import com.example.telegram.databinding.ItemVoiceMessageBinding
import com.example.telegram.ui.message_recycler_view.views.MessageView

class AppHolderFactory {
    companion object{
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
            return when(viewType){
                MessageView.MESSAGE_IMAGE -> {
                    val binding = ItemImageMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                     HolderImageMessage(binding)
                }

                MessageView.MESSAGE_VOICE -> {
                    val binding = ItemVoiceMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderVoiceMessage(binding)
                }
                MessageView.MESSAGE_FILE -> {
                    val binding = ItemFileMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderFileMessage(binding)
                }
                else -> {
                    val binding = ItemTextMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                    HolderTextMessage(binding)
                }
            }
        }
    }
}