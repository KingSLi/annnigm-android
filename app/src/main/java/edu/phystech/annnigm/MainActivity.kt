package edu.phystech.annnigm

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.phystech.annnigm.main_fragments.AddMeasureFragment
import edu.phystech.annnigm.main_fragments.PersonFragment
import edu.phystech.annnigm.main_fragments.RealTimeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var router: Router
    private lateinit var mAuth: FirebaseAuth

    val personFragment: Fragment = PersonFragment()
    val addMeasureFragment: Fragment = AddMeasureFragment()
    val fm = supportFragmentManager
    var active = addMeasureFragment
    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        fm.beginTransaction().add(R.id.main_fragment_container, personFragment).hide(personFragment).commit()
        fm.beginTransaction().add(R.id.main_fragment_container, addMeasureFragment).commit()

        setHideKeyboardOnTouch(this, main_fragment_container)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_add_measurements
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_person -> {
                fm.beginTransaction().hide(active).show(personFragment).commit()
                active = personFragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_realtime -> {
                fragment = RealTimeFragment()
                fm.beginTransaction().add(R.id.main_fragment_container, fragment).commit()
                fm.beginTransaction().hide(active).show(fragment).commit()
                active = fragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_add_measurements -> {
                fm.beginTransaction().hide(active).show(addMeasureFragment).commit()
                active = addMeasureFragment
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}