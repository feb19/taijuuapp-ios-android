package jp.feb19.taijuu

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.TextView


class InputPickerbasedActivity : AppCompatActivity(), GoogleFitListener {
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

        setContentView(R.layout.input_pickerbased)

        // 整数
        val numberPicker = findViewById<NumberPicker>(R.id.number_picker)
        val numberPicker1 = findViewById<NumberPicker>(R.id.number_picker1)
        numberPicker.maxValue = 150
        numberPicker.minValue = 0

        // 小数点
        numberPicker1.maxValue = 9
        numberPicker1.minValue = 0

        numberPicker.setOnValueChangedListener(OnValueChangeListener { _, _, _ ->
            updateBMI()
        })
        numberPicker1.setOnValueChangedListener(OnValueChangeListener { _, _, _ ->
            updateBMI()
        })

        if (GoogleFit.shared().bodyMasses.size > 0) {
            numberPicker.value = GoogleFit.shared().bodyMasses[0].bodyMass.toInt()
            numberPicker1.value = (GoogleFit.shared().bodyMasses[0].bodyMass.rem(1) * 10).toInt()
        } else {
            numberPicker.value =  60
            numberPicker1.value = 0
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener(View.OnClickListener {
            val weight = (numberPicker.value + numberPicker1.value * 0.1).toFloat()

            Log.i("TaijuuApp", "---------")
            Log.i("TaijuuApp", numberPicker.value.toString())
            Log.i("TaijuuApp", numberPicker1.value.toString())
            Log.i("TaijuuApp", (numberPicker.value + numberPicker1.value * 0.1).toString())
            Log.i("TaijuuApp", "${weight}")
            Log.i("TaijuuApp", "---------")

            GoogleFit.shared().listener = this
            GoogleFit.shared().setWeight(weight, this)
        })

        updateBMI()
    }

    fun updateBMI () {
        val numberPicker = findViewById<NumberPicker>(R.id.number_picker)
        val numberPicker1 = findViewById<NumberPicker>(R.id.number_picker1)
        val bmiTextView = findViewById<TextView>(R.id.bmiTextView)
        val weight = (numberPicker.value + numberPicker1.value * 0.1).toFloat()

        if (GoogleFit.shared().height > 0.0) {
            val height = GoogleFit.shared().height
            val bmi = weight / (height * height)
            Log.i("TaijuuApp", "${bmi} ${weight} ${GoogleFit.shared().height}")

            bmiTextView.text = "BMI: ${bmi}"
        }
    }
}