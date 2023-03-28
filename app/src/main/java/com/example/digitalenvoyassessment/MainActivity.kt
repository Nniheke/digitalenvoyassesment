package com.example.digitalenvoyassessment

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.digitalenvoyassesment.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.id_text_view)
        val clickableText = "Hello"

        setClickableText(textView, textView.text as String, clickableText) {
            Toast.makeText(this, "$clickableText has been clicked!", Toast.LENGTH_SHORT).show()
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }


    /**
     * Function sets onClickListener for textView & displays a toast when clicking the word 'hello' in hello world
     */
    private fun setClickableText(
        textView: TextView,
        text: String,
        clickableText: String,
        onClick: () -> Unit
    ) {
        val builder = SpannableStringBuilder()

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color =
                    resources.getColor(androidx.appcompat.R.color.secondary_text_default_material_light)
            }
        }

        val startIndex = text.indexOf(clickableText, ignoreCase = true)

        val endIndex = startIndex + clickableText.length

        builder.append(text)
        builder.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = builder
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
                    return
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }
}