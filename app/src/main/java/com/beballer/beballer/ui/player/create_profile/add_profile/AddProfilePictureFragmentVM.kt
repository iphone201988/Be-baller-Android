package com.beballer.beballer.ui.player.create_profile.add_profile

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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddProfilePictureFragmentVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()
    fun createProfileApi(url: String, map: HashMap<String, RequestBody>, part: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipart(url,map,part).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("createProfileApi", it.body()))
                    } else {
                        commonObserver.postValue(Resource.error(handleErrorResponse(it.errorBody(),it.code()), null))
                    }
                }
            } catch (e: Exception) {
                Log.d("error", "createProfileApi: $e")
            }
        }
    }
}