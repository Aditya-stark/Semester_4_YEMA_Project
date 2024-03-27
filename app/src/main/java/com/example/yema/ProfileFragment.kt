import android.app.Activity
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
import com.example.yema.SignOutListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var signOutListener: SignOutListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val buttonSignOut = view.findViewById<Button>(R.id.mainSign)
        buttonSignOut.setOnClickListener {
            signOutListener.onSignOut()
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
