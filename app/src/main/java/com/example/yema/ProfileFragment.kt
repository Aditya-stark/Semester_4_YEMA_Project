import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.yema.R
import com.example.yema.SignOutListener // Import the correct package for SignOutListener

class ProfileFragment : Fragment() {

    private lateinit var signOutListener: SignOutListener // Declare SignOutListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val buttonSignOut = view.findViewById<Button>(R.id.mainSign)
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
