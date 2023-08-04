package com.example.telegram.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.databinding.ItemAddContactsBinding
import com.example.telegram.model.CommonModel
import com.example.telegram.utilits.downloadAndSetImage
import com.example.telegram.utilits.showToast

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class AddContactsHolder(binding: ItemAddContactsBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.addContactsName
        val lastMessage = binding.addContactsLastMessage
        val photo = binding.addContactsPhoto
        val choice = binding.addContactsChoice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val binding = ItemAddContactsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = AddContactsHolder(binding)
        holder.itemView.setOnClickListener {
            if (listItems[holder.bindingAdapterPosition].choice){
                holder.choice.visibility = View.INVISIBLE
                listItems[holder.bindingAdapterPosition].choice = false
                AddContactsFragment.listContacts.remove(listItems[holder.bindingAdapterPosition])
            }else{
                holder.choice.visibility = View.VISIBLE
                listItems[holder.bindingAdapterPosition].choice = true
                AddContactsFragment.listContacts.add(listItems[holder.bindingAdapterPosition])
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size


    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.name.text = listItems[position].fullname
        holder.lastMessage.text = listItems[position].lastMessage
        holder.photo.downloadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}