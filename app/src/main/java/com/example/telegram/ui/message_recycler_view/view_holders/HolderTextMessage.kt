package com.example.telegram.ui.message_recycler_view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.databinding.ItemTextMessageBinding
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.downloadAndSetImage

class HolderTextMessage(binding: ItemTextMessageBinding) : RecyclerView.ViewHolder(binding.root), MessageHolder {
    private val blockUserMessage = binding.blockUserMessage
    private val chatUserMessage = binding.chatUserMessage
    private val chatUserMessageTime = binding.chatUserMessageTime

    private val blockReceivedMessage = binding.blockReceivedMessage
    private val chatReceivedMessage = binding.chatReceivedMessage
    private val chatReceivedMessageTime = binding.chatReceivedMessageTime



    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view
                .timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text = view
                .timeStamp.asTime()
        }

    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }
}