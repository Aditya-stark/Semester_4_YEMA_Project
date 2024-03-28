package com.example.yema

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.yema.databinding.LoginPageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class Login_page : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var binding: LoginPageBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private var GAuthTag: String = "G-Auth Status"
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = findViewById<Button>(R.id.login_btn)

        val instance = FirebaseAuth.getInstance()
        val password_editText: EditText = findViewById(R.id.login_password)
        val email_editText: EditText = findViewById(R.id.login_email)
        auth = Firebase.auth

//        Intent
        val signup_btn_hyper: TextView = findViewById<TextView>(R.id.sign_up_btn)
        signup_btn_hyper.setOnClickListener{
            startActivity(Intent(this, signup_page::class.java))
            finish()
        }

        val forgotPass = findViewById<TextView>(R.id.forgot_pass)
        forgotPass.setOnClickListener {
            startActivity(Intent(this, forgot_password_page::class.java))
        }

//      Show Password
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.show_password_eye)

        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable)

            wrappedDrawable.setBounds(0, 0, wrappedDrawable.intrinsicWidth, wrappedDrawable.intrinsicHeight)
            password_editText.setCompoundDrawablesWithIntrinsicBounds(null, null, wrappedDrawable, null)

            var isPasswordVisible = false

            password_editText.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (password_editText.right - wrappedDrawable.bounds.width())) {
                        password_editText.inputType = if (isPasswordVisible) {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        } else {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        }
                        isPasswordVisible = !isPasswordVisible
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

        //Normal Login Button Code
        loginButton.setOnClickListener {
            val email = email_editText.text.toString().trim()
            val password = password_editText.text.toString().trim()

            instance.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                    val preferences = getSharedPreferences("login_provider", MODE_PRIVATE);
                    preferences.edit().putString("PROVIDER_ID", "firebase_auth").apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_LONG).show()
                }

            }
        }

        //GOOGLE SIGN IN CODE
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signinGoogle.setOnClickListener{
            val signInClient = googleSignInClient.signInIntent
            //startActivity(signInClient)
            launcher.launch(signInClient)
        }
    }



    // Changes: Modified the Listeners ~Penguin5681
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account : GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnSuccessListener {
                    // Logging the Login Status => Success
                    Log.d(GAuthTag, "Login Status Success")
                    val preferences = getSharedPreferences("login_provider", MODE_PRIVATE);
                    preferences.edit().putString("PROVIDER_ID", "google.com").apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    // Logging the Login Status => Failure
                    Log.d(GAuthTag, it.message.toString())

                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }.addOnCanceledListener {
                    // Logging the Login Status => Operation Cancelled
                    Log.d(GAuthTag, "Operation Cancelled")
                }
            }
        }
        else {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }


}