package com.ksuniv.pvp_log_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val summorer_name : EditText = findViewById(R.id.summorer_name)
//        summorer_name.imeOptions = EditorInfo.IME_ACTION_DONE
        summorer_name.setOnKeyListener{ v, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER) {
                val keyword: String by lazy {
                    if (summorer_name.text.toString().isNullOrEmpty()) {
                        return@lazy ""
                    } else {
                        return@lazy ""
                    }
                }
                summorer_name.clearFocus()
                summorer_name.requestFocus()

            }
            return@setOnKeyListener false
        }
    }
}