package com.beballer.beballer.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.beballer.beballer.R
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.CourtDataById
import com.beballer.beballer.data.model.FollowerUser
import com.beballer.beballer.data.model.FollowingUser
import com.beballer.beballer.data.model.SuggestedUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

object BindingUtils {

    var lat: Double = 0.0
    var long: Double = 0.0

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }


    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("dfsddfsd", "parseJson: $e")
            null
        }
    }


    fun formatBirthDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "-"
        }
    }


    fun calculateAgeFromIsoLegacy(dateString: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val birthDate = sdf.parse(dateString) ?: return 0
        val birthCalendar = Calendar.getInstance().apply { time = birthDate }

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        // Adjust if birthday hasn't occurred yet this year
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }


    fun convertCmToFeetInchesFormatted(cm: Int): String {
        val totalInches = (cm / 2.54).toInt()
        val feet = totalInches / 12
        val inches = totalInches % 12
        return "${feet}'${inches}\" ft"
    }


    @BindingAdapter("setImagePostUrl")
    @JvmStatic
    fun setImagePostUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            if (url.isNotEmpty()) {
                Glide.with(image.context).load(Constants.IMAGE_URL + url)
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.iv_event)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            } else {
                image.setImageResource(R.drawable.iv_event)
            }
        } else {
            image.setImageResource(R.drawable.iv_event)
        }
    }


    @BindingAdapter("setImageCourtUrl")
    @JvmStatic
    fun setImageCourtUrl(image: ShapeableImageView, url: List<String>?) {
            if (url?.isNotEmpty()==true) {
                Glide.with(image.context).load(Constants.IMAGE_URL + url[0])
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.iv_event)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            } else {
                image.setImageResource(R.drawable.iv_event)
            }

    }

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            if (url.isNotEmpty()) {
                Glide.with(image.context).load(Constants.IMAGE_URL + url)
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.ic_round_account_circle_40)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            } else {
                image.setImageResource(R.drawable.ic_round_account_circle_40)
            }
        } else {
            image.setImageResource(R.drawable.ic_round_account_circle_40)
        }
    }


    fun formattedDistance(
        eventLat: Double, eventLon: Double, currentLat: Double, currentLon: Double
    ): String {
        val eventLocation = Location("event").apply {
            latitude = eventLat
            longitude = eventLon
        }

        val currentLocation = Location("current").apply {
            latitude = currentLat
            longitude = currentLon
        }

        val distanceInMeters = currentLocation.distanceTo(eventLocation)

        return if (distanceInMeters < 1000) {
            "${distanceInMeters.roundToInt()} m"
        } else {
            val distanceInKm = distanceInMeters / 1000.0
            "${DecimalFormat("#.#").format(distanceInKm)} km"
        }
    }

    @BindingAdapter("setTeamFromUrl")
    @JvmStatic
    fun setTeamFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            if (url.isNotEmpty()) {
                Glide.with(image.context).load(Constants.IMAGE_URL + url)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            }
        }
    }

    @BindingAdapter("setRatings")
    @JvmStatic
    fun setRatings(ratings: com.github.bilalnasir9.library.ratingbar.CustomRatingBar, url: Double?) {
        if (url != null) {
            ratings.rating = url.toFloat()
        }
    }

    @BindingAdapter("setTime")
    @JvmStatic
    fun setTime(tvHour: AppCompatTextView, time: String?) {
            if (time?.isNotEmpty() == true) {
                val rawDate = time
                if (rawDate.isNotEmpty() == true) {
                    val date = convertToDate(rawDate)
                    val relative = DateHelper.formatRelativeDate(date)
                    tvHour.text = relative
                }
            }

    }

    @BindingAdapter("setImageTeam")
    @JvmStatic
    fun setImageTeam(image: AppCompatImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            image.setImageResource(R.drawable.round_team_24)
        } else {
            Glide.with(image.context).load(Constants.IMAGE_URL + url)
                .placeholder(R.drawable.progress_animation_small).error(R.drawable.round_team_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
        }


    }

    @JvmStatic
    @BindingAdapter("setIconTint")
    fun setIconTint(imageView: AppCompatImageView, colorString: String?) {
        if (colorString != null) {
            if (colorString.isNotEmpty()) {
                when (colorString) {
                    "1" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.first_color))
                    "2" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.second_color))
                    "3" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.third_color))
                    "4" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.four_color))
                    "5" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.five_color))
                    "6" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.six_color))
                    "7" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.seven_color))
                    "8" -> imageView.setColorFilter(imageView.context.resources.getColor(R.color.eight_color))
                }
            }
        }
    }


    @BindingAdapter("setTextEmptyCheck")
    @JvmStatic
    fun setTextEmptyCheck(textView: AppCompatTextView, name: String?) {
            if (name?.isNotEmpty() == true) {
                textView.text = name
            }else{
                textView.text = ""
            }

    }

    @BindingAdapter("setName")
    @JvmStatic
    fun setName(textView: AppCompatTextView, user: SuggestedUser?) {
        if (user != null) {
            val firstName: String? = user.firstName?.takeIf { it.isNotBlank() }
            val lastName: String? = user.lastName?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "-" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }

    @BindingAdapter("setNameCourt")
    @JvmStatic
    fun setNameCourt(textView: AppCompatTextView, user: CourtDataById?) {
        if (user != null) {
            val firstName: String? = user.userInformation?.firstName?.takeIf { it.isNotBlank() }
            val lastName: String? = user.userInformation?.lastName?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "-" }

            val text = "By @$fullName"
            val spannable = SpannableString(text)

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.black)),
                0,
                2,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(textView.context, R.color.blue)),
                3,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                UnderlineSpan(),
                3,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            textView.text = spannable
        } else {
            textView.text = ""
        }
    }



    @BindingAdapter("setNameFollowingUser")
    @JvmStatic
    fun setNameFollowingUser(textView: AppCompatTextView, user: FollowingUser?) {
        if (user != null) {
            val firstName: String? = user.firstName?.takeIf { it.isNotBlank() }
            val lastName: String? = user.lastName?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "-" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }

    @BindingAdapter("setNameFollowerUser")
    @JvmStatic
    fun setNameFollowerUser(textView: AppCompatTextView, user: FollowerUser?) {
        if (user != null) {
            val firstName: String? = user.firstName?.takeIf { it.isNotBlank() }
            val lastName: String? = user.lastName?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "-" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }

    @BindingAdapter("setCountryNameFollowerUser")
    @JvmStatic
    fun setCountryNameFollowerUser(textView: AppCompatTextView, user: FollowerUser?) {
        if (user != null) {
            val firstName: String? = user.country?.takeIf { it.isNotBlank() }
            val lastName: String? = user.city?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }


    @BindingAdapter("setCountryNameFollowingUser")
    @JvmStatic
    fun setCountryNameFollowingUser(textView: AppCompatTextView, user: FollowingUser?) {
        if (user != null) {
            val firstName: String? = user.country?.takeIf { it.isNotBlank() }
            val lastName: String? = user.city?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }

    @BindingAdapter("setCountryName")
    @JvmStatic
    fun setCountryName(textView: AppCompatTextView, user: SuggestedUser?) {
        if (user != null) {
            val firstName: String? = user.country?.takeIf { it.isNotBlank() }
            val lastName: String? = user.city?.takeIf { it.isNotBlank() }

            val fullName: String = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .ifBlank { "" }

            textView.text = fullName
        } else {
            textView.text = ""
        }
    }


    @BindingAdapter("setImageInt")
    @JvmStatic
    fun setImageInt(image: AppCompatImageView, url: Int?) {
        if (url != null) {
            image.setImageResource(url)
        }
    }

    @BindingAdapter("likeCountSet")
    @JvmStatic
    fun likeCountSet(image: AppCompatImageView, item: Int?) {
        if (item != null) {
            when (item) {
                0 -> image.setImageResource(R.drawable.ic_like_0_24)
                1 -> image.setImageResource(R.drawable.like_icon1)
                2 -> image.setImageResource(R.drawable.like_icon2)
                3 -> image.setImageResource(R.drawable.like_icon3)
            }
        }
    }



    @BindingAdapter("setProgress")
    @JvmStatic
    fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }

    @BindingAdapter("setCardViewColor")
    @JvmStatic
    fun setCardViewColor(image: CardView, url: Int?) {
        if (url != 0) {
            when (url) {
                1 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.courts_light_color))
                2 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.workouts_light_color))
                3 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.games_light_color))
                4 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.ticketing_light_color))
                5 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.tournaments_light_color))
                6 -> image.setCardBackgroundColor(image.context.resources.getColor(R.color.camps_light_color))
            }
        }
    }

    fun navigateWithSlide(navController: NavController, destinationId: Int, bundle: Bundle?) {
        val navOptions = NavOptions.Builder().build()
        navController.navigate(destinationId, bundle, navOptions)
    }

    /** full screen status bar style change **/
    @SuppressLint("ObsoleteSdkInt")
    fun statusBarStyle(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    /** set status bar item color change **/
    fun statusBarTextColor(activity: Activity, isDark: Boolean = false) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        WindowInsetsControllerCompat(
            activity.window, activity.window.decorView
        ).isAppearanceLightStatusBars = isDark
    }

    fun formatDateTime(dateString: String): Pair<String, String> {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)!!
        val dateFormat = SimpleDateFormat("EEEE dd.MM.yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        timeFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date) to timeFormat.format(date)
    }


    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun convertToDate(from: String?): Date {
        if (from == null) return Date()
        return try {
            isoFormatter.parse(from) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    object DateHelper {
        fun formatRelativeDate(date: Date): String {
            return DateUtils.getRelativeTimeSpanString(
                date.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()
        }
    }


}

