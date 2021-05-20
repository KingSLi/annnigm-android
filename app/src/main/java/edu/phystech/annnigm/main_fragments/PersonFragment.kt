package edu.phystech.annnigm.main_fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import edu.phystech.annnigm.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class PersonFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var personLoader : PersonLoader
    lateinit var outName: TextView
    lateinit var outAge: TextView
    lateinit var outHeight: TextView
    lateinit var outWeight: TextView
    lateinit var outEmail: TextView
    lateinit var outSex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_person, container, false)
        val outButton: Button = layout.findViewById(R.id.button_sign_out)


        setupOutLabels(layout)


        outButton.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(requireContext(), "log out", Toast.LENGTH_SHORT).show()
            val mainIntent = Intent(requireContext(), EntranceActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            requireActivity().finish()
        }
        personLoader = PersonLoader(requireContext().cacheDir)

        if (MainApplication.user.isNotEmpty) {
            updateUI()
        } else {
            GlobalScope.launch {
                personLoader.load()
            }
        }
        GlobalScope.launch {
            while (!MainApplication.user.isNotEmpty) {
                sleep(100)
            }
            updateUI()
        }
        updateUI()
        return layout
    }

    private fun setupOutLabels(layout: View) {
        outName = layout.findViewById(R.id.person_name)
        outAge = layout.findViewById(R.id.person_date)
        outHeight = layout.findViewById(R.id.person_height)
        outWeight = layout.findViewById(R.id.person_weight)
        outEmail = layout.findViewById(R.id.person_email)
        outSex = layout.findViewById(R.id.person_gender)
    }

    fun updateUI() {
        if (!MainApplication.user.isNotEmpty)
            outName.setText("loading")
            // todo : show loading dialog
        else {
            outName.text = MainApplication.user.firstName + ' ' + MainApplication.user.lastName
            outHeight.text = MainApplication.user.height.toString()
            outWeight.text = MainApplication.user.weight.toString()
            outSex.text = if (MainApplication.user.sex == "MALE") "муж." else "жен."
            outEmail.text = MainApplication.user.email
            outAge.text = MainApplication.user.birthDate
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        personLoader.saveToCachedDir()
    }
}




