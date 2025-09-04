package com.beballer.beballer.ui

import com.beballer.beballer.data.model.GetUserPostData
import com.beballer.beballer.data.model.MpvModel

sealed class FeedItem {
    data class MvpSection(val items: List<MpvModel>) : FeedItem()
    data class Post(val post: GetUserPostData) : FeedItem()
    object Loader : FeedItem()
}

