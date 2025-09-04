package com.beballer.beballer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** common response ***/
data class CommonResponse(
    val message: String?, val success: Boolean?
)

data class PostCommentResponse(
    val commentId: String?,
    val message: String?,
    val success: Boolean?
)

/** login and signup response ***/
data class LoginApiResponse(val data: Data?, val message: String?, val success: Boolean?)

data class Data(val token: String, val user: User?)

data class User(
    val _id: String?,
    val birthDate: String?,
    val city: String?,
    val country: String?,
    val favoriteProTeam: FavoriteProTeam,
    val feedCountry: String?,
    val firstName: String?,
    val followersCount: Int?,
    val followingCount: Int?,
    val height: String?,
    val id: String?,
    val isOnboardAnalyticsDone: Boolean?,
    val isProfileCompleted: Boolean?,
    val lastName: String?,
    val playPositionId: String?,
    val position: String?,
    val profilePicture: String?,
    val progression: List<Any>,
    val rank: Int?,
    val rankRegion: Int?,
    val rankSector: Int?,
    val recutersViewed: String?,
    val referralCode: String?,
    val region: String?,
    val score: Int?,
    val sector: String?,
    val totalProgression: Int?,
    val verified: Boolean?,
    val lat: Double,
    val long: Double,
    val setProfilePopup: Boolean,
    val setSettingsPopup: Boolean,

    )

data class FavoriteProTeam(
    val ref: Ref?
)

data class Ref(
    val id: String?
)

/** user profile **/
data class UserProfile(
    val `data`: ProfileData?, val message: String?, val success: Boolean?
)

data class ProfileData(
    val user: ProfileDataUser?
)

data class ProfileDataUser(
    val _id: String?,
    val badgeUri: String?,
    val birthDate: String?,
    val city: String?,
    val country: String?,
    val countryCode: String?,
    val favoriteProTeam: UserFavoriteProTeam?,
    val feedCountry: String?,
    val firstName: String?,
    val followersCount: Int?,
    val followingCount: Int?,
    val gender: String?,
    val height: Int?,
    val id: String?,
    val isOnboardAnalyticsDone: Boolean?,
    val isProfileCompleted: Boolean?,
    val lastName: String?,
    val lat: Double?,
    val long: Double?,
    val playPositionId: Int?,
    val position: String?,
    val profilePicture: String?,
    val progression: List<Any?>,
    val rank: Int?,
    val rankRegion: Int?,
    val rankSector: Int?,
    val recutersViewed: String?,
    val referralCode: String?,
    val region: String?,
    val score: Int?,
    val sector: String?,
    val setProfilePopup: Boolean?,
    val setSettingsPopup: Boolean?,
    val totalProgression: Int?,
    val username: String?,
    val verified: Boolean,
    val hasOrganizerAccount: Boolean?,
    val isAdmin: Boolean?,
    val isDeleted: Boolean?,

)

data class UserFavoriteProTeam(
    val _id: String?, val id: String?, val imageURL: String?, val name: String?
)

/** get player team team **/
data class GetPlayerTeamResponse(
    val `data`: List<PlayerTeamData?>, val message: String?, val success: Boolean?
)

data class PlayerTeamData(
    val _id: String?,
    val coordinates: List<Double?>,
    val id: String?,
    val imageURL: String?,
    val name: String?,
    val type: String?,
    val url: String?,
    var check: Boolean = false
)

/** get user post response **/
data class GetUserPostResponse(
    val data: List<GetUserPostData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
)

@Parcelize
data class GetUserPostData(
    val __v: Int?,
    val _id: String?,
    var commentCount: Int?,
    var contentType: String?,
    val country: String?,
    var currentUserLikeCount: Int?,
    val date: String?,
    val description: String?,
    val event: GetUserPostEvent?,
    val id: String?,
    val image: String?,
    val isCommentedByCurrentUser: Boolean?,
    val isDeleted: Boolean?,
    val isFeed: Boolean?,
    val isLikedByCurrentUser: Boolean?,
    val isSharedByCurrentUser: Boolean?,
    var isSubscribed: Boolean?,
    var likesCount: Int?,
    val postContentHeight: String?,
    val postContentWidth: String?,
    val publisherData: PublisherData?,
    val score: Int?,
    val video: String?,
    val court: UserPostCourt?,
    val game: Game?,
    val role: Int?,
    val proGame: String?,
    val stability: Int?
    ): Parcelable
