package com.example.telegram.ui.message_recycler_view.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.databinding.ItemVoiceMessageBinding
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.AppVoicePlayer
import com.example.telegram.utilits.asTime

class HolderVoiceMessage(binding: ItemVoiceMessageBinding) : RecyclerView.ViewHolder(binding.root),
    MessageHolder {
    private val mAppVoicePlayer = AppVoicePlayer()
    private val blockUserVoiceMessage = binding.blockUserVoiceMessage
    private val chatUserBtnPlay = binding.chatUserBtnPlay
    private val chatUserBtnStop = binding.chatUserBtnStop
    private val chatUserVoiceMessageTime = binding.chatUserVoiceMessageTime

    private val blockReceivedVoiceMessage = binding.blockReceivedVoiceMessage
    private val chatReceivedBtnPlay = binding.chatReceivedBtnPlay
    private val chatReceivedBtnStop = binding.chatReceivedBtnStop
    private val chatReceivedVoiceMessageTime = binding.chatReceivedVoiceMessageTime


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedVoiceMessage.visibility = View.GONE
            blockUserVoiceMessage.visibility = View.VISIBLE
            chatUserVoiceMessageTime.text = view
                .timeStamp.asTime()
        } else {
            blockReceivedVoiceMessage.visibility = View.VISIBLE
            blockUserVoiceMessage.visibility = View.GONE
            chatReceivedVoiceMessageTime.text = view
                .timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.init()
        if (view.from == CURRENT_UID) {
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop {
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE
                }
            }
        } else {
            chatReceivedBtnPlay.setOnClickListener {
                chatReceivedBtnPlay.visibility = View.GONE
                chatReceivedBtnStop.visibility = View.VISIBLE
                chatReceivedBtnStop.setOnClickListener {
                    stop {
                        chatReceivedBtnStop.setOnClickListener(null)
                        chatReceivedBtnPlay.visibility = View.VISIBLE
                        chatReceivedBtnStop.visibility = View.GONE
                    }
                }
                play(view) {

                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun stop(function: () -> Unit) {
        mAppVoicePlayer.stop() {
            function()
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl) {
            function()
        }

    }

    override fun onDetach() {
        chatUserBtnPlay.setOnClickListener(null)
        chatReceivedBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}