package com.example.telegram.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.getFileFromStorage
import com.example.telegram.databinding.ItemFileMessageBinding
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.WRIGHT_FILES
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.checkPermission
import com.example.telegram.utilits.showToast
import java.io.File

class HolderFileMessage(binding: ItemFileMessageBinding) : RecyclerView.ViewHolder(binding.root),
    MessageHolder {
    private val blockUserFileMessage = binding.blockUserFileMessage
    private val chatUserBtnDownload = binding.chatUserBtnDownload
    private val chatUserFileMessageTime = binding.chatUserFileMessageTime
    private val chatUserFileName = binding.chatUserFileName
    private val chatUserProgressBar = binding.chatUserProgressBar

    private val blockReceivedFileMessage = binding.blockReceivedFileMessage
    private val chatReceivedBtnDownload = binding.chatReceivedBtnDownload
    private val chatReceivedFileMessageTime = binding.chatReceivedFileMessageTime
    private val chatReceivedFileName = binding.chatReceivedFileName
    private val chatReceivedProgressBar = binding.chatReceivedProgressBar


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedFileMessage.visibility = View.GONE
            blockUserFileMessage.visibility = View.VISIBLE
            chatUserFileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blockReceivedFileMessage.visibility = View.VISIBLE
            blockUserFileMessage.visibility = View.GONE
            chatReceivedFileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFileName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) chatUserBtnDownload.setOnClickListener { clickToBtnFile(view) }
        else chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID){
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else{
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if (checkPermission(WRIGHT_FILES)){
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl){
                    if (view.from == CURRENT_UID){
                        chatUserBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else{
                        chatReceivedBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }catch (e:java.lang.Exception){
            showToast(e.message.toString())
        }

    }


    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}