package com.example.yema;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

public class NewImagePicker {
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
    // ... other code ...
}
