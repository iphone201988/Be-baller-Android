package com.beballer.beballer.data.api

import android.util.Log
import com.beballer.beballer.base.local.SharedPrefManager
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService, private val sharedPrefManager: SharedPrefManager) :
    ApiHelper {

    override suspend fun apiForRawBody(request: HashMap<String, Any>,url:String): Response<JsonObject> {
        return apiService.apiForRawBody(request,url)
    }

    override suspend fun commonParamPostApi(url:String): Response<JsonObject> {
        return apiService.commonParamPostApi(getTokenFromSPref(),url)
    }


    override suspend fun apiForRawBodyWithToken(request: HashMap<String, Any>,url:String): Response<JsonObject> {
        return apiService.apiForRawBodyWithToken( getTokenFromSPref(),request,url)
    }

    override suspend fun apiPostForRawBody(
        url: String,
        request: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPostForRawBody(getTokenFromSPref(), url, request)
    }

    override suspend fun apiForFormData(data: HashMap<String, Any>, url: String): Response<JsonObject> {
        return apiService.apiForFormData(data,url)
    }

    override suspend fun apiForFormDataPut(
        data: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForFormDataPut(data,url, getTokenFromSPref())
    }



    override suspend fun apiGetOnlyAuthToken(url: String,data: HashMap<String, Any>): Response<JsonObject> {
        return apiService.apiGetOnlyAuthToken(url,getTokenFromSPref(),data)
    }

    override suspend fun apiGetWithQuery(data: HashMap<String, String>, url: String): Response<JsonObject> {
        return apiService.apiGetWithQuery(url,data)
    }
    override suspend fun apiForPostMultipartList(url: String,map: HashMap<String, RequestBody>,
                                          part: MutableList<MultipartBody.Part>): Response<JsonObject> {
        return apiService.apiForPostMultipartList(url,getTokenFromSPref(), map, part)
    }

    override suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url,getTokenFromSPref(), map, part)
    }


    override suspend fun apiForMultipartPut(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?
    ): Response<JsonObject> {
        return apiService.apiForMultipartPut(url,getTokenFromSPref(), map, part)
    }


    override suspend fun apiForMultipartCourt(
        url: String,
        map: HashMap<String, RequestBody>?,
       courtPicFirst: MultipartBody.Part?,
       courtPicSecond: MultipartBody.Part?,
       courtPicThird: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForMultipartCourt(url,getTokenFromSPref(), map,courtPicFirst,courtPicSecond,courtPicThird)
    }

    override suspend fun apiPutForRawBody(
        url: String,
        map: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPutForRawBody(url,getTokenFromSPref(), map)
    }

    private fun getTokenFromSPref(): String {

        return "Bearer ${
            sharedPrefManager.getToken()
        }"
    }

}