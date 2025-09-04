package com.beballer.beballer.ui.player.auth.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.event.SingleRequestEvent
import com.beballer.beballer.data.api.ApiHelper
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmentVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    fun commonLoginAPi(request: HashMap<String, Any>, url: String) {
        viewModelScope.launch {
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


