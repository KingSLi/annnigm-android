package edu.phystech.annnigm.entrance_fragments


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import edu.phystech.annnigm.MainApplication
import edu.phystech.annnigm.R
import edu.phystech.annnigm.Router
import info.hoang8f.android.segmented.SegmentedGroup
import kotlinx.android.synthetic.main.fragment_name.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class NameFragment : Fragment() {

    private lateinit var router : Router
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = Router(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_name, container, false)
        val inName: EditText = layout.findViewById(R.id.person_name)
        val inSurname: EditText = layout.findViewById(R.id.person_email)
        val inDate: EditText = layout.findViewById(R.id.person_date)
        val inHeight: EditText = layout.findViewById(R.id.person_height)
        val inWeight: EditText = layout.findViewById(R.id.person_weight)
        val inGender: SegmentedGroup = layout.findViewById(R.id.person_gender)
        val dataButton: Button = layout.findViewById(R.id.button_data_more)

        inGender.setTintColor(R.color.colorPrimaryDark)
        inGender.check(R.id.select_types_all)

        inName.addTextChangedListener(afterTextChangedListener)
        inSurname.addTextChangedListener(afterTextChangedListener)
        inDate.addTextChangedListener(afterTextChangedListener)
        inHeight.addTextChangedListener(afterTextChangedListener)
        inName.addTextChangedListener(afterTextChangedListener)
        inWeight.addTextChangedListener(afterTextChangedListener)
        inGender.setOnCheckedChangeListener { _, _ ->
            val buttonNext: Button = requireActivity().findViewById(R.id.entrance_next_button)
            buttonNext.visibility = if (isInputsValid()) View.VISIBLE else View.INVISIBLE
        }

        dataButton.setOnClickListener {
            DatePickerDialog(requireContext(),
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        val buttonNext: Button = requireActivity().findViewById(edu.phystech.annnigm.R.id.entrance_next_button)
        buttonNext.visibility = View.INVISIBLE
        buttonNext.setOnClickListener { onNextClickNameFragment(inName, inSurname, inDate, inHeight, inWeight, inGender) }
        return layout
    }

    private val dateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        val date = LocalDate.of(year, monthOfYear, dayOfMonth)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedString = date.format(formatter)
        person_date.setText(formattedString)
    }


    private fun onNextClickNameFragment(inName: EditText,
                                        inSurname: EditText,
                                        inDate: EditText, inHeight:
                                        EditText, inWeight: EditText,
                                        inGender: SegmentedGroup) {
        MainApplication.user.firstName = inName.text.toString()
        MainApplication.user.lastName = inSurname.text.toString()
        MainApplication.user.birthDate = inDate.text.toString()
        MainApplication.user.height = inHeight.text.toString().toInt()
        MainApplication.user.weight = inWeight.text.toString().toDouble()
        MainApplication.user.phone = null
        MainApplication.user.sex = if (inGender.checkedRadioButtonId == R.id.person_gender_man) "MALE" else "FEMALE"

        router.navigateTo(fragmentFactory = ::EmailFragment)
    }

    private var afterTextChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val buttonNext: Button = requireActivity().findViewById(R.id.entrance_next_button)
            buttonNext.visibility = if (isInputsValid()) View.VISIBLE else View.INVISIBLE
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    private fun isInputsValid() : Boolean {
        return person_name.text.isNotEmpty() &&
                person_email.text.isNotEmpty() &&
                person_date.text.isNotEmpty() &&
                person_height.text.isNotEmpty() &&
                person_weight.text.isNotEmpty() &&
                (person_gender_man.isChecked || person_gender_woman.isChecked)
    }


}
//
//private fun setupDatePicker(layout: View) {
//        val data: DatePicker = layout.findViewById(R.id.date_picker)
//        data.maxDate = System.currentTimeMillis()
//
//        val overview: TextView = layout.findViewById(R.id.overview_age)
//        overview.text = "0"
//        MainApplication.instance.user.date = Date(System.currentTimeMillis())
//
//        data.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
//            MainApplication.instance.user.date = Date(year, monthOfYear, dayOfMonth)
//            val age = getAge(year, monthOfYear, dayOfMonth)
//            overview.text = "$age"
//        }
//    }
//
//    private fun getAge(year: Int, month: Int, day: Int): Int {
//        val dob = Calendar.getInstance()
//        val today = Calendar.getInstance()
//        dob.set(year, month, day)
//        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--
//        }
//        return age
//    }
