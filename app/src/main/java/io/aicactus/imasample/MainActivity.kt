package io.aicactus.imasample

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity() {

    private lateinit var adsLoader: ImaAdsLoader
    private var playerView: PlayerView? = null
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsLoader = ImaAdsLoader.Builder(this).build()
        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            playerView?.onResume()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adsLoader.release()
    }

    private fun initializePlayer() {
        val dataSourceFactory = DefaultDataSource.Factory(this)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory).apply {
            setAdsLoaderProvider { adsLoader }
            setAdViewProvider { playerView }
        }

        player = ExoPlayer.Builder(this).apply {
           setMediaSourceFactory(mediaSourceFactory)
        }.build()
        playerView?.player = player
        adsLoader.setPlayer(player)

        val contentUri = Uri.parse(getString(R.string.content_url))
        val adTagUri = Uri.parse(getString(R.string.ad_tag_url))

        val adsConfiguration = MediaItem.AdsConfiguration.Builder(adTagUri).build()
        val mediaItem = MediaItem.Builder().apply {
            setUri(contentUri)
            setAdsConfiguration(adsConfiguration)
        }.build()

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    private fun releasePlayer() {
        adsLoader.setPlayer(null)
        playerView?.player = null
        player?.release()
        player = null
    }
}