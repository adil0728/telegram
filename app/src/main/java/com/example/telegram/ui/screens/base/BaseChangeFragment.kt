package com.example.telegram.ui.screens.base

import android.view.*
import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.hideKeyboard

open class BaseChangeFragment(layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_confirm_change -> change()
        }
        return true
    }

    open fun change() {

    }
}
