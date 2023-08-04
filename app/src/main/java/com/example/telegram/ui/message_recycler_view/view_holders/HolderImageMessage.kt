package com.example.telegram.ui.message_recycler_view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.databinding.ItemImageMessageBinding
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.downloadAndSetImage

class HolderImageMessage(binding: ItemImageMessageBinding): RecyclerView.ViewHolder(binding.root), MessageHolder {
    private val blockUserImageMessage = binding.blockUserImageMessage
    private val chatUserImage = binding.chatUserImage
    private val chatUserImageMessageTime = binding.chatUserImageMessageTime

    private val blockReceivedImageMessage = binding.blockReceivedImageMessage
    private val chatReceivedImage = binding.chatReceivedImage
    private val chatReceivedImageMessageTime = binding.chatReceivedImageMessageTime

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedImageMessage.visibility = View.GONE
            blockUserImageMessage.visibility = View.VISIBLE
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view
                .timeStamp.asTime()
        } else {
            blockReceivedImageMessage.visibility = View.VISIBLE
            blockUserImageMessage.visibility = View.GONE
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text = view
                .timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDetach() {
    }


}