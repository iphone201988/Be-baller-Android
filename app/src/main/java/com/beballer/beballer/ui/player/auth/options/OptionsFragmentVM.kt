package com.beballer.beballer.ui.player.auth.options

import android.util.Log
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.event.SingleRequestEvent
import com.beballer.beballer.data.api.ApiHelper
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsFragmentVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    fun onBoardingAPi(request: HashMap<String, Any>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiPutForRawBody(url,request).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("onBoardingAPi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(),it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "onBoardingAPi: $e")
            }
        }
    }


    fun getProfileApi(url: String,data :HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url,data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getProfileApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(),it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getProfileApi: $e")
            }
        }
    }

}