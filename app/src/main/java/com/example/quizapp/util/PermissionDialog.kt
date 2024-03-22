package com.example.quizapp.util

import android.app.AlertDialog
import android.content.Context

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

fun permissionDialog(
    context: Context,
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
){
    val alertDialog = AlertDialog.Builder(context).apply {
        setTitle("Permission required")
        setMessage(permissionTextProvider.getDescription(isPermanentlyDeclined))
        setOnDismissListener { onDismiss() }
        setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        setPositiveButton(
            if (isPermanentlyDeclined) "Grant Permission" else "OK"
        ) { _, _ ->
            if (isPermanentlyDeclined) {
                onGoToAppSettingsClick()
            } else {
                onOkClick()
            }
        }
    }.create()
    alertDialog.show()
}

class GalleryPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined gallery permission. You can go to the app settings to grant it."
        } else {
            "This app needs access to gallery access permission to upload image from your gallery."
        }
    }
}