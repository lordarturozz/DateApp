package com.example.dateapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.dateapp.R

object Config {
    var dialog : AlertDialog? = null
    fun showDialog(context: Context) {
        dialog = AlertDialog.Builder(context)
        .setView(R.layout.loading_layout)
            .setCancelable(false)  // Optional: prevent the dialog from being canceled
            .create()
        dialog!!.show()

    }
    fun hideDialog() {
        dialog!!.dismiss()
    }
}