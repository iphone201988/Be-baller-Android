package com.beballer.beballer.ui.player.dash_board.find.player_profile.posts

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
class ProfilePostsFragmentVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
    // get post Publisher Id
    fun postPublisherId(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("postPublisherId", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postPublisherId: $e")
            }
        }
    }
}