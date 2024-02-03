package com.example.yema

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import org.w3c.dom.Text

class Login_page : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val signup_btn_hyper: TextView = findViewById<TextView>(R.id.sign_up_btn)

        signup_btn_hyper.setOnClickListener{
            startActivity(Intent(this, signup_page::class.java))
        }

//      Show Password
        val editText: EditText = findViewById(R.id.login_password)
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

    }






}