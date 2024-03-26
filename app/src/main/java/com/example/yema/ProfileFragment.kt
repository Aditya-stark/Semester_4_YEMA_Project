import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.yema.ImagePicker
import com.example.yema.R
import com.example.yema.SignOutListener // Import the correct package for SignOutListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.PhantomReference

class ProfileFragment : Fragment() {

    private lateinit var signOutListener: SignOutListener // Declare SignOutListener
    private lateinit var profilePhoto: CircleImageView
    private lateinit var storageReference: StorageReference
    private lateinit var selectedImageUri: Uri
    private lateinit var uploadButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val buttonSignOut = view.findViewById<Button>(R.id.mainSign)
        profilePhoto = view.findViewById(R.id.profile_photo_profile_page)
        uploadButton = view.findViewById(R.id.uploadBTN)
        val optionArray = arrayOf("Google Drive", "Google Photos", "Gallery")
        uploadButton = view.findViewById(R.id.uploadBTN)
        profilePhoto.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Choose Photo Source")
            builder.setItems(optionArray, DialogInterface.OnClickListener { dialog, which ->
                run {
                    when (which) {
                        0 -> ImagePicker.openGoogleDrivePicker(requireActivity())
                        1 -> ImagePicker.openGooglePhotosPicker(requireActivity())
                        2 -> ImagePicker.openImagePicker(requireActivity())
                        else -> {
                            Log.d("TAG", "Finish")
                        }
                    }
                }
            })
            builder.show()
//            storageReference.putFile()
        }

        uploadButton.setOnClickListener {
            uploadToFirebaseStorage("com", )
        }

        buttonSignOut.setOnClickListener {
            signOutListener.onSignOut() // Call the sign-out method
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignOutListener) {
            signOutListener = context
        } else {
            throw RuntimeException("$context must implement SignOutListener")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedImageUri =
            ImagePicker.handleImagePickerResult(requireActivity(), requestCode, resultCode, data)!!
        if (selectedImageUri != null) {
            profilePhoto.setImageURI(selectedImageUri)
        }
    }

    private fun uploadToFirebaseStorage(email: String) {
        storageReference = FirebaseStorage.getInstance().getReference()
        var childReference: StorageReference = storageReference.child("profile_images/" + email + ".jpg")

        val uploadTask: UploadTask = childReference.putFile(selectedImageUri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(requireActivity(), "Photo Uploaded", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
        }
    }

    fun setSignOutListener(listener: SignOutListener) {
        signOutListener = listener
    }
}
