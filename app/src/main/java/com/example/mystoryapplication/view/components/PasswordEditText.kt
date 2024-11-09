package com.example.mystoryapplication.view.components

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.example.mystoryapplication.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {
    private var textInputEditText: TextInputEditText
    private lateinit var errorTextView: TextView
    private var showAlert: Boolean = false

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText)
        showAlert = attributes.getBoolean(R.styleable.PasswordEditText_showAlert, false)
        attributes.recycle()

        hint = context.getString(R.string.label_password)
        textInputEditText = TextInputEditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        addView(textInputEditText)

//        Log.d("AlertsValue", showAlert.toString())

        if(showAlert) {
            errorTextView = TextView(context).apply {
                text = context.getString(R.string.alert_password)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                }
                visibility = View.GONE
            }
            addView(errorTextView)

            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    validatePassword(s)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun validatePassword(password: CharSequence?) {
        if (password != null && password.length < 8) {
            errorTextView.visibility = View.VISIBLE
        } else {
            errorTextView.visibility = View.GONE
        }
    }
}