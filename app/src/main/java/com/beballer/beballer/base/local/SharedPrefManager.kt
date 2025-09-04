package com.beballer.beballer.base.local

import android.content.SharedPreferences
import com.beballer.beballer.data.model.LoginApiResponse
import com.beballer.beballer.data.model.UserProfile
import com.google.gson.Gson
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    object KEY {
        const val LOGIN_RESPONSE = "loginResponse"
        const val USER_PROFILE = "UserProfile"
        const val USER_TOKEN = "userToken"
        const val COURT_DETAILS_ID = "courtDetailsId"
    }


    fun setLoginData(bean: LoginApiResponse) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.LOGIN_RESPONSE, Gson().toJson(bean))
        editor.apply()
    }


    fun getLoginData(): LoginApiResponse? {
        val s: String? = sharedPreferences.getString(KEY.LOGIN_RESPONSE, null)
        return Gson().fromJson(s, LoginApiResponse::class.java)
    }


    fun setProfileData(isFirst: UserProfile) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_PROFILE, Gson().toJson(isFirst))
        editor.apply()
    }


    fun getProfileData(): UserProfile? {
        val s: String? = sharedPreferences.getString(KEY.USER_PROFILE, null)
        return Gson().fromJson(s, UserProfile::class.java)
    }


    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        val token: String? = sharedPreferences.getString(KEY.USER_TOKEN, null)
        return token
    }

    fun saveCourtDetailsId(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY.COURT_DETAILS_ID, token)
        editor.apply()
    }

    fun getCourtDetailsId(): String? {
        val token: String? = sharedPreferences.getString(KEY.COURT_DETAILS_ID, null)
        return token
    }


    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}