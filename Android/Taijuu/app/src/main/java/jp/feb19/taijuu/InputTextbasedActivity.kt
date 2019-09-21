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
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InputTextbasedActivity : AppCompatActivity(), GoogleFitListener {
    override fun onWeightLoaded() {

    }

    override fun onHeightLoaded() {

    }

    override fun onSetWeight(success: Boolean) {
        if (success) {
            finish()
        }
    }

    override fun onSetHeight(success: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editText = findViewById<EditText>(R.id.weightEditText)
        if (GoogleFit.shared().bodyMasses.size > 0) {
            editText.setText(GoogleFit.shared().bodyMasses[0].bodyMass.toString(), TextView.BufferType.NORMAL)
        }

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

        saveButton.setOnClickListener(View.OnClickListener {
            GoogleFit.shared().listener = this
            GoogleFit.shared().setWeight(editText.text.toString().toFloat(), this)
        })

        updateBMI()
    }

    fun updateBMI () {
        val editText = findViewById<EditText>(R.id.weightEditText)
        val bmiTextView = findViewById<TextView>(R.id.bmiTextView)
        val weight = editText.text.toString().toFloat()

        if (GoogleFit.shared().height > 0.0) {
            val height = GoogleFit.shared().height
            val bmi = weight / (height * height)

            bmiTextView.text = "BMI: ${bmi}"
        }
    }

    fun validate(s: CharSequence?): Boolean {
        return s!= null && s.length > 1 && s.toString().toDouble() > 10 && s.toString().toDouble() < 200
    }
}