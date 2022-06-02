package io.aicactus.imasample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.ads.interactivemedia.v3.api.*
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.aiactiv.adnetwork.ads.AdRequest
import io.aiactiv.adnetwork.ads.AdSize
import io.aiactiv.adnetwork.ads.VideoAdListener
import io.aiactiv.adnetwork.ads.VideoAdLoader
import io.aicactus.imasample.databinding.FragmentExoPlayerBinding

class ExoPlayerFragment: Fragment(), AdsMediaSource.MediaSourceFactory, AdsLoader.AdsLoadedListener, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {

    private lateinit var binding: FragmentExoPlayerBinding

    private lateinit var dataSourceFactory: DataSource.Factory

    private lateinit var imaAdsLoader: ImaAdsLoader
    private var imaAdsManager: AdsManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_exo_player, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoAdLoader = VideoAdLoader.Builder(
            requireContext(),
            adUnitID = 11,
            adSize = AdSize.VIDEO
        ).build()

        videoAdLoader.listener = object : VideoAdListener {
            override fun onVideoAdFailedToLoad(adUnitID: Int, error: String) {
                Log.d("VideoAdFragment","Video Ad did fail to load with error: $error")
            }

            override fun onVideoAdLoaded(adUnitID: Int, vastTagUrl: String) {
                Log.d("VideoAdFragment","Video Ad Content URL: $vastTagUrl")
            }
        }

        val adRequest = AdRequest.Builder().build()
        videoAdLoader.loadAd(adRequest)
    }

    private fun initializePlayer() {
        val player = ExoPlayerFactory.newSimpleInstance(context)

        val contentUri = Uri.parse(getString(R.string.content_url))
        val adTagUri = Uri.parse(getString(R.string.ad_tag_url))

        imaAdsLoader = ImaAdsLoader(context, adTagUri)
        imaAdsLoader.setPlayer(player)
        imaAdsLoader.adsLoader?.apply {
            addAdsLoadedListener(this@ExoPlayerFragment)
            addAdErrorListener(this@ExoPlayerFragment)
        }


        dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "AicactusAdsNetwork"))
        val contentMediaSource = buildMediaSource(contentUri)
        val mediaSourceWithAds = AdsMediaSource(contentMediaSource, this, imaAdsLoader, binding.playerView)

        player.apply {
            seekTo(0)
            playWhenReady = true
            prepare(mediaSourceWithAds)
        }

        binding.playerView.player = player
    }

    private fun buildMediaSource(uri: Uri?): MediaSource {
        return when (val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    override fun createMediaSource(uri: Uri?): MediaSource {
        return buildMediaSource(uri)
    }

    override fun getSupportedTypes(): IntArray {
        return intArrayOf(C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER)
    }

    override fun onAdsManagerLoaded(adsManagerLoadedEvent: AdsManagerLoadedEvent?) {
        imaAdsManager = adsManagerLoadedEvent?.adsManager
        imaAdsManager?.apply {
            addAdErrorListener(this@ExoPlayerFragment)
            addAdEventListener(this@ExoPlayerFragment)
            init()
        }
    }

    override fun onAdError(adErrorEvent: AdErrorEvent?) {
        Log.d("ExoPlayerFragment", "Ad Event: ${adErrorEvent?.error}")
    }

    override fun onAdEvent(adEvent: AdEvent?) {
        Log.d("ExoPlayerFragment", "Ad Event: ${adEvent?.type?.name}")
    }
}