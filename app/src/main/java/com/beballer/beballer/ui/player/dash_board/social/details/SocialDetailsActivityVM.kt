package com.beballer.beballer.ui.player.dash_board.social.details

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
class SocialDetailsActivityVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){
    val commonObserver = SingleRequestEvent<JsonObject>()
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

    fun postCommentLikeApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.commonParamPostApi(url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("postCommentLikeApi", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postCommentLikeApi: $e")
            }
        }
    }

    fun getPostComment(url: String,data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url,data).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("getPostComment", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "getPostComment: $e")
            }
        }
    }

    fun postComment(url: String,data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBodyWithToken(data,url).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("postComment", it.body()))
                    } else commonObserver.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("error", "postComment: $e")
            }
        }
    }
}