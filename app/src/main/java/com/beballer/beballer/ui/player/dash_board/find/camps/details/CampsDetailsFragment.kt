package com.beballer.beballer.ui.player.dash_board.find.camps.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.beballer.beballer.R
import com.beballer.beballer.base.BaseFragment
import com.beballer.beballer.base.BaseViewModel
import com.beballer.beballer.databinding.FragmentCampsDetailsBinding
import com.beballer.beballer.ui.player.dash_board.find.details.ImagesPagerAdapter
import com.beballer.beballer.ui.player.dash_board.find.tournament.details.TournamentPagerAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CampsDetailsFragment : BaseFragment<FragmentCampsDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: CampsDetailsFragmentVM by viewModels()
    private val translationYaxis = -100F
    private var isFabMenuVisible = false
    private val interpolator = OvershootInterpolator()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_camps_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // set block pos
        binding.pos = 1
        binding.posSub = 1
        binding.first.visibility = View.INVISIBLE
        binding.second.visibility = View.VISIBLE
        binding.third.visibility = View.VISIBLE
        binding.tvFeed.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvSubscriptions.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvPost.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
        binding.tvClassmates.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvStatistiques.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        binding.tvInventaire.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
        // fab button click
        initFabMenu()
        // view pager
        initViewPager()
        // observer
        initObserver()
        // click
        initOnClick()
        // adapter
        val adapter = TournamentPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPagerProfile.adapter = adapter
    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.cancelImage -> {
                    requireActivity().onBackPressed()
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

                R.id.tvPost -> {
                    binding.posSub = 1
                    binding.viewPagerProfile.currentItem = 0
                    binding.first.visibility = View.INVISIBLE
                    binding.second.visibility = View.VISIBLE
                    binding.third.visibility = View.VISIBLE

                    binding.tvPost.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvClassmates.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvStatistiques.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvInventaire.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }

                R.id.tvClassmates -> {
                    binding.posSub = 2
                    binding.viewPagerProfile.currentItem = 1
                    binding.first.visibility = View.INVISIBLE
                    binding.second.visibility = View.INVISIBLE
                    binding.third.visibility = View.VISIBLE

                    binding.tvPost.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvClassmates.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvStatistiques.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvInventaire.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }

                R.id.tvStatistiques -> {
                    binding.posSub = 3
                    binding.viewPagerProfile.currentItem = 2
                    binding.first.visibility = View.VISIBLE
                    binding.second.visibility = View.INVISIBLE
                    binding.third.visibility = View.INVISIBLE

                    binding.tvPost.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvClassmates.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvStatistiques.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_bold)
                    binding.tvInventaire.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                }

                R.id.tvInventaire -> {
                    binding.posSub = 4
                    binding.viewPagerProfile.currentItem = 3
                    binding.first.visibility = View.VISIBLE
                    binding.second.visibility = View.VISIBLE
                    binding.third.visibility = View.INVISIBLE

                    binding.tvPost.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvClassmates.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvStatistiques.typeface =
                        ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                    binding.tvInventaire.typeface =
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


    /** handle api response **/
    private fun initObserver() {


    }
}
