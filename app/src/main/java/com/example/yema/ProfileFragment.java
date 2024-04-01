package com.example.yema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private SignOutListener signOutListener;
    private CircleImageView profileImage;
    private Uri selectedImageUri;
    private Uri photoDownlaodUri;       // For the image which is uploaded in the firebase
    private TextView nameTextView, emailTextView;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        LinearLayout buttonSignOut = view.findViewById(R.id.mainSign);
        profileImage = view.findViewById(R.id.profile_photo_profile_page);
        nameTextView = view.findViewById(R.id.username_profile_page);
        emailTextView = view.findViewById(R.id.email_profile_page);
        firebaseAuth = FirebaseAuth.getInstance();

        // Checking for the providers to load the profile accordingly
        SharedPreferences providerPreferences = requireActivity().getSharedPreferences("login_provider", Context.MODE_PRIVATE);
        final String PROVIDER_ID = providerPreferences.getString("PROVIDER_ID", "");

        switch (PROVIDER_ID) {
            case "google.com":
                loadGoogleProfile();
                break;
            case "firebase_auth":
                loadFirebaseProfile();
                break;
            case "":
                Log.d("TAG", "Invalid Provider");
            default:
                Toast.makeText(requireActivity(), "Invalid Login", Toast.LENGTH_SHORT).show();
        }

        // listener for signing out => start
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signOutListener != null) {
                    signOutListener.onSignOut();
                }
            }
        });
        // listener for signing out => end

        // listener for profile photo selection => start
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Choose Photo Source");
                builder.setItems(new CharSequence[]{"Google Drive", "Google Photos", "Gallery"}, ((dialog, which) -> {
                    Intent intent = null;
                    switch (which) {
                        case 0:
                            intent = ImagePicker.getGoogleDrivePickerIntent(requireActivity());
                            break;
                        case 1:
                            intent = ImagePicker.getGooglePhotosPickerIntent(requireActivity());
                            break;
                        case 2:
                            intent = ImagePicker.getImagePickerIntent(requireActivity());
                            break;
                    }
                        startActivityForResult(intent, ImagePicker.PICK_IMAGE_REQUEST);
                }));
                builder.show();
            }
        });
        // listener for profile photo selection => end

        return view;
    }

    // Handing the selected image uri
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImageUri = ImagePicker.handleImagePickerResult(requireActivity(), requestCode, resultCode, data);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs_uri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (selectedImageUri != null) {
            profileImage.setImageURI(selectedImageUri);
            profileImage.setEnabled(false);
            Log.d("IMAGE", selectedImageUri.toString());
            String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
            uploadToFirebase(selectedImageUri, email);
            editor.putString("imageUri", selectedImageUri.toString());
            editor.apply();
        }
    }

    private void loadFirebaseProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String email = currentUser.getEmail();
        emailTextView.setText(email);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Root").child("Users");

        // TODO: I don't know how the fuck this works
        String finalEmail = email;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot emailSnapshot : snapshot.getChildren()) {
                    String userEmail = emailSnapshot.getKey();
                    Log.d("USER_EMAIL", userEmail);
                    if (userEmail.equals(finalEmail.replace('.', ','))) {
                        for (DataSnapshot userChildSnapshot : emailSnapshot.getChildren()) {
                            String userId = userChildSnapshot.getKey();
                            Log.d("USER_ID", userId);
                            if (!userId.equals("Income") && !userId.equals("Expenses")) {
                                DataSnapshot userDataSnapshot = userChildSnapshot;
                                String userName = userDataSnapshot.child("name").getValue(String.class);
                                Log.d("USER_NAME", userName);
                                nameTextView.setText(userName);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Profile Image Fetching Part
        email = email.replace('@', '_');
        email = email.replace('.', '_');
        String profileImagePath = "profile_images/" + email + ".jpg";
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child(profileImagePath);

        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("Profile Fetch Status", "Profile Fetch Success");
                photoDownlaodUri = uri;
                Picasso.get().load(photoDownlaodUri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Profile Fetch Status", "Profile Fetch Failure: " + e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d("Profile Fetch Status", "Image Fetch Operation Cancelled");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignOutListener) {
            signOutListener = (SignOutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement SignOutListener");
        }
    }
    public void setSignOutListener(SignOutListener listener) {
        signOutListener = listener;
    }

    // uploads the selected media directly to firebase console
    private void uploadToFirebase(Uri imageUri, String email) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        email = email.replace('@', '_');
        email = email.replace('.', '_');

        StorageReference profileImageReference = storageReference.child("profile_images/" + email + ".jpg");
        UploadTask imageUploadTask = profileImageReference.putFile(imageUri);

        imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Image Upload", "Image Uploaded Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Image Upload", "Image Upload Failed");
            }
        });
    }

    // Fetching the image from google account directly (the image is not editable within the app, but can be changed using the google account)
    private void loadGoogleProfile() {
        GoogleSignInAccount currentUserAccount = GoogleSignIn.getLastSignedInAccount(requireActivity());
        assert currentUserAccount != null;
        Uri profileImageUri = currentUserAccount.getPhotoUrl();
        Glide.with(this).load(profileImageUri).into(profileImage);
        String name = currentUserAccount.getDisplayName();
        String email = currentUserAccount.getEmail();
        nameTextView.setText(name);
        emailTextView.setText(email);
        profileImage.setEnabled(false);
    }
}

