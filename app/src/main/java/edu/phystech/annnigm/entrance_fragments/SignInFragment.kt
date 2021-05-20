package edu.phystech.annnigm.entrance_fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import edu.phystech.annnigm.MainActivity
import edu.phystech.annnigm.R
import edu.phystech.annnigm.Router
import edu.phystech.annnigm.firebase.FirebaseManager
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var router : Router
    lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        router = Router(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_sign_in, container, false)

        firebaseManager = FirebaseManager(requireContext())
        val buttonNext: Button = layout.findViewById(R.id.button_signin)
        buttonNext.setOnClickListener { inputOnClick(layout) }

        return layout
    }

    private fun inputOnClick(layout: View) {
        val emailInput: EditText = layout.findViewById(R.id.email_input)
        val passwordInput: EditText = layout.findViewById(R.id.password_input)
        if (isInputsValid()) {
            firebaseManager.signIn(emailInput.text.toString(), passwordInput.text.toString(), requireActivity(), requireContext())
        } else {
            if (emailInput.text.isEmpty())
                emailInput.error = "Введите почту"
            if (passwordInput.text.isEmpty())
                passwordInput.error = "Введите пароль"
        }
    }

    private fun isInputsValid() = email_input.text.isNotEmpty() && password_input.text.isNotEmpty()

}
