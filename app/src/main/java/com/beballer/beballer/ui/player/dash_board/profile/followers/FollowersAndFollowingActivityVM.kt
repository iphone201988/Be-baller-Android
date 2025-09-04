package com.beballer.beballer.ui.player.dash_board.profile.followers

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
class FollowersAndFollowingActivityVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()

    // get Followings Following Api
    fun getFollowingsFollowingApi(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getFollowingsFollowingApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getFollowingsFollowingApi: $e")
            }
        }
    }

    // get Followers Following Api
    fun getFollowersFollowingApi(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getFollowersFollowingApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getFollowersFollowingApi: $e")
            }
        }
    }


}