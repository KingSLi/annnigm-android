package edu.phystech.annnigm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_add_measure.*

class AddMeasureActivity : AppCompatActivity() {
    val httpManager: HttpManager = HttpManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_measure)

        val types = findViewById<RadioGroup>(R.id.radio_buttons)
        types.check(R.id.radio_button_eat)

        val button = findViewById<Button>(R.id.button_add_measure)
        button.setOnClickListener {
            val description = findViewById<EditText>(R.id.measure_description)

            if (description.text.isNotEmpty()) {

                val myIntent = Intent()

                val type = when(types.checkedRadioButtonId) {
                    R.id.radio_button_eat -> MeasureType.MEAL
                    R.id.radio_button_device -> MeasureType.GLUCOSE
                    R.id.radio_button_insulin -> MeasureType.INSULIN
                    else -> MeasureType.UNDEFINED
                }

                if (!validateInputsWithType(type)) {
                    return@setOnClickListener
                }

                myIntent.putExtra("type", type.toString())
                myIntent.putExtra("description", description.text.toString())
                setResult(RESULT_OK, myIntent)
                finish()
            } else {
                description.error = "Введите описание"
            }
        }
    }

    private fun validateInputsWithType(type: MeasureType) : Boolean {
        if (type == MeasureType.INSULIN || type == MeasureType.GLUCOSE) {
            try {
                measure_description.text.toString().toDouble()
            } catch (e: Exception) {
                measure_description.error = "Введите данные правильно\n Для измерений Глюкозы или Инсулина можно писать только числа и ."
                return false
            }
        }
        return true
    }
}

