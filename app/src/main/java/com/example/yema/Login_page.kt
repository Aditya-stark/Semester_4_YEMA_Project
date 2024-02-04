package com.example.yema

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.yema.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import org.w3c.dom.Text

class Login_page : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    private lateinit var auth:FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        val instance = FirebaseAuth.getInstance()
        val loginButton = findViewById<Button>(R.id.login_btn)

        val password_editText: EditText = findViewById(R.id.login_password)
        val email_editText: EditText = findViewById(R.id.login_email)



//        Intent
        val signup_btn_hyper: TextView = findViewById<TextView>(R.id.sign_up_btn)
        signup_btn_hyper.setOnClickListener{
            startActivity(Intent(this, signup_page::class.java))
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
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}