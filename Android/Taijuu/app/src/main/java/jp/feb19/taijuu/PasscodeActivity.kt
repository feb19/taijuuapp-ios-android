package jp.feb19.taijuu

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PasscodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.passcode)

        val passcodeTextView = findViewById<TextView>(R.id.passcodeTextView)
        val passcodeEditText = findViewById<EditText>(R.id.passcodeEditText)
        passcodeEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passcodeTextView.text = "パスコードを入力してください"

                if (s != null && s.length == 4) {
                    passcodeTextView.text = "パスコードが違います"

                    val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
                    val passcode = sharedPreferences.getString("passcode", "")
                    if (passcode.toString() == passcodeEditText.text.toString()) {
                        finish()
                    }
                }
            }
        })
    }
}