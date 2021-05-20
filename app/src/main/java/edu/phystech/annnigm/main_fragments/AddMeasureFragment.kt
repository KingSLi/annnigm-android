package edu.phystech.annnigm.main_fragments


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.phystech.annnigm.*
import edu.phystech.annnigm.cards.Card
import edu.phystech.annnigm.cards.CardsAdapter
import info.hoang8f.android.segmented.SegmentedGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import android.support.design.widget.Snackbar
import android.widget.Toast
import edu.phystech.annnigm.firebase.FirebaseManager


class AddMeasureFragment : Fragment() {

//    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    val httpManager: HttpManager = HttpManager()
    lateinit var firebaseManager: FirebaseManager
    private lateinit var cardsRecycler: RecyclerView
    private lateinit var cardsAdapter: CardsAdapter
    var listCardsAll = ArrayList<Card>()
    var listCardsMeal = ArrayList<Card>()
    var listCardsInsulin = ArrayList<Card>()
    var listCardsGlucometr = ArrayList<Card>()

    var lastRemovedPositionAll = -1
    var lastRemovedPositionTypes = -1

    lateinit var lastRemovedCard: Card

    private val gson = Gson()
    private val GOOD_CODE = 1488
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_add_measure, container, false)
        firebaseManager = FirebaseManager(requireContext())
        setSelectTypeSegmentGroup(layout)
        setRecyclerView(layout)

        val fab = layout.findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            val myIntent = Intent(requireContext(), AddMeasureActivity::class.java)
            startActivityForResult(myIntent, GOOD_CODE)
        }
        return layout
    }

    private fun setSelectTypeSegmentGroup(layout: View) {
        val segmantGroup = layout.findViewById(R.id.measure_types) as SegmentedGroup
        segmantGroup.setTintColor(R.color.colorPrimaryDark)
        segmantGroup.check(R.id.select_types_all)
        segmantGroup.setOnCheckedChangeListener { _, checkedId ->
            cardsAdapter.replaceCards(
                when (checkedId) {
                    R.id.select_types_all -> listCardsAll
                    R.id.select_types_eat -> listCardsMeal
                    R.id.select_types_insulin -> listCardsInsulin
                    R.id.select_types_device -> listCardsGlucometr
                    else -> ArrayList()
                }
            )
            cardsAdapter.notifyDataSetChanged()
        }
    }

    private fun setRecyclerView(layout: View) {
        cardsRecycler = layout.findViewById<View>(R.id.cards_recycler) as RecyclerView
        cardsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        cardsAdapter = CardsAdapter(listCardsAll)
        cardsRecycler.adapter = cardsAdapter
        cardsAdapter.setOnItemClickListener(object : CardsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val card = cardsAdapter.getCard(position)
                lastRemovedPositionAll = listCardsAll.indexOf(card)
                lastRemovedPositionTypes = when(card.type) {
                    MeasureType.INSULIN -> listCardsInsulin.indexOf(card)
                    MeasureType.GLUCOSE -> listCardsGlucometr.indexOf(card)
                    else -> listCardsMeal.indexOf(card)
                }
                lastRemovedCard = card
                removeCard(card, layout.findViewById(R.id.linear_for_snackbar), position)
            }
        })
        firebaseManager.loadListCards(requireActivity(), ::addCardToAdapter)
    }

    private fun addCardToAdapter(card: Card, position: Int = -1) {
        val positionTypes = if (position == -1) 0 else lastRemovedPositionTypes
        val positionAll = if (position == -1) 0 else lastRemovedPositionAll

        listCardsAll.add(positionAll, card)
        when(card.type) {
            MeasureType.INSULIN -> {
                listCardsInsulin.add(positionTypes, card)
                MainApplication.listInsulinMeasure.add(card.description.toDouble())
            }
            MeasureType.GLUCOSE -> {
                listCardsGlucometr.add(positionTypes, card)
                MainApplication.listGlusoseMeasure.add(card.description.toDouble())
            }
            else -> listCardsMeal.add(positionTypes, card)
        }
        cardsAdapter.notifyDataSetChanged()
    }

    private fun removeCardFromAdapter(removedCard: Card, position: Int = -1) {
        listCardsAll.remove(removedCard)
        listCardsMeal.remove(removedCard)
        listCardsInsulin.remove(removedCard)
        listCardsGlucometr.remove(removedCard)
        if (position == -1) {
            cardsAdapter.notifyDataSetChanged()
        } else {
            cardsAdapter.notifyItemRemoved(position)
        }

        when(removedCard.type) {
            MeasureType.INSULIN -> MainApplication.listInsulinMeasure.remove(removedCard.description.toDouble())
            MeasureType.GLUCOSE -> MainApplication.listGlusoseMeasure.remove(removedCard.description.toDouble())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GOOD_CODE) {
            // unpack word and def sent back from AddWordActivity
            if (data == null) {
                return
            }
            val type = data.getStringExtra("type")
            val desc = data.getStringExtra("description")
            val addedCard = Card(
                type = MeasureType.valueOf(type),
                description = desc,
                time = LocalDateTime.now().toString()
            )
            firebaseManager.addCardToServer(addedCard, requireActivity(), ::addCardToAdapter)
        }
    }

    private fun removeCard(card: Card, layout: View, position: Int) {
        removeCardFromAdapter(card, position)
        showUndoSnackbar(layout)
    }

    private fun showUndoSnackbar(layout: View) {
        val snackbar = Snackbar.make( layout,  "Отменить удаление?",    Snackbar.LENGTH_LONG )
        snackbar.setAction("Отменить") { undoDelete() }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    firebaseManager.removeCardFromServer(lastRemovedCard)
                }
            }
        })
        snackbar.show()
    }

    private fun undoDelete() {
        addCardToAdapter(lastRemovedCard, lastRemovedPositionAll)
    }

    override fun onStop() {
        firebaseManager.cacheWorker.saveListsToCache(listCardsAll)
        Log.d("GG", "OnStop")
        super.onStop()
    }
}
