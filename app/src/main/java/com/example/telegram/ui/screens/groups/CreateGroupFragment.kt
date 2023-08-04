package com.example.telegram.ui.screens.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentCreateGroupBinding
import com.example.telegram.model.CommonModel
import com.example.telegram.ui.screens.base.BaseFragment
import com.example.telegram.ui.screens.main_list.MainListFragment
import com.example.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private lateinit var binding: FragmentCreateGroupBinding

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter
    private var mUri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.create_group)
        hideKeyboard()
        initRecyclerView()
        binding.createGroupCounts.text = getPlurals(listContacts.size)
        binding.createGroupPhoto.setOnClickListener { addPhoto() }
        binding.createGroupBtnComplete.setOnClickListener {
            val nameGroup = binding.createGroupInputName.text.toString()
            if (nameGroup.isEmpty()) {
                showToast("Введите имя")
            } else {
                createGroupToDatabase(nameGroup, mUri, listContacts){
                    replaceFragment(MainListFragment())

                }
            }
            binding.createGroupInputName.requestFocus()
        }
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            mUri = CropImage.getActivityResult(data).uri
            binding.createGroupPhoto.setImageURI(mUri)

        }
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.createGroupRecyclerView
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach {
            mAdapter.updateListItems(it)
        }

    }

}