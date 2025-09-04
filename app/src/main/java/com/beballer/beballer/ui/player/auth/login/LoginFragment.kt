package com.beballer.beballer.ui.player.auth.login

import android.app.Activity
import android.content.Intent
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
import com.beballer.beballer.databinding.FragmentLoginBinding
import com.beballer.beballer.ui.player.auth.helper.PhoneAuthHelper
import com.beballer.beballer.ui.player.auth.helper.SocialLoginHelper
import com.beballer.beballer.ui.player.dash_board.DashboardActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), OtpVerifyBottomSheet.OtpListener {

    private val viewModel: LoginFragmentVM by viewModels()

    @Inject
    lateinit var phoneAuthHelper: PhoneAuthHelper

    @Inject
    lateinit var socialLoginHelper: SocialLoginHelper
    private var token = ""
    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var bottomSheet: OtpVerifyBottomSheet


    override fun getLayoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClickListeners()
        // observer
        initObserver()
        // firebase get token
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            token = it.result

        }
    }


    /** handle click **/
    private fun initClickListeners() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }

                R.id.btnNext -> {
                    val mobileNumber = binding.etMobile.text.toString().trim()
                    if (mobileNumber.isNotEmpty() && mobileNumber.length >= 10) {
                        showLoading()
                        logoutUser()
                        val phone = "+" + binding.countryCode.selectedCountryCode + binding.etMobile.text.toString().trim()
                        phoneAuthHelper.startPhoneVerification(requireActivity(), phone,
                            onCodeSent = { id, token ->
                                storedVerificationId = id
                                resendToken = token
                                bottomSheet = OtpVerifyBottomSheet(this)
                                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                            },
                            onComplete = { signInWithPhoneCredential(it) },
                            onError = {
                                hideLoading()
                                showErrorToast(it)
                            })
                    } else {
                        showInfoToast("Please enter valid mobile number")
                    }

                }

                R.id.clGoogle -> {
                    logoutUser()
                    signInWithGoogle()
                }

                R.id.clFacebook -> {
                    logoutUser()
                    loginWithFacebook()
                }

                R.id.tvAccount -> {
                    BindingUtils.navigateWithSlide(
                        findNavController(), R.id.navigateSignupFragment, null
                    )
                }

                // snapChats button click
                R.id.clSnapChats -> {

                }
            }
        }

        // text watcher click
        binding.etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.buttonCheck = s?.isNotEmpty() == true && s.length >= 10
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
                                    myDataModel.data?.token?.let { it1 ->
                                        sharedPrefManager.saveToken(
                                            it1
                                        )
                                    }
                                    sharedPrefManager.setLoginData(myDataModel)
                                    if (myDataModel.data?.user?.isOnboardAnalyticsDone == true ) {
                                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finishAffinity()
                                    } else {
                                        BindingUtils.navigateWithSlide(
                                            findNavController(), R.id.navigateOptionFragment, null
                                        )
                                    }

                                }

                            } catch (e: Exception) {
                                Log.e("error", "commonLoginAPi: $e")
                            }finally {
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


    /** otp enter call function ***/
    override fun onOtpEntered(otp: String) {
        hideLoading()
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        signInWithPhoneCredential(credential)
        bottomSheet.dismiss()
    }


    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        phoneAuthHelper.signInWithCredential(
            credential,
            onSuccess = { commonLogin(it.uid) },
            onFailure = { showErrorToast(it) })
    }

    private fun signInWithGoogle() {
        val intent = GoogleSignIn.getClient(
            requireActivity(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        ).signInIntent

        resultLauncher.launch(intent)


    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    socialLoginHelper.loginWithGoogle(
                        account.idToken!!,
                        onSuccess = { commonLogin(it.uid) },
                        onFailure = { showErrorToast("Google login failed") })
                } catch (e: ApiException) {
                    showErrorToast("Sign-in error")
                }
            }else{
                showErrorToast("Sign-in error")
            }
        }


    /** logout **/
    private fun logoutUser() {
        socialLoginHelper.signOut {

        }
    }

    private fun loginWithFacebook() {
        val manager = LoginManager.getInstance()
        val callbackManager = CallbackManager.Factory.create()
        manager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                socialLoginHelper.loginWithFacebook(
                    result.accessToken,
                    onSuccess = { commonLogin(it.uid) },
                    onFailure = { showErrorToast("Facebook login failed") })
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                showErrorToast(error.message.toString())
            }
        })
        manager.logInWithReadPermissions(this, listOf("email", "public_profile"))
    }

    /*** common login api call ***/
    private fun commonLogin(uid: String) {
        val data: HashMap<String, Any> = hashMapOf(
            "id" to uid,
            "longitude" to "76.7794",
            "latitude" to "30.7333",
            "type" to "player",
            "deviceToken" to token,
            "deviceType" to 2
        )
        viewModel.commonLoginAPi(data, Constants.MOBILE_LOGIN)
    }


}

