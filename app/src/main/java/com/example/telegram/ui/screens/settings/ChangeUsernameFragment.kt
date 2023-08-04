package com.example.telegram.ui.screens.settings

import android.os.Bundle
import android.view.*
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentChangeUsernameBinding
import com.example.telegram.ui.screens.base.BaseChangeFragment
import com.example.telegram.utilits.*
import java.util.*


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private lateinit var binding: FragmentChangeUsernameBinding
    private lateinit var mNewUsername: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputUsername.setText(USER.username)
    }



    override fun change() {
        mNewUsername = binding.settingsInputUsername.text.toString().lowercase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERSNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUsername)) {
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()

                    }
                })
        }

    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERSNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    updateCurrentUsername(mNewUsername)
                }
            }
    }


}