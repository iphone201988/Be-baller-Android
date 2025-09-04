package com.beballer.beballer.ui.organizers.login

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
class OrganizersLoginFragmentVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    fun commonLoginAPi(request: HashMap<String, Any>, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("commonLoginAPi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(),it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "commonLoginAPi: $e")
            }
        }
    }
}