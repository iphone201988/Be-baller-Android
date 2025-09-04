package com.beballer.beballer.ui.player.dash_board.find.player_profile

import android.util.Log
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.ApiHelper
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class PlayerProfileActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()

    // get user by id
    fun getUserById(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getUserById", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getUserById: $e")
            }
        }
    }
       // update profile api
    fun updateProfileApi(url: String, map: HashMap<String, RequestBody>, part: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipart(url,map,part).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("updateProfileApi", it.body()))
                    } else {
                        commonObserver.postValue(Resource.error(handleErrorResponse(it.errorBody(),it.code()), null))
                    }
                }
            } catch (e: Exception) {
                Log.d("error", "updateProfileApi: $e")
            }
        }
    }

    // post subscribe or un subscribe
    fun postSubscribeApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("postSubscribeApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postSubscribeApi: $e")
            }
        }
    }
}