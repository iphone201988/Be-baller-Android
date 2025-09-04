package com.beballer.beballer.ui.player.create_profile

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
class CreateProfileActivityVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    fun userNameCheck(request:HashMap<String, Any>,url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("userNameCheck", it.body()))
                    } else {
                        commonObserver.postValue(Resource.error(handleErrorResponse(it.errorBody(),it.code()), null))
                    }
                }
            } catch (e: Exception) {
                Log.d("error", "userNameCheck: $e")
            }
        }
    }
}