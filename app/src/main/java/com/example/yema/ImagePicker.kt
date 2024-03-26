package com.example.yema

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
object ImagePicker {
    const val PICK_IMAGE_REQUEST = 1001
    const val PICK_IMAGE_FROM_GOOGLE_DRIVE_REQUEST = 1002
    const val PICK_IMAGE_FROM_GOOGLE_PHOTOS_REQUEST = 1003

    @SuppressLint("IntentReset")
    fun openImagePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun openGoogleDrivePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        activity.startActivityForResult(intent, PICK_IMAGE_FROM_GOOGLE_DRIVE_REQUEST)
    }

    fun openGooglePhotosPicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
        activity.startActivityForResult(intent, PICK_IMAGE_FROM_GOOGLE_PHOTOS_REQUEST)
    }

    fun handleImagePickerResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?): Uri? {
        return when {
            requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null -> data.data
            requestCode == PICK_IMAGE_FROM_GOOGLE_DRIVE_REQUEST && resultCode == Activity.RESULT_OK && data != null -> data.data
            requestCode == PICK_IMAGE_FROM_GOOGLE_PHOTOS_REQUEST && resultCode == Activity.RESULT_OK && data != null -> data.data
            else -> null
        }
    }
}
