package com.beballer.beballer.ui.player.auth.helper

import android.content.Context
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialLoginHelper @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {

    fun loginWithGoogle(idToken: String, onSuccess: (FirebaseUser) -> Unit, onFailure: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) onSuccess(it.result!!.user!!) else onFailure()
        }
    }

    fun loginWithFacebook(accessToken: AccessToken, onSuccess: (FirebaseUser) -> Unit, onFailure: () -> Unit) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) onSuccess(it.result!!.user!!) else onFailure()
        }
    }


    fun signOut(onGoogleSignedOut: () -> Unit = {}) {
        // Sign out from Firebase
        auth.signOut()

        // Sign out from Facebook
        LoginManager.getInstance().logOut()

        // Sign out from Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            onGoogleSignedOut()
        }
    }
}
