package edu.phystech.annnigm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.phystech.annnigm.entrance_fragments.ChooseFragment
import kotlinx.android.synthetic.main.activity_entrance.*

class EntranceActivity : AppCompatActivity() {
    private lateinit var router: Router
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)

        mAuth = FirebaseAuth.getInstance()

        router = Router(this, R.id.fragment_container)
        // if (savedInstanceState == null) router.navigateTo(false, ::ChooseFragment)

        setHideKeyboardOnTouch(this, fragment_container)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            router.navigateTo(false, ::ChooseFragment)
        } else {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            finish()
        }
    }

    override fun onBackPressed() {
        if (!router.navigateBack()) {
            super.onBackPressed()
        }
    }
}

