package com.beballer.beballer.ui.player.dash_board.social

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
class SocialsFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    val commonObserver = SingleRequestEvent<JsonObject>()
    val commonObserverSub = SingleRequestEvent<JsonObject>()
     // get post
    fun getPostApi(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getPostApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getPostApi: $e")
            }
        }
    }

    // post like
    fun postLikeApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.commonParamPostApi(url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("postLikeApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postLikeApi: $e")
            }
        }
    }


    // post subscribe or un subscribe
    fun postLikeSubApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserverSub.postValue(Resource.loading(null))
            try {
                apiHelper.commonParamPostApi(url).let {
                    if (it.isSuccessful) {
                        commonObserverSub.postValue(Resource.success("postLikeSubApi", it.body()))
                    } else commonObserverSub.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postLikeSubApi: $e")
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

    // report or delete post
    fun reportOrDeletePostApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("reportOrDeletePostApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "reportOrDeletePostApi: $e")
            }
        }
    }



    // get Sub post
    fun getPostSubApi(url: String, data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserverSub.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url, data).let {
                    if (it.isSuccessful) {
                        commonObserverSub.postValue(Resource.success("getPostSubApi", it.body()))
                    } else commonObserverSub.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getPostSubApi: $e")
            }
        }
    }


    // report or delete post
    fun postSubscribeSubApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserverSub.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserverSub.postValue(Resource.success("postSubscribeSubApi", it.body()))
                    } else commonObserverSub.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postSubscribeSubApi: $e")
            }
        }
    }


    // report or delete post
    fun reportOrDeletePostSubApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserverSub.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(request,url).let {
                    if (it.isSuccessful) {
                        commonObserverSub.postValue(Resource.success("reportOrDeletePostSubApi", it.body()))
                    } else commonObserverSub.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "reportOrDeletePostSubApi: $e")
            }
        }
    }

}