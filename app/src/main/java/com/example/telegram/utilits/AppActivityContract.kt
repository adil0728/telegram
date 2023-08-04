package com.example.telegram.utilits

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class AppActivityContract : ActivityResultContract<String, Int?>() {



    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getIntExtra("my_result_key", 42)
    }
}