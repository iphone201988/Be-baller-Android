package com.beballer.beballer.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.beballer.beballer.App
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.connectivity.ConnectivityProvider
import com.beballer.beballer.base.local.SharedPrefManager
import com.beballer.beballer.ui.player.auth.AuthActivity
import com.beballer.beballer.utils.event.NoInternetSheet
import javax.inject.Inject

abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {

    lateinit var progressDialogAvl: ProgressDialogAvl
    lateinit var binding: Binding
    val app: App
        get() = application as App

    private lateinit var connectivityProvider: ConnectivityProvider
    private var noInternetSheet: NoInternetSheet? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.vm, getViewModel())
        connectivityProvider = ConnectivityProvider.createProvider(this)
        connectivityProvider.addListener(this)
        progressDialogAvl = ProgressDialogAvl(this)
        // setStatusBarColor(R.color.white)
        // setStatusBarDarkText()
        onCreateView()

        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        vm.onUnAuth.observe(this) {
            showUnauthorised()
        }
    }

    fun showUnauthorised() {
        sharedPrefManager.clear()
        val intent = Intent(this@BaseActivity, AuthActivity::class.java)
        startActivity(intent)
        finishAffinity()
        overridePendingTransition(
            R.anim.slide_in_right, R.anim.slide_out_left
        )
    }


    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()


    override fun onStop() {
        super.onStop()
    }

    fun hideLoading() {
        progressDialogAvl.isLoading(false)
    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }


    override fun onDestroy() {
        connectivityProvider.removeListener(this)
        super.onDestroy()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.isCancelable = false
        }
        if (state.hasInternet()) {
            if (noInternetSheet?.isVisible == true) noInternetSheet?.dismiss()
        } else {
            if (noInternetSheet?.isVisible == false) noInternetSheet?.show(
                supportFragmentManager,
                noInternetSheet?.tag
            )
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }


    fun showErrorToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.error_toast_item, null)

        val textView: AppCompatTextView = layout.findViewById(R.id.tvErrorToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this@BaseActivity)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }


    fun showSuccessToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.successful_toast_item, null)
        val textView: AppCompatTextView = layout.findViewById(R.id.tvSuccessToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this@BaseActivity)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }

    fun showInfoToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.info_toast_item, null)
        val textView: TextView = layout.findViewById(R.id.tvInfoToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(this@BaseActivity)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }

    fun showCustomToast(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.info_toast_item, null)

        val textView = layout.findViewById<TextView>(R.id.tvInfoToast)
        textView.text = message

        Toast(context.applicationContext).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            show()
        }
    }


}