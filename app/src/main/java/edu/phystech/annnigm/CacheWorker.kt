package edu.phystech.annnigm

import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.phystech.annnigm.cards.Card
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

class CacheWorker(val cacheDir: File) {
    val gson = Gson()

    fun loadListFromCache(activity: FragmentActivity, updater: (Card, Int) -> (Unit)) {
        GlobalScope.async {
            //            listCardsAll = ArrayList()
            val listCardsAll: ArrayList<Card> = try {
                val file = File(cacheDir, "listCardsAll.json")
                val str = file.readText()
                val listType = object : TypeToken<List<Card>>() {}.type
                gson.fromJson(str, listType) as ArrayList<Card>
            } catch (e: Exception) {
                ArrayList()
            }

            activity.runOnUiThread {
                for (card in listCardsAll) {
                    updater(card, -1)
                }
            }
//            cardsAdapter.addCards(listCardsAll)
//            cardsAdapter.notifyDataSetChanged()
//
//            for (card in listCardsAll) {
//                when (card.type) {
//                    MeasureType.MEAL -> listCardsMeal.add(card)
//                    MeasureType.INSULIN -> listCardsInsulin.add(card)
//                    MeasureType.GLUCOSE -> listCardsGlucometr.add(card)
//                }
//            }
        }
    }

    fun saveListsToCache(listCardsAll: ArrayList<Card>) {
        GlobalScope.async {
            val file = File(cacheDir, "listCardsAll.json")
            val json = gson.toJson(listCardsAll)
            file.writeText(json)
        }
    }

}