@Parcelize
data class Pagination(val currentPage: Int?, val totalPages: Int?): Parcelable
@Parcelize
data class GetUserPostEvent(
    val _id: String?,
    val address: String?,
    val city: String?,
    val coordinates: List<Double?>?,
    val country: String?,
    val createdAt: String?,
    val discountCode: String?,
    val endDate: String?,
    val formats: String?,
    val geohash: String?,
    val hasCategories: Boolean?,
    val hasSponsors: Boolean?,
    val isVisible: Boolean?,
    val name: String?,
    val organizersCode: String?,
    val paymentStatus: Int?,
    val referees: List<String?>?,
    val refereesCode: String?,
    val region: String?,
    val shareLink: String?,
    val spectatorsCode: String?,
    val startDate: String?,
    val type: String?

): Parcelable
@Parcelize
data class PublisherData(
    val _id: String?,
    val city: String?,
    val coordinates: List<Double?>?,
    val country: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val profilePicture: String?,
    val username: String?,
    val verified: Boolean?



): Parcelable
@Parcelize
data class UserPostCourt(
    val _id: String?,
    val accessibility: String?,
    val address: String?,
    val areDimensionsStandard: Boolean?,
    val boardType: String?,
    val city: String?,
    val coordinates: List<Double?>?,
    val country: String?,
    val createdAt: String?,
    val description: String?,
    val floorType: String?,
    val geohash: String?,
    val grade: Double?,
    val hasWaterPoint: Boolean?,
    val hoopsCount: Int?,
    val id: String?,
    val isWomanFriendly: Boolean?,
    val name: String?,
    val netType: String?,
    val photos: List<String?>?,
    val region: String?,
    val zipCode: String?
): Parcelable



@Parcelize
data class Game(
    val _id: String?,
    val createdAt: String?,
    val date: String?,
    val `field`: Field?,
    val hasAcceptedInvitationReferee: Boolean?,
    val hasAcceptedInvitationTeam1: List<Boolean>?,
    val hasAcceptedInvitationTeam2: List<Boolean>?,
    val id: String?,
    val isAutoRefereeing: Boolean?,
    val mode: Int?,
    val organizer: Organizer?,
    val scoreTeam1: Int?,
    val scoreTeam2: Int?,
    val startDate: String?,
    val status: String?,
    val team1Players: List<Team1Player>?,
    val team1ScoreTeam1: Int?,
    val team1ScoreTeam2: Int?,
    val team2Players: List<Team1Player>?,
    val team2ScoreTeam1: Int?,
    val team2ScoreTeam2: Int?,
    val teamToValidate: Int?,
    val type: String?,
    val visible: Boolean?
): Parcelable

@Parcelize
data class Field(
    val _id: String?,
    val city: String?,
    val country: String?,
    val id: String?,
    val name: String?,
    val postalCode: String?,
    val region: String?
): Parcelable
@Parcelize
data class Organizer(
    val ref: GameRef?
): Parcelable
@Parcelize
data class Team1Player(
    val accepted: Boolean?,
    val collectionName: String?,
    val id: String?
): Parcelable
@Parcelize
data class GameRef(
    val collectionName: String?,
    val id: String?
): Parcelable

/** post comment response **/
data class GetPostCommentResponse(
    val `data`: List<PostCommentData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
)

data class PostCommentData(
    val _id: String?,
    val comment: String?,
    val createdAt: String?,
    var currentUserLikeCount: Int?,
    val id: String?,
    val isDeleted: Boolean?,
    var likeCount: Int?,
    val postId: String?,
    val publisherData: CommentData?
)

data class CommentData(
    val _id: String?,
    val firstName: String?,
    val id: String?,
    val lastName: Any?,
    val profilePicture: String?,
    val username: String?,
    val verified: Boolean?
)

/** following response **/
data class FollowingResponse(
    val `data`: FollowingData?,
    val message: String?,
    val success: Boolean?
)

data class FollowingData(
    val followingUser: List<FollowingUser?>?,
    val pagination: Pagination?
)

data class FollowingUser(
    val _id: String?,
    val badge: String?,
    val badgeUri: String?,
    val city: String?,
    val country: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val profilePicture: String?,
    val score: Int?,
    val username: String?

)

/** following response **/
data class FollowersResponse(
    val `data`: FollowersData?,
    val message: String?,
    val success: Boolean?
)

data class FollowersData(
    val followerUser: List<FollowerUser?>?,
    val pagination: Pagination?
)

data class FollowerUser(
    val _id: String?,
    val badge: String?,
    val badgeUri: Any?,
    val city: String?,
    val country: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val profilePicture: String?,
    val score: Int?,
    val username: String?
)

/** get suggested response **/

data class GetSuggestedResponse(
    val `data`: SuggestedData?,
    val message: String?,
    val success: Boolean?
)

data class SuggestedData(
    val currentPage: Int?,
    val totalCount: Int?,
    val totalPages: Int?,
    val users: List<SuggestedUser?>?
)

data class SuggestedUser(
    val _id: String?,
    val city: String?,
    val country: String?,
    val distance: Double?,
    val firstName: String?,
    val id: String?,
    var isSubscribed: Boolean?,
    val lastName: String?,
    val lat: Double?,
    val long: Double?,
    val profilePicture: String?,
    val score: Int?,
    val username: String?,
)

