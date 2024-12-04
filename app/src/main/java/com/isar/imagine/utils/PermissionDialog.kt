package com.isar.imagine.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object PermissionDialog {

    val code = 100


    private fun onDismiss(dialog: AlertDialog) {
        dialog.dismiss()
    }


    fun permissionDialog(
        context: Context,
        permissionTextProvider: PermissionTextProvider,
        isPermanentlyDeclined: Boolean,
        onClick: () -> Unit,
    ) {
        AlertDialog.Builder(context).apply {


            setPositiveButton(
                if (isPermanentlyDeclined) {
                    "Settings"
                } else "OK"

            ) { _, _ ->
                if (isPermanentlyDeclined) {
                    onClick()
                } else onClick()
            }
            setNegativeButton(
                if (isPermanentlyDeclined) {
                    "Cancel"
                } else "Cancel"
            ) { _, _ ->
                onDismiss(this.create())

            }
            setTitle("Permission Required")
            setMessage(permissionTextProvider.getDescription(isPermanentlyDeclined))
        }.show()
    }


}