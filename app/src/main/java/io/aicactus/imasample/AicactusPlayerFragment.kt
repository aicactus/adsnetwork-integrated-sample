package io.aicactus.imasample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.ads.interactivemedia.v3.api.AdErrorEvent
import com.google.ads.interactivemedia.v3.api.AdEvent
import io.aiactiv.adnetwork.ads.AdRequest
import io.aiactiv.adnetwork.ads.AdSize
import io.aiactiv.adnetwork.ads.ima.ImaAdsLoadListener
import io.aiactiv.adnetwork.ads.ima.PlayerView
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
        val playerView = binding.root.findViewById<PlayerView>(R.id.player_view)

        playerView.apply {
            contentUri = "https://storage.googleapis.com/gvabox/media/samples/stock.mp4"
            autoPlay = false
            initializePlayer()
        }

        playerView.listener = object : ImaAdsLoadListener {
            override fun onImaAdsFailedToLoad(adUnitID: Int, error: String) {
                Log.d("VideoAdFragment","Video Ad did fail to load with error: $error")
            }

            override fun onImaAdsLoaded(adUnitID: Int, vastTagUrl: String) {
                Log.d("VideoAdFragment","Video Ad Content URL: $vastTagUrl")
            }

            override fun onAdErrorEvent(adErrorEvent: AdErrorEvent?) {
                super.onAdErrorEvent(adErrorEvent)
                Log.d("VideoAdFragment","Ad Error Event: ${adErrorEvent?.error?.localizedMessage}")
            }

            override fun onAdEvent(adEvent: AdEvent?) {
                super.onAdEvent(adEvent)
                Log.d("VideoAdFragment","Ad Event: ${adEvent?.type?.name}")
            }
        }

        val adRequest = AdRequest.Builder().build()
        binding.playerView.requestAd(11, adRequest, null)
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.releasePlayer()
    }
}