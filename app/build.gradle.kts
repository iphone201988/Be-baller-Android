plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin1kept)
    alias(libs.plugins.google.services)
    id ("kotlin-parcelize")

}

android {
    namespace = "com.beballer.beballer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.beballer.beballer"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.dagger)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.gson)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.lottie)
    implementation(libs.converter.gson)


    // Country Picker
    implementation(libs.ccp)
    // map
    implementation(libs.android.maps.utils)
    implementation(libs.play.services.location.v2101)
    implementation(libs.play.services.maps)
    implementation (libs.places)

    // exo player
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    // dot indicator
    implementation(libs.viewpagerindicator)

    // SwipeItem
    implementation(libs.swipelayout)

    // facebook
    implementation("com.facebook.android:facebook-login:18.0.3")
    implementation("com.facebook.android:facebook-android-sdk:18.0.3")



    //  image picker
    implementation(libs.imagepicker)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")

    // image crop
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("id.zelory:compressor:3.0.1")

    implementation ("com.arthenica:smart-exception-java:0.1.1")
    implementation(files("libs/ffmpeg-kit.aar"))

    // refresh layout
    implementation ("com.airbnb.android:lottie:5.2.0")
    implementation ("com.github.nabil6391:LottieSwipeRefreshLayout:1.0.0")

    // ratings
    implementation ("com.github.bilalnasir9:CustomRatingBar:1.0.0")

}