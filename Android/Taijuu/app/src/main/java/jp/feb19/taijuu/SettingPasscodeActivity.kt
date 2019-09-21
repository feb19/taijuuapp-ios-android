package jp.feb19.taijuu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingPasscodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.setting_passcode)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val passcodeEditText = findViewById<EditText>(R.id.passcodeEditText)
        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        saveButton.setOnClickListener(View.OnClickListener {
            val passcode = passcodeEditText.text.toString()
            if (passcodeEditText.text.toString().length == 4) {
                Log.i("TaijuuApp", passcodeEditText.text.toString())

                val editor = sharedPreferences.edit()
                editor.putString("passcode", passcode)
                editor.apply()

                val toast = Toast.makeText(this, "パスコードを設定しました", Toast.LENGTH_SHORT)
                toast.show()

                finish()
            }
        })
    }

}