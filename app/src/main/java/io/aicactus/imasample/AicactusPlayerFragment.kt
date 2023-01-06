package io.aicactus.imasample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.aiactiv.sdk.adnetwork.ads.AdRequest
import io.aiactiv.sdk.adnetwork.ads.IMAPlayerView
import io.aicactus.imasample.databinding.FragmentAicactusPlayerBinding

class AicactusPlayerFragment: Fragment() {

    private lateinit var binding: FragmentAicactusPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_aicactus_player, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playerView = binding.root.findViewById<IMAPlayerView>(R.id.player_view)

        playerView.apply {
            contentUri = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4"
            initializePlayer()
        }

        playerView.listener = object : IMAPlayerView.PlayerViewListener {
            override fun onImaAdsFailedToLoad(adUnitID: Int, error: String) {
                Log.d("VideoAdFragment","Video Ad did fail to load with error: $error")
            }

            override fun onImaAdsLoaded(adUnitID: Int, vastTagUrl: String) {
                Log.d("VideoAdFragment","Video Ad Content URL: $vastTagUrl")
            }

            override fun onAdErrorEvent(errorTypeName: String?) {
                super.onAdErrorEvent(errorTypeName)
                Log.d("VideoAdFragment","Ad Error Event: $errorTypeName")
            }

            override fun onAdEvent(adEventName: String?) {
                super.onAdEvent(adEventName)
                Log.d("VideoAdFragment","Ad Event: $adEventName")
            }
        }

        val adRequest = AdRequest.Builder().build()
        binding.playerView.requestAd(45, adRequest, null)
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.releasePlayer()
    }
}