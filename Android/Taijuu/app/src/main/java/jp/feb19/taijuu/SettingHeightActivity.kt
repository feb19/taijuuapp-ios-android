package jp.feb19.taijuu

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingHeightActivity : AppCompatActivity(), GoogleFitListener {
    override fun onWeightLoaded() {

    }

    override fun onHeightLoaded() {

    }

    override fun onSetWeight(success: Boolean) {
    }

    override fun onSetHeight(success: Boolean) {
        if (success) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.setting_height)

        val editText = findViewById<EditText>(R.id.heightText)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.isEnabled = false

        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.isEnabled = validate(s)

                if (s != null) {
                    Log.i("TaijuuApp", s.length.toString())
                    Log.i("TaijuuApp", s.toString())
                }
            }
        })

        if (GoogleFit.shared().height != 0.0) {
            editText.setText((GoogleFit.shared().height * 100.0).toString(), TextView.BufferType.NORMAL)
        }



        saveButton.setOnClickListener(View.OnClickListener {
            GoogleFit.shared().listener = this
            GoogleFit.shared().setHeight((editText.text.toString().toFloat() / 100.0F), this)
        })

    }

    fun validate(s: CharSequence?): Boolean {
        return s!= null && s.length > 1 && s.toString().toDouble() > 10 && s.toString().toDouble() < 200
    }
}