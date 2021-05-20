package edu.phystech.annnigm

import com.google.gson.Gson
import okhttp3.*


class HttpManager {
    private val gson: Gson = Gson()
    private val apiUrl = "https://klipsa.nic11.ru/clips/api"
    val urlSignIn: String = "$apiUrl/users/me"
    val urlRegistration: String = "$apiUrl/users"

    val urlGlucose: String = "$apiUrl/users/me/glucometer_measurements"
    val urlInsulin: String = "$apiUrl/users/me/insulin_injections"
    val urlMeal: String = "$apiUrl/users/me/meals"

    val urlRemoveGlucose: String = "$apiUrl/glucometer_measurements/"
    val urlRemoveInsulin: String = "$apiUrl/insulin_injections/"
    val urlRemoveMeal: String = "$apiUrl/meals/"

    fun createGetRequest(token: String?, url: String): Response {
        val request = Request.Builder()
            .url(url)
            .addHeader("X-Fb-Token", token)
            .build()
        return OkHttpClient().newCall(request).execute()
    }

    fun createPostRequest(token: String?, url: String, jsonBody: String) : Response {
        val request = Request.Builder()
            .addHeader("X-Fb-Token", token)
            .url(url)
            .post(RequestBody
                .create(MediaType.parse("application/json"), jsonBody))
            .build()
        return OkHttpClient().newCall(request).execute()
    }

    fun createDeleteRequest(token: String?, url: String) : Response {
        val request = Request.Builder()
            .addHeader("X-Fb-Token", token)
            .url(url)
            .delete()
            .build()
        return OkHttpClient().newCall(request).execute()
    }

    fun parseServerResponseOnUserRequest(response: String) {
        MainApplication.user = gson.fromJson(response, User::class.java)
    }

    fun getIdFromResponse(response: String) : Int {
        val list = gson.fromJson(response, List::class.java) as List<Double>
        return list[0].toInt()
    }

}

data class GlucometerMeasure(var measurementDate: String? = null,
                             var glucoseLevel: Double = 0.0,
                             var id: Int = -1)

data class InsulinMeasure(var injectionDate: String? = null,
                          var insulinAmount: Double = 0.0,
                          var id: Int = -1)

data class MealMeasure(var mealDate: String? = null,
                       var food: String? = null,
                       var id: Int = -1)

