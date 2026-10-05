package io.github.govindtank.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private var appContext: Context? = null

actual fun mediaPlayerInit(context: Any?) {
    if (context is Context) {
        appContext = context.applicationContext
    }
}

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    config: PlayerConfig,
    controller: MediaPlayerController,
    onStateChanged: ((PlayerState) -> Unit)?
) {
    val context = LocalContext.current

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            repeatMode = if (config.loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            volume = config.volume
            playbackParameters = PlaybackParameters(config.playbackSpeed)
            playWhenReady = config.autoPlay
            prepare()
        }
    }

    // Attach controller actions
    DisposableEffect(exoPlayer) {
        controller.onPlayAction = { exoPlayer.play() }
        controller.onPauseAction = { exoPlayer.pause() }
        controller.onSeekAction = { pos -> exoPlayer.seekTo(pos) }
        controller.onSetVolumeAction = { vol -> exoPlayer.volume = vol }
        controller.onSetSpeedAction = { spd -> exoPlayer.playbackParameters = PlaybackParameters(spd) }
        controller.onReleaseAction = { exoPlayer.release() }

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> PlayerState.IDLE
                    Player.STATE_BUFFERING -> PlayerState.BUFFERING
                    Player.STATE_READY -> if (exoPlayer.isPlaying) PlayerState.PLAYING else PlayerState.READY
                    Player.STATE_ENDED -> PlayerState.ENDED
                    else -> PlayerState.IDLE
                }
                controller.state = state
                controller.durationMs = exoPlayer.duration.coerceAtLeast(0L)
                onStateChanged?.invoke(state)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val state = if (isPlaying) PlayerState.PLAYING else {
                    if (exoPlayer.playbackState == Player.STATE_ENDED) PlayerState.ENDED else PlayerState.PAUSED
                }
                controller.state = state
                onStateChanged?.invoke(state)
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Position tracker coroutine
    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                controller.currentPositionMs = exoPlayer.currentPosition
                controller.durationMs = exoPlayer.duration.coerceAtLeast(0L)
            }
            delay(500)
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = config.showControls
                resizeMode = when (config.resizeMode) {
                    VideoResizeMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    VideoResizeMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    VideoResizeMode.FIXED_WIDTH -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    VideoResizeMode.FIXED_HEIGHT -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                }
            }
        },
        modifier = modifier
    )
}

@Composable
actual fun AudioPlayer(
    url: String,
    config: PlayerConfig,
    controller: MediaPlayerController,
    onStateChanged: ((PlayerState) -> Unit)?
) {
    val context = LocalContext.current

    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            repeatMode = if (config.loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            volume = config.volume
            playbackParameters = PlaybackParameters(config.playbackSpeed)
            playWhenReady = config.autoPlay
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        controller.onPlayAction = { exoPlayer.play() }
        controller.onPauseAction = { exoPlayer.pause() }
        controller.onSeekAction = { pos -> exoPlayer.seekTo(pos) }
        controller.onSetVolumeAction = { vol -> exoPlayer.volume = vol }
        controller.onSetSpeedAction = { spd -> exoPlayer.playbackParameters = PlaybackParameters(spd) }
        controller.onReleaseAction = { exoPlayer.release() }

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> PlayerState.IDLE
                    Player.STATE_BUFFERING -> PlayerState.BUFFERING
                    Player.STATE_READY -> if (exoPlayer.isPlaying) PlayerState.PLAYING else PlayerState.READY
                    Player.STATE_ENDED -> PlayerState.ENDED
                    else -> PlayerState.IDLE
                }
                controller.state = state
                controller.durationMs = exoPlayer.duration.coerceAtLeast(0L)
                onStateChanged?.invoke(state)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val state = if (isPlaying) PlayerState.PLAYING else {
                    if (exoPlayer.playbackState == Player.STATE_ENDED) PlayerState.ENDED else PlayerState.PAUSED
                }
                controller.state = state
                onStateChanged?.invoke(state)
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                controller.currentPositionMs = exoPlayer.currentPosition
                controller.durationMs = exoPlayer.duration.coerceAtLeast(0L)
            }
            delay(500)
        }
    }
}
