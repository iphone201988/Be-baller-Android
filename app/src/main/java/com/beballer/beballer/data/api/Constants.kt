package com.beballer.beballer.data.api


object Constants {
    const val BASE_URL = "http://98.86.12.144:9000/api/v1/"
    const val IMAGE_URL = "https://d3dnkdkyhsfbca.cloudfront.net/"

    var welcomeDialog = 0
    var userType = 1

    /**************** API LIST *****************/
    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    const val MOBILE_LOGIN = "user/login"
    const val ON_BOARDING = "player/set-onboard-analytics"
    const val PLAYER_TEAM = "player/get-all-teams"
    const val CREATE_PROFILE = "player/create-player-profile"
    const val USER_PROFILE = "user/profile"
    const val USER_NAME_UNIQUE = "user/is-username-unique"
    const val USER_GET_POST = "post/get-posts"
    const val USER_POST_LIKE = "post/like-post"
    const val USER_POST_LIKE_COMMENT = "post/like-comment"
    const val USER_GET_POST_COMMENT = "post/get-post-comments"
    const val USER_POST_COMMENT = "post/comment-on-post"
    const val USER_CREATE_POST = "post/create-post"
    const val USER_SUBSCRIBE = "user/subscribe"
    const val REPORT_OR_DELETE_POST = "post/report-or-delete-post"
    const val GET_FOLLOWERS_FOLLOWING = "user/get-followers-following"
    const val GET_SUGGESTED_PLAYERS = "player/get-suggested-players"
    const val POST_PUBLISHER_ID = "post/get-post-by-puplisher-id/"
    const val PLAYER_POST = "post/get-post"
    const val USER_GET_USER_BY_ID = "user/get-user-by-id"
    const val GET_COURTS = "court/get-courts"
    const val GET_COURTS_BY_ID = "court/get-court/"
    const val CREATE_COURT = "court/new"

}