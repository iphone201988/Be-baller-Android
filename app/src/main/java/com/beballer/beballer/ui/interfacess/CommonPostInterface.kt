package com.beballer.beballer.ui.interfacess

interface CommonPostInterface {
    fun likeCount(likeCount:Int,currentLikeCount:Int,position:Int)
    fun commentCount(commentCount:Int,position:Int)
}