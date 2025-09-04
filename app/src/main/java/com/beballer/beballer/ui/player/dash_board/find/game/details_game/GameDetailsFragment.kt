package com.beballer.beballer.ui.player.dash_board.find.game.details_game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.beballer.beballer.BR
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.base.SimpleRecyclerViewAdapter
import com.beballer.beballer.data.model.ChatModel
import com.beballer.beballer.data.model.MpvModel
import com.beballer.beballer.databinding.FragmentGameDetailsBinding
import com.beballer.beballer.databinding.RecyclerGameMessageItemBinding
import com.beballer.beballer.databinding.RvOutSideItemBinding
import com.beballer.beballer.databinding.RvTeamItemBinding
import com.beballer.beballer.ui.player.dash_board.find.details.ImagesPagerAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailsFragment : BaseFragment<FragmentGameDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: GameDetailsFragmentVM by viewModels()
    private lateinit var homeTeamAdapter: SimpleRecyclerViewAdapter<MpvModel, RvTeamItemBinding>
    private lateinit var outSideTeamAdapter: SimpleRecyclerViewAdapter<MpvModel, RvOutSideItemBinding>
    private lateinit var chatAdapter: SimpleRecyclerViewAdapter<ChatModel, RecyclerGameMessageItemBinding>
    private val translationYaxis = -100F
    private var isFabMenuVisible = false
    private val interpolator = OvershootInterpolator()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_game_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // set block pos
        binding.pos = 1
        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvSubscriptions.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        // fab button click
        initFabMenu()
        // view pager
        initViewPager()
        // click
        initOnClick()
        // adapter
        initHomeTeamAdapter()
        initOutSideAdapter()
        initChatAdapter()
        val fullText = "Created by\n@Junior78"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("@Junior78")
        val end = start + "@Junior78".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#1877F2")),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGameOrganizer.text = spannable

    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.cancelImage -> {
                    requireActivity().finish()
                }
                // message send button  click
                R.id.ivSend -> {
                    val message = binding.etSendMessage.text.toString()
                    if (message.isEmpty()) {
                        showInfoToast("Please enter message")
                    } else {
                        chatList.add(ChatModel(message, true))
                        chatAdapter.list = chatList
                        binding.messagesRecyclerView.scrollToPosition(chatList.size - 1)
                        binding.etSendMessage.setText("")
                        binding.etSendMessage.clearFocus()
                    }

                }

                // tvFeed button click
                R.id.tvFeed -> {
                    binding.pos = 1
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }
                // tvSubscriptions  button click
                R.id.tvSubscriptions -> {
                    binding.pos = 2
                    binding.tvFeed.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvSubscriptions.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                }
            }
        }
    }

    /*** view pager handel **/
    private fun initViewPager() {
//        val uriList = ArrayList<Int>().apply {
//            add(R.drawable.iv_event)
//            add(R.drawable.iv_event)
//        }
//
//        val imagesPagerAdapter = ImagesPagerAdapter(requireContext(), uriList)
//        binding.viewPager.adapter = imagesPagerAdapter
//        binding.dotsIndicator.apply {
//            setSliderColor(
//                ContextCompat.getColor(requireContext(), R.color.beballer_grey),
//                ContextCompat.getColor(requireContext(), R.color.beballer_orange)
//            )
//            setSliderWidth(
//                resources.getDimension(com.intuit.sdp.R.dimen._8sdp),
//                resources.getDimension(com.intuit.sdp.R.dimen._8sdp)
//            )
//            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._8sdp))
//            setSlideMode(IndicatorSlideMode.NORMAL)
//            setIndicatorStyle(IndicatorStyle.CIRCLE)
//
//            binding.dotsIndicator.setupWithViewPager(binding.viewPager)
//            setPageSize(imagesPagerAdapter.itemCount)
//            notifyDataChanged()
//
//            binding.noCourtPicture.isVisible = imagesPagerAdapter.itemCount == 0
//        }
    }

    /*** fab button click handel  **/
    private fun initFabMenu() {
        binding.courtMenusLayout.alpha = 0F
        binding.courtMenusLayout.translationY = translationYaxis
        binding.courtMenusLayout.isVisible = false
        binding.courtMenuFab.setOnClickListener {
            when (isFabMenuVisible) {
                true -> {
                    binding.courtMenuFab.animate().rotation(0F).setInterpolator(interpolator)
                        .setDuration(150).start()
                    binding.courtMenusLayout.animate().translationY(translationYaxis).alpha(0F)
                        .setInterpolator(interpolator).setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                binding.courtMenusLayout.isVisible = false

                            }
                        }).start()
                }

                false -> {
                    binding.courtMenusLayout.isVisible = true

                    binding.courtMenuFab.animate().rotation(-90F).setInterpolator(interpolator)
                        .setDuration(150).start()

                    binding.courtMenusLayout.animate().translationY(0F).setListener(null).alpha(1F)
                        .setInterpolator(interpolator).setDuration(300).start()
                }
            }
            isFabMenuVisible = !isFabMenuVisible
        }
    }


    /*** map ready ***/
    private var mMap: GoogleMap? = null
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        try {
            mMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    /** handle home team adapter **/
    private fun initHomeTeamAdapter() {
        homeTeamAdapter = SimpleRecyclerViewAdapter(R.layout.rv_team_item, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }
        homeTeamAdapter.list = getList()
        binding.rvHomeTeam.adapter = homeTeamAdapter
        Log.d("fsddfsdf", "initHomeTeamAdapter: ${homeTeamAdapter.list.size}")
    }

    /** handle out side adapter **/
    private fun initOutSideAdapter() {
        outSideTeamAdapter =
            SimpleRecyclerViewAdapter(R.layout.rv_out_side_item, BR.bean) { v, m, pos ->
                when (v.id) {

                }
            }
        outSideTeamAdapter.list = getList()
        binding.rvOutsideTeam.adapter = outSideTeamAdapter
    }

    /** handle out side adapter **/
    private var chatList = ArrayList<ChatModel>()
    private fun initChatAdapter() {
        chatAdapter =
            SimpleRecyclerViewAdapter(R.layout.recycler_game_message_item, BR.bean) { v, m, pos ->
                when (v.id) {

                }
            }
        chatList = getChatList()
        chatAdapter.list = chatList
        binding.messagesRecyclerView.adapter = chatAdapter
    }


    // add List in data chat
    private fun getChatList(): ArrayList<ChatModel> {
        return arrayListOf(
            ChatModel("hi", true),
            ChatModel("how are you", true),
            ChatModel("i am fine", false),
            ChatModel("where are you at this time", true),
            ChatModel("jaunpur", false),
            ChatModel("ok ok ", true),
        )

    }


    // add List in data
    private fun getList(): ArrayList<MpvModel> {
        return arrayListOf(
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "175pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "11pts"),
            MpvModel("Leo Florentin", "Forcalquier", "75pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "120pts"),
            MpvModel("Elliot Le Gall", "Camaret-sur-Mer", "131pts"),
            MpvModel("Leo Florentin", "Forcalquier", "100pts"),

            )
    }


}