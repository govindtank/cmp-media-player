package io.github.govindtank.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import platform.AVFoundation.AVLayerVideoGravityResize
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerTimeControlStatus
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL

actual fun mediaPlayerInit(context: Any?) {
    // No context needed on iOS
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    config: PlayerConfig,
    controller: MediaPlayerController,
    onStateChanged: ((PlayerState) -> Unit)?
) {
    val nsUrl = remember(url) { NSURL.URLWithString(url) ?: NSURL.fileURLWithPath(url) }
    val playerItem = remember(nsUrl) { AVPlayerItem(uRL = nsUrl) }
    val player = remember(playerItem) { AVPlayer(playerItem = playerItem) }

    val playerViewController = remember(player) {
        AVPlayerViewController().apply {
            this.player = player
            this.showsPlaybackControls = config.showControls
            this.videoGravity = when (config.resizeMode) {
                VideoResizeMode.FIT -> AVLayerVideoGravityResizeAspect
                VideoResizeMode.FILL -> AVLayerVideoGravityResizeAspectFill
                VideoResizeMode.FIXED_WIDTH,
                VideoResizeMode.FIXED_HEIGHT -> AVLayerVideoGravityResize
            }
        }
    }

    DisposableEffect(player) {
        if (config.autoPlay) {
            player.play()
            controller.state = PlayerState.PLAYING
        }

        controller.onPlayAction = {
            player.play()
            controller.state = PlayerState.PLAYING
        }
        controller.onPauseAction = {
            player.pause()
            controller.state = PlayerState.PAUSED
        }
        controller.onSeekAction = { posMs ->
            val seconds = posMs.toDouble() / 1000.0
            player.seekToTime(CMTimeMakeWithSeconds(seconds, 1000))
        }
        controller.onSetVolumeAction = { vol -> player.volume = vol }
        controller.onReleaseAction = {
            player.pause()
        }

        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = playerItem,
            queue = null
        ) { _ ->
            if (config.loop) {
                player.seekToTime(CMTimeMakeWithSeconds(0.0, 1000))
                player.play()
            } else {
                controller.state = PlayerState.ENDED
                onStateChanged?.invoke(PlayerState.ENDED)
            }
        }

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
            player.pause()
        }
    }

    LaunchedEffect(player) {
        while (isActive) {
            val isPlaying = player.timeControlStatus == AVPlayerTimeControlStatus.AVPlayerTimeControlStatusPlaying
            val posSec = CMTimeGetSeconds(player.currentTime())
            if (!posSec.isNaN()) {
                controller.currentPositionMs = (posSec * 1000).toLong()
            }
            val durSec = CMTimeGetSeconds(playerItem.duration)
            if (!durSec.isNaN()) {
                controller.durationMs = (durSec * 1000).toLong()
            }

            val state = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
            if (controller.state != PlayerState.ENDED && controller.state != state) {
                controller.state = state
                onStateChanged?.invoke(state)
            }
            delay(500)
        }
    }

    UIKitView(
        factory = { playerViewController.view },
        modifier = modifier
    )
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AudioPlayer(
    url: String,
    config: PlayerConfig,
    controller: MediaPlayerController,
    onStateChanged: ((PlayerState) -> Unit)?
) {
    val nsUrl = remember(url) { NSURL.URLWithString(url) ?: NSURL.fileURLWithPath(url) }
    val playerItem = remember(nsUrl) { AVPlayerItem(uRL = nsUrl) }
    val player = remember(playerItem) { AVPlayer(playerItem = playerItem) }

    DisposableEffect(player) {
        if (config.autoPlay) {
            player.play()
            controller.state = PlayerState.PLAYING
        }

        controller.onPlayAction = {
            player.play()
            controller.state = PlayerState.PLAYING
        }
        controller.onPauseAction = {
            player.pause()
            controller.state = PlayerState.PAUSED
        }
        controller.onSeekAction = { posMs ->
            val seconds = posMs.toDouble() / 1000.0
            player.seekToTime(CMTimeMakeWithSeconds(seconds, 1000))
        }
        controller.onSetVolumeAction = { vol -> player.volume = vol }
        controller.onReleaseAction = {
            player.pause()
        }

        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = playerItem,
            queue = null
        ) { _ ->
            if (config.loop) {
                player.seekToTime(CMTimeMakeWithSeconds(0.0, 1000))
                player.play()
            } else {
                controller.state = PlayerState.ENDED
                onStateChanged?.invoke(PlayerState.ENDED)
            }
        }

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
            player.pause()
        }
    }

    LaunchedEffect(player) {
        while (isActive) {
            val isPlaying = player.timeControlStatus == AVPlayerTimeControlStatus.AVPlayerTimeControlStatusPlaying
            val posSec = CMTimeGetSeconds(player.currentTime())
            if (!posSec.isNaN()) {
                controller.currentPositionMs = (posSec * 1000).toLong()
            }
            val durSec = CMTimeGetSeconds(playerItem.duration)
            if (!durSec.isNaN()) {
                controller.durationMs = (durSec * 1000).toLong()
            }

            val state = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
            if (controller.state != PlayerState.ENDED && controller.state != state) {
                controller.state = state
                onStateChanged?.invoke(state)
            }
            delay(500)
        }
    }
}
