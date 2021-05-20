package edu.phystech.annnigm.entrance_fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import edu.phystech.annnigm.R
import edu.phystech.annnigm.Router


class ChooseFragment : Fragment() {

    private lateinit var router : Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router = Router(requireActivity(), R.id.fragment_container)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_choose, container, false)

        val signIn: Button = layout.findViewById(R.id.button_signin)
        val registration: Button = layout.findViewById(R.id.button_registration)

        signIn.setOnClickListener { router.navigateTo(fragmentFactory = ::SignInFragment) }
        registration.setOnClickListener { router.navigateTo(fragmentFactory = ::NameFragment) }

        val buttonNext: Button = requireActivity().findViewById(R.id.entrance_next_button)
        buttonNext.visibility = View.GONE
        return layout
    }
}
