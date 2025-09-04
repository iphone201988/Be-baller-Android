package com.beballer.beballer.ui.player.dash_board.find.game.add_photo

import android.util.Log
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.data.api.ApiHelper
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.utils.Resource
import com.beballer.beballer.utils.event.SingleRequestEvent
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddPhotoFragmentVM @Inject constructor(private val apiHelper: ApiHelper):BaseViewModel(){

    val commonObserver = SingleRequestEvent<JsonObject>()
    fun createCourtApi(map: HashMap<String, RequestBody>,courtPicFirst: MultipartBody.Part?,
                       courtPicSecond: MultipartBody.Part?,
                       courtPicThird: MultipartBody.Part?,) {
        CoroutineScope(Dispatchers.IO).launch {
            commonObserver.postValue(Resource.loading(null))
            try {
                apiHelper.apiForMultipartCourt(Constants.CREATE_COURT,map,courtPicFirst,courtPicSecond,courtPicThird).let {
                    if (it.isSuccessful) {
                        commonObserver.postValue(Resource.success("createCourtApi", it.body()))
                    } else {
                        commonObserver.postValue(Resource.error(handleErrorResponse(it.errorBody(),it.code()), null))
                    }
                }
            } catch (e: Exception) {
                Log.d("error", "createCourtApi: $e")
            }
        }
    }
}