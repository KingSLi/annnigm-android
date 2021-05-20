package edu.phystech.annnigm

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class PersonLoader(private val cacheDir : File) {

    private val gson = Gson()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val httpManager: HttpManager = HttpManager()

    private fun loadUserFromCacheDir() : Boolean {
        return try {
            val file = File(cacheDir, "user.json")
            val str = file.readText()
            val cachedUser = gson.fromJson(str, User::class.java)
            MainApplication.user = cachedUser
            true
        } catch (e: Exception) {
            false
        }
    }

    fun load() {
        val mUser = mAuth.currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    loadPersonByToken(idToken)
                } else {
                    // todo: message check internet
                }
            }
    }

    private fun loadPersonByToken(token: String?) {
       GlobalScope.launch {
            val response = httpManager.createGetRequest(
                    token = token,
                    url = httpManager.urlSignIn)
            if (response.isSuccessful) {
                httpManager.parseServerResponseOnUserRequest(response.body()!!.string())

            } else {// if (!loadUserFromCacheDir()) {
                setNoInformationAboutUser()
            }
        }
    }

    private fun setNoInformationAboutUser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun saveToCachedDir() {
        val file = File(cacheDir, "user.json")
        val json = gson.toJson(MainApplication.user)
        file.writeText(json)
    }
}

// todo: snackbar
// todo: fraggmets