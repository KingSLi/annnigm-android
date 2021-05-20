package edu.phystech.annnigm.firebase

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.phystech.annnigm.*
import edu.phystech.annnigm.cards.Card
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FirebaseManager(val context: Context, private val job : Job = Job()) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userToken = ""
    val httpManager: HttpManager = HttpManager()
    val gson = Gson()

    val cacheWorker = CacheWorker(context.cacheDir)

    ///////////////////////////
    /// Registration blocks ///
    ///////////////////////////
    fun registration(email: String, password: String, activity: FragmentActivity, context: Context) {
        GlobalScope.launch {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        clipsServerVerification()
                        val mainIntent = Intent(context, MainActivity::class.java)
                        activity.startActivity(mainIntent)
                        activity.finish()
                        // saveUserToCache()
                    } else {
                        // Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun clipsServerVerification() {
        GlobalScope.launch {
            val mUser = mAuth.currentUser
            mUser!!.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val idToken = task.result!!.token
                        registrateUserIntoServer(idToken)
                    } else {
                        //MainApplication.user.isDefined = false
                        // todo: message check internet
                    }
                }
        }
    }

    private fun registrateUserIntoServer(idToken: String?) {
        GlobalScope.launch {
            val response = httpManager.createPostRequest(
                token = idToken,
                url = httpManager.urlRegistration,
                jsonBody = gson.toJson(MainApplication.user))
            if (response.isSuccessful) {
                httpManager.parseServerResponseOnUserRequest(response.body()!!.string())
            } else {
                // todo: registration failed message
            }
        }
    }

    ///////////////////////////
    /// Sign in blocks ///
    ///////////////////////////
    fun signIn(email: String, pass: String, activity: FragmentActivity, context: Context) {
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // val user = mAuth.currentUser
                    Toast.makeText(context, "You are authorized",
                        Toast.LENGTH_SHORT).show()


                    val mainIntent = Intent(context, MainActivity::class.java)
                    activity.startActivity(mainIntent)
                    activity.finish()
                } else {
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    ///////////////////////////
    /// Remove Card  blocks ///
    ///////////////////////////
    fun removeCardFromServer(card: Card) {
        GlobalScope.launch {
            if (MainApplication.userToken != "") {
                removeCardWithToken(card)
            } else {
                val mUser = mAuth.currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result!!.token
                            MainApplication.userToken = token!!
                            // removeCardWithToken(card)
                        } else {
                            // todo: message check internet
                        }
                    }
            }
        }
    }

    private fun removeCardWithToken(card: Card) {
        GlobalScope.launch {
            val token = MainApplication.userToken
            val response = when (card.type) {
                MeasureType.MEAL -> httpManager.createDeleteRequest(
                    token = token,
                    url = httpManager.urlRemoveMeal + card.serverId.toString()
                )
                MeasureType.INSULIN -> httpManager.createDeleteRequest(
                    token = token,
                    url = httpManager.urlRemoveInsulin + card.serverId.toString()
                )
                MeasureType.GLUCOSE -> httpManager.createDeleteRequest(
                    token = token,
                    url = httpManager.urlRemoveGlucose + card.serverId.toString()
                )
                else -> httpManager.createGetRequest("BAD_TOKEN", "NO_URL")
            }
            if (response.isSuccessful) {
//                requireActivity().runOnUiThread {
//                    removeCardFromAdapter(card)
//                }
            }
        }
    }

    ////////////////////////
    /// Add Card  blocks ///
    ////////////////////////
    fun addCardToServer(card: Card, activity: FragmentActivity, updater: (Card, Int) -> (Unit)) {
        GlobalScope.launch {
            if (MainApplication.userToken != "") {
                sendCardWithToken(card, activity, updater)
            } else {
                val mUser = mAuth.currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result!!.token
                            MainApplication.userToken = token!!
                            sendCardWithToken(card, activity, updater)
                        } else {
                            // todo: message check internet
                        }
                    }
            }
        }
    }


    private fun sendCardWithToken(card: Card, activity: FragmentActivity, updater: (Card, Int) -> (Unit)) {
        GlobalScope.launch {
            val token = MainApplication.userToken
            val response = when (card.type) {
                MeasureType.MEAL -> httpManager.createPostRequest(
                    token = token,
                    url = httpManager.urlMeal,
                    jsonBody = gson.toJson(
                        listOf(
                            MealMeasure(
                                mealDate = card.time,
                                food = card.description,
                                id = card.serverId
                            )
                        )
                    )
                )
                MeasureType.INSULIN -> httpManager.createPostRequest(
                    token = token,
                    url = httpManager.urlInsulin,
                    jsonBody = gson.toJson(
                        listOf(
                            InsulinMeasure(
                                injectionDate = card.time,
                                insulinAmount = card.description.toDouble(),
                                id = card.serverId
                            )
                        )
                    )
                )
                MeasureType.GLUCOSE -> httpManager.createPostRequest(
                    token = token,
                    url = httpManager.urlGlucose,
                    jsonBody = gson.toJson(
                        listOf(
                            GlucometerMeasure(
                                measurementDate = card.time,
                                glucoseLevel = card.description.toDouble(),
                                id = card.serverId
                            )
                        )
                    )
                )
                else -> httpManager.createGetRequest("BAD_TOKEN", "NO_URL")
            }
            if (response.isSuccessful) {
                card.serverId = httpManager.getIdFromResponse(response.body()!!.string())
                activity.runOnUiThread {
                    updater(card, -1)
                }
            }
        }
    }

    /////////////////////////
    /// Load Card  blocks ///
    /////////////////////////
    fun loadListCards(activity: FragmentActivity, updater: (Card, Int) -> (Unit)) {
        GlobalScope.launch {
            if (MainApplication.userToken != "") {
                loadListFromServer(activity, updater)
            } else {
                val mUser = mAuth.currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result!!.token
                            MainApplication.userToken = token!!
                            loadListFromServer(activity, updater)
                        } else {
                            // cacheWorker.loadListFromCache(activity, updater)
                        }
                    }
            }
        }
    }

    private fun loadListFromServer(activity: FragmentActivity, updater: (Card, Int) -> (Unit)) {
        GlobalScope.launch {
            val token = MainApplication.userToken
            val response = httpManager.createGetRequest(
                token = token,
                url = httpManager.urlGlucose
            )
            if (response.isSuccessful) {
                val listType = object : TypeToken<List<GlucometerMeasure>>() {}.type
                val body = response.body()!!.string()
                var glucList = gson.fromJson(body, listType) as ArrayList<GlucometerMeasure>
                val cardList = ArrayList<Card>()
                for (measure in glucList) {
                    if (measure.measurementDate == null)
                        continue
                    cardList.add(Card(
                        type = MeasureType.GLUCOSE,
                        time = measure.measurementDate!!,
                        description = measure.glucoseLevel.toString(),
                        serverId = measure.id
                    ))
                    // MainApplication.listGlusoseMeasure.add(measure.glucoseLevel)
                }
                activity.runOnUiThread {
                    for (card in cardList) {
                        updater(card, -1)
                    }
                }

            }
        }
        GlobalScope.launch {
            val token = MainApplication.userToken
            val response = httpManager.createGetRequest(
                token = token,
                url = httpManager.urlMeal
            )
            if (response.isSuccessful) {
                val listType = object : TypeToken<List<MealMeasure>>() {}.type
                val body = response.body()!!.string()
                var glucList = gson.fromJson(body.toString(), listType) as ArrayList<MealMeasure>
                val cardList = ArrayList<Card>()
                for (measure in glucList) {
                    if (measure.mealDate == null  || measure.food == null)
                        continue
                    cardList.add(Card(
                        type = MeasureType.MEAL,
                        time = measure.mealDate!!,
                        description = measure.food!!,
                        serverId = measure.id
                    ))
                }
                activity.runOnUiThread {
                    for (card in cardList) {
                        updater(card, -1)
                    }
                }

            }
        }
        GlobalScope.launch {
            val token = MainApplication.userToken
            val response = httpManager.createGetRequest(
                token = token,
                url = httpManager.urlInsulin
            )
            if (response.isSuccessful) {
                val listType = object : TypeToken<List<InsulinMeasure>>() {}.type
                val body = response.body()!!.string()
                var glucList = gson.fromJson(body, listType) as ArrayList<InsulinMeasure>
                val cardList = ArrayList<Card>()
                for (measure in glucList) {
                    if (measure.injectionDate == null)
                        continue
                    cardList.add(Card(
                        type = MeasureType.INSULIN,
                        time = measure.injectionDate!!,
                        description = measure.insulinAmount.toString(),
                        serverId = measure.id
                    ))
                    // MainApplication.listInsulinMeasure.add(measure.insulinAmount)
                }
                activity.runOnUiThread {
                    for (card in cardList) {
                        updater(card, -1)
                    }
                }

            }
        }
    }


}