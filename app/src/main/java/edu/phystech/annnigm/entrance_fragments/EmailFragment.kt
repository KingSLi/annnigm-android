package edu.phystech.annnigm.entrance_fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import edu.phystech.annnigm.*
import edu.phystech.annnigm.firebase.FirebaseManager
import kotlinx.android.synthetic.main.fragment_email.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class EmailFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var router : Router
    private val firebaseManager: FirebaseManager = FirebaseManager(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        router = Router(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_email, container, false)

        val email: TextView = layout.findViewById(R.id.email_input)
        val pass1: TextView = layout.findViewById(R.id.password_input)
        val pass2: TextView = layout.findViewById(R.id.password2_input)

        email.addTextChangedListener(afterTextChangedListener)
        pass1.addTextChangedListener(afterTextChangedListener)
        pass2.addTextChangedListener(afterTextChangedListener)

        val buttonNext: Button = requireActivity().findViewById(R.id.entrance_next_button)
        buttonNext.visibility = View.INVISIBLE
        buttonNext.setOnClickListener {
            emailPasswordOnClick(email, layout, pass1, pass2)
        }
        return layout
    }

    private fun emailPasswordOnClick(email: TextView, layout: View, pass1: TextView, pass2: TextView) {
        if (!isEmailValid(email)) {
            Toast.makeText(layout.context, "Укажите правильно e-mail", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass1.text.toString().hashCode() != pass2.text.toString().hashCode()) {
            Toast.makeText(layout.context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return
        }
        val email = email.text.toString()
        val password = pass1.text.toString()

        MainApplication.user.email = email

        firebaseManager.registration(email, password, requireActivity(), requireContext())
    }

//



//    private fun saveUserToCache() {
//        val file = File(requireContext().cacheDir, "user.json")
//        var gson = Gson()
//        var json = gson.toJson(MainApplication.user)
//        file.writeText(json)
//    }

    private var afterTextChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val buttonNext: Button = requireActivity().findViewById(R.id.entrance_next_button)
            buttonNext.visibility = if (isInputsValid()) View.VISIBLE else View.INVISIBLE
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }


    private fun isEmailValid(email: TextView) =
        Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()

    private fun isInputsValid() =
        email_input.text.isNotEmpty() && password_input.text.isNotEmpty() && password2_input.text.isNotEmpty()

}
