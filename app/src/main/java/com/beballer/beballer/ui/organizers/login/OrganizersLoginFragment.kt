package com.beballer.beballer.ui.organizers.login

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.utils.BindingUtils
import com.beballer.beballer.utils.Status
import com.beballer.beballer.data.api.Constants
import com.beballer.beballer.data.model.LoginApiResponse
import com.beballer.beballer.databinding.FragmentOrganizersLoginBinding
import com.beballer.beballer.ui.player.auth.login.OtpVerifyBottomSheet
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OrganizersLoginFragment : BaseFragment<FragmentOrganizersLoginBinding>(),
    OtpVerifyBottomSheet.OtpListener {

    private val viewModel: OrganizersLoginFragmentVM by viewModels()

    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var phoneNumber = ""
    private lateinit var bottomSheet: OtpVerifyBottomSheet
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var loginManager: LoginManager? = null
    private var callbackManager: CallbackManager? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_organizers_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initOnClick()
        // initialize
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        // observer
        initObserver()
    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // next button click
                R.id.btnNext -> {
                    if (binding.etMobile.text.toString().trim()
                            .isNotEmpty() && binding.etMobile.text.toString().trim().length >= 10
                    ) {
                        phoneNumber =
                            "+" + binding.countryCode.selectedCountryCode + binding.etMobile.text.toString()
                                .trim()
                        startPhoneNumberVerification(phoneNumber)
                    } else {
                        showInfoToast("Please enter mobile number")
                    }
                }
                // google button click
                R.id.clGoogle -> {
                    signIn()
                }
                // facebook button click
                R.id.clFacebook -> {
                    facebookLogin()
                }
            }
        }

        // text watcher click
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true && s.length >= 10) {
                    binding.buttonCheck = true
                }
            }
        })
    }

    /**  api response observer  **/
    private fun initObserver() {
        viewModel.commonObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "commonLoginAPi" -> {
                            try {
                                val myDataModel: LoginApiResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), R.id.fragmentOrganizersProfile, null
                                    )
                                }

                            } catch (e: Exception) {
                                Log.e("error", "commonLoginAPi: $e")
                            } finally {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }


    /** facebook login **/
    private fun facebookLogin() {
        FacebookSdk.sdkInitialize(requireActivity())
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.e("gfdgfd", "onSuccess: $result")
                fetchFacebookUserData(result.accessToken)
            }

            override fun onCancel() {
                Log.e("gfdgfd", "onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.e("gfdgfd", "onError: ${error.message}")
            }
        })
        loginManager!!.logOut()
        loginManager!!.logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    // Helper function
    private fun fetchFacebookUserData(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        commonLogin(user.uid)
                    }
                } else {
                    showErrorToast("Login failed")
                }
            }
    }


    /*** common login api call ***/
    private fun commonLogin(uid: String) {
        val data = HashMap<String, Any>()
        data["id"] = uid
        data["longitude"] = "76.7794"
        data["type"] = "player"
        data["latitude"] = "30.7333"
        data["deviceToken"] = "112121212"
        data["deviceType"] = 2
        viewModel.commonLoginAPi(data, Constants.MOBILE_LOGIN)
        showInfoToast("Login Success")

    }


    /** google sign in **/
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }


    /** google launcher **/
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    /**  handel signIn results  **/
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            updateUI(null)
        }
    }

    /** api call social login **/
    private fun updateUI(account: GoogleSignInAccount?) {
        account?.idToken?.let { firebaseAuthWithGoogle(it) }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    if (user != null) {
                        commonLogin(user.uid)
                    }
                } else {
                    showErrorToast("Login failed")
                }
            }
    }

    /**  phone number verification **/
    private fun startPhoneNumberVerification(phone: String) {
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext())
        }
        val options = PhoneAuthOptions.newBuilder(Firebase.auth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(requireActivity())
            .setCallbacks(callbacks).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /** firebase call back **/
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
            showInfoToast("onVerificationCompleted")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            showInfoToast(e.localizedMessage)

        }

        override fun onCodeSent(
            verificationId: String, token: PhoneAuthProvider.ForceResendingToken
        ) {
            showInfoToast("Code Send")
            storedVerificationId = verificationId
            resendToken = token
            // bottom sheet
            bottomSheet = OtpVerifyBottomSheet(this@OrganizersLoginFragment)
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    /** otp enter call function ***/
    override fun onOtpEntered(otp: String) {
        if (storedVerificationId.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            showErrorToast("VerificationToken Invalid")
        }
        bottomSheet.dismiss()
    }

    /** success phone auth credential ***/
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result?.user
                if (user != null) {
                    commonLogin(user.uid)
                }
            } else {
                showErrorToast("Login failed")
            }
        }
    }

}
