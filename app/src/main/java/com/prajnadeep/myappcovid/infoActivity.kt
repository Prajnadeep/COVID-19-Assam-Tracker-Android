package com.prajnadeep.myappcovid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView

class infoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val textView:TextView = findViewById(R.id.textViewLink)
        textView.setMovementMethod(LinkMovementMethod.getInstance())

        val emailtextView:TextView = findViewById(R.id.emailTextView)
        textView.setMovementMethod(LinkMovementMethod.getInstance())
    }
}
