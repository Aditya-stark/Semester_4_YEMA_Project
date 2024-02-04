package com.example.yema

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.yema.databinding.ActivityMainBinding
import com.example.yema.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import java.util.regex.Pattern
import kotlin.math.log

class signup_page : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

//        Show Password
        val editText: EditText = findViewById(R.id.signup_password)
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.show_password_eye)
        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable)

            wrappedDrawable.setBounds(0, 0, wrappedDrawable.intrinsicWidth, wrappedDrawable.intrinsicHeight)
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, wrappedDrawable, null)

            var isPasswordVisible = false

            editText.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (editText.right - wrappedDrawable.bounds.width())) {
                        editText.inputType = if (isPasswordVisible) {
                            Log.d("Not Showing", "Not Showing")
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        } else {
                            Log.d("Showing", "Showing")
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        }
                        isPasswordVisible = !isPasswordVisible
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

//        Normal Authentication
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signupBtn.setOnClickListener{
            //val username = binding.signupUsername.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            if (checkAllFields()){
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this, "Account Created!!!",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Login_page::class.java))
                    }
                    else{
                        Toast.makeText(this, "Network Error:(",Toast.LENGTH_LONG).show()
                        Log.e("error:", it.exception.toString())
                    }
                }
            }
        }

//        GOOGLE AUTHENTICATION
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.signupGoogle.setOnClickListener{
            val signInClient = googleSignInClient.signInIntent
            //startActivity(signInClient)
            launcher.launch(signInClient)
        }



//        Intent to Login_page.kt
        val signin_btn_hyper: TextView = findViewById<TextView>(R.id.sign_in_btn)
        signin_btn_hyper.setOnClickListener{
            startActivity(Intent(this, Login_page::class.java))
        }
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account : GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener{
                    if (it.isSuccessful){
                        Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    else{
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }
    override fun onStart() {
        super.onStart()

        if (auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }






//    FUNCTION TO CHECK THE INPUT FIELD ARE NOT EMPTY
    private fun checkAllFields(): Boolean{
        val email = binding.signupEmail.text.toString()

//        Username
//        if (binding.signupUsername.text.toString() == ""){
//            Toast.makeText(this, "Can't be Empty",Toast.LENGTH_LONG).show()
//            return false
//        }
//        Email
        if (binding.signupEmail.text.toString() == ""){
            Toast.makeText(this, "Can't be Empty",Toast.LENGTH_LONG).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Check Format",Toast.LENGTH_LONG).show()
            return false
        }
//        Password
        if (binding.signupPassword.text.toString()==""){
            Toast.makeText(this, "Can't be Empty",Toast.LENGTH_LONG).show()
            return false
        }
        if (binding.signupPassword.length() <= 7){
            Toast.makeText(this, "Password Aleast have 8 letters",Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }



}