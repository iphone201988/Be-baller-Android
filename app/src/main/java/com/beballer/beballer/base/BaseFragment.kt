package com.beballer.beballer.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.local.SharedPrefManager
import javax.inject.Inject


abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    lateinit var binding: Binding
    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>
    lateinit var progressDialogAvl: ProgressDialogAvl
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateView(view)
        progressDialogAvl = ProgressDialogAvl(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout,container,false)

        val vm = getViewModel()
        binding.setVariable(BR.vm, vm)
        vm.onUnAuth.observe(viewLifecycleOwner) {
            val activity = requireActivity() as BaseActivity<*>
            activity.showUnauthorised()
        }
        return binding.root
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View)

    override fun onPause() {
        super.onPause()
    }
    fun hideLoading() {
        progressDialogAvl.isLoading(false)

    }

    fun showLoading() {
        progressDialogAvl.isLoading(true)
    }



    fun showErrorToast(msg: String? = "Something went wrong !!") {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.error_toast_item, null)

        val textView: AppCompatTextView = layout.findViewById(R.id.tvErrorToast)
        textView.text = msg ?: "Showed null value !!"

        val toast = Toast(parentActivity)
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

        val toast = Toast(parentActivity)
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

        val toast = Toast(parentActivity)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 50)
        toast.show()
    }


}