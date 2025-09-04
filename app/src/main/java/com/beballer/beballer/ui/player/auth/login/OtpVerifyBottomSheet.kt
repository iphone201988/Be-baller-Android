package com.beballer.beballer.ui.player.auth.login

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import com.beballer.beballer.R
import com.beballer.beballer.databinding.OtpBottomSheetItemBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OtpVerifyBottomSheet(private val listener: OtpListener) : BottomSheetDialogFragment() {

    interface OtpListener {
        fun onOtpEntered(otp: String)
    }

    private lateinit var otpETs: Array<AppCompatEditText?>
    var isOtpComplete = false

    private var _binding: OtpBottomSheetItemBinding? = null
    private val binding get() = _binding!!
    private var otpData = ""

    override fun getTheme(): Int = R.style.SheetDialog1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = OtpBottomSheetItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                Log.d("Keyboard", "Visible")
            } else {
                binding.apply {
                    otpET1.clearFocus()
                    otpET2.clearFocus()
                    otpET3.clearFocus()
                    otpET4.clearFocus()
                    otpET5.clearFocus()
                    otpET6.clearFocus()
                }

            }
        }

        binding.btnNext.setOnClickListener {
            listener.onOtpEntered(otpData)
        }
        binding.otpET1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET1.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET1.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
        binding.otpET2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET2.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET2.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
        binding.otpET3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET3.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET3.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
        binding.otpET4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET4.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET4.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
        binding.otpET5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET5.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET5.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
        binding.otpET6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.otpET6.setBackgroundResource(R.drawable.green_btn_bg)
                } else {
                    binding.otpET6.setBackgroundResource(R.drawable.track_background)
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { sheet ->
                sheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = true
                dialog!!.window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*** view ***/
    private fun initView() {
        otpETs = arrayOf(
            binding.otpET1,
            binding.otpET2,
            binding.otpET3,
            binding.otpET4,
            binding.otpET5,
            binding.otpET6
        )

        otpETs.forEachIndexed { index, editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index != otpETs.size - 1) {
                        otpETs[index + 1]?.requestFocus()
                    }

                    isOtpComplete = otpETs.all { it?.text?.isNotEmpty() == true }
                    if (isOtpComplete) {
                        otpData = otpETs.joinToString("") { it?.text.toString() }
                        binding.apply {
                            nextBtnDivider1.visibility = View.GONE
                            btnNext1.visibility = View.GONE
                            nextBtnDivider.visibility = View.VISIBLE
                            btnNext.visibility = View.VISIBLE
                            otpET1.clearFocus()
                            otpET2.clearFocus()
                            otpET3.clearFocus()
                            otpET4.clearFocus()
                            otpET5.clearFocus()
                            otpET6.clearFocus()
                        }
                    } else {
                        binding.apply {
                            nextBtnDivider1.visibility = View.VISIBLE
                            btnNext1.visibility = View.VISIBLE
                            nextBtnDivider.visibility = View.GONE
                            btnNext.visibility = View.GONE
                        }

                    }
                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text?.isEmpty() == true && index != 0) {
                        otpETs[index - 1]?.apply {
                            text?.clear()
                            requestFocus()
                        }
                    }
                }
                isOtpComplete = otpETs.all { it?.text?.isNotEmpty() == true }
                false
            }
        }
    }


}
