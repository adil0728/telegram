package com.example.telegram.ui.screens.main_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.databinding.ItemMainListBinding
import com.example.telegram.model.CommonModel
import com.example.telegram.ui.screens.groups.GroupChatFragment
import com.example.telegram.ui.screens.single_chat.SingleChatFragment
import com.example.telegram.utilits.TYPE_CHAT
import com.example.telegram.utilits.TYPE_GROUP
import com.example.telegram.utilits.downloadAndSetImage
import com.example.telegram.utilits.replaceFragment

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(binding: ItemMainListBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.mainListName
        val lastMessage = binding.mainListLastMessage
        val photo = binding.mainListPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val binding = ItemMainListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = MainListHolder(binding)
        holder.itemView.setOnClickListener {
            when(listItems[holder.bindingAdapterPosition].type){
                TYPE_CHAT ->replaceFragment(SingleChatFragment(listItems[holder.bindingAdapterPosition]))
                TYPE_GROUP -> replaceFragment(GroupChatFragment(listItems[holder.bindingAdapterPosition]))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size


    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.name.text = listItems[position].fullname
        holder.lastMessage.text = listItems[position].lastMessage
        holder.photo.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}