/** get player profile response **/
@Parcelize
data class PlayerProfileResponse(
    val `data`: List<PlayerData?>?,
    val message: String?,
    val pagination: Pagination?,
    val success: Boolean?
): Parcelable
@Parcelize
data class PlayerData(
    val _id: String?,
    val contentType: String?,
    val description: String?,
    val id: String?,
    val image: String?,
    val postContentHeight: String?,
    val postContentWidth: String?,
    val video: String?
): Parcelable

/** get player post by id  response **/
data class PlayerPostBYIdResponse(
    val `data`: PlayerPostData?,
    val message: String?,
    val success: Boolean?
)

data class PlayerPostData(
    val __v: Int?,
    val _id: String?,
    val commentCount: Int?,
    val contentType: String?,
    val currentUserLikeCount: Int?,
    val date: String?,
    val description: String?,
    val id: String?,
    val image: String?,
    val isCommentedByCurrentUser: Boolean?,
    val isDeleted: Boolean?,
    val isFeed: Boolean?,
    val isLikedByCurrentUser: Boolean?,
    val isReported: Boolean?,
    val isSharedByCurrentUser: Boolean?,
    val isSubscribed: Boolean?,
    val likesCount: Int?,
    val postContentHeight: String?,
    val postContentWidth: String?,
    val publisherData: PlayerPublisherData?,
    val score: Int?,
    val video: String?
)

data class PlayerPublisherData(
    val _id: String?,
    val city: String?,
    val coordinates: List<Double?>?,
    val country: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val profilePicture: String?,
    val username: String?,
    val verified: Boolean?
)


/** Player Profile  response **/
data class PlayerProfileByIdResponse(
    val `data`: ProfileByIdData?,
    val message: String?,
    val success: Boolean?
)

data class ProfileByIdData(
    val user: ProfileByUser?
)

data class ProfileByUser(
    val _id: String?,
    val badgeUri: Any?,
    val birthDate: String?,
    val city: String?,
    val country: String?,
    val countryCode: String?,
    val favoriteProTeam: ProfileByFavoriteProTeam?,
    val feedCountry: Any?,
    val firstName: String?,
    val followersCount: Int?,
    val followingCount: Int?,
    val gender: String?,
    val hasOrganizerAccount: Boolean?,
    val height: Int?,
    val id: String?,
    val isAdmin: Boolean?,
    val isDeleted: Boolean?,
    val isOnboardAnalyticsDone: Boolean?,
    val isProfileCompleted: Boolean?,
    val isSubscribed: Boolean?,
    val lastName: String?,
    val lat: Double?,
    val long: Double?,
    val playPositionId: Int?,
    val position: String?,
    val profileDescription: Any?,
    val profilePicture: String?,
    val progression: List<Any?>?,
    val rank: Int?,
    val rankRegion: Int?,
    val rankSector: Int?,
    val recutersViewed: String?,
    val referralCode: Any?,
    val region: Any?,
    val score: Int?,
    val sector: Any?,
    val setProfilePopup: Boolean?,
    val setSettingsPopup: Boolean?,
    val totalProgression: Int?,
    val username: String?,
    val verified: Boolean?
)

data class  ProfileByFavoriteProTeam(
    val _id: String?,
    val id: String?,
    val imageURL: String?,
    val name: String?
)


/** Get Court Api response **/
data class GetCourtApiResponse(
    val courts: List<GetCourtData?>?,
    val message: String?,
    val pagination: CourtPagination?,
    val success: Boolean?
)

data class GetCourtData(
    val _id: String?,
    val address: String?,
    val createdAt: String?,
    val distance: Double?,
    val grade: Double?,
    val hoopsCount: Int?,
    val id: String?,
    val lat: Double?,
    val long: Double?,
    val name: String?,
    val photos: List<String?>?
)

data class CourtPagination(
    val currentPage: Int?,
    val totalCount: Int?,
    val totalPages: Int?
)


/** Get Court Api response **/
data class GetCourtByIdResponse(
    val `data`: DataById?,
    val message: String?,
    val success: Boolean?
)

data class DataById(
    val court: CourtDataById?
)

data class CourtDataById(
    val _id: String?,
    val accessibility: String?,
    val address: String?,
    val areDimensionsStandard: Boolean?,
    val boardType: String?,
    val city: String?,
    val country: String?,
    val createdAt: String?,
    val description: String?,
    val distance: Double?,
    val floorType: String?,
    val grade: Double?,
    val hasWaterPoint: Boolean?,
    val hoopsCount: Int?,
    val id: String?,
    val isWomanFriendly: Boolean?,
    val lat: Double?,
    val level: Any?,
    val long: Double?,
    val name: String?,
    val netType: String?,
    val photos: List<String?>?,
    val userInformation: UserInformation?,
    val zipCode: String?
)

data class UserInformation(
    val _id: String?,
    val firstName: String?,
    val id: String?,
    val lastName: String?
)