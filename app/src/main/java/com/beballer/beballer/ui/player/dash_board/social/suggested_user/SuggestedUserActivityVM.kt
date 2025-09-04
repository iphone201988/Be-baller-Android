package com.beballer.beballer.ui.player.dash_board.social.suggested_user

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
import javax.inject.Inject

@HiltViewModel
class SuggestedUserActivityVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    // get suggested players
    fun  getSuggestedPlayers(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getSuggestedPlayers", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getSuggestedPlayers: $e")
            }
        }
    }

    // players subscribe or un subscribe
    fun playerSubscribeApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("playerSubscribeApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "playerSubscribeApi: $e")
            }
        }
    }

}