import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.yema.ImagePicker
import com.example.yema.R
import com.example.yema.SignOutListener // Import the correct package for SignOutListener
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var signOutListener: SignOutListener // Declare SignOutListener
    private lateinit var profilePhoto: CircleImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val buttonSignOut = view.findViewById<Button>(R.id.mainSign)
        profilePhoto = view.findViewById(R.id.profile_photo_profile_page)
        val optionArray = arrayOf("Google Drive", "Google Photos", "Gallery")

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

    fun setSignOutListener(listener: SignOutListener) {
        signOutListener = listener
    }
}
