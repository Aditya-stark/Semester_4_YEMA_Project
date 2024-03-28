package com.example.yema;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePicker {
    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int PICK_IMAGE_FROM_GOOGLE_DRIVE_REQUEST = 1002;
    public static final int PICK_IMAGE_FROM_GOOGLE_PHOTOS_REQUEST = 1003;
    @SuppressLint("IntentReset")
    public static Intent getImagePickerIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    public static Intent getGoogleDrivePickerIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    public static Intent getGooglePhotosPickerIntent(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return intent;
    }
    public static Uri handleImagePickerResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            return data.getData();

        else if (requestCode == PICK_IMAGE_FROM_GOOGLE_DRIVE_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            return data.getData();

        else if (requestCode == PICK_IMAGE_FROM_GOOGLE_PHOTOS_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            return data.getData();

        return null;
    }
}
