package io.github.govindtank.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Controller providing observable playback state and imperative commands (play, pause, seek, volume).
 */
@Stable
class MediaPlayerController {
    var state by mutableStateOf(PlayerState.IDLE)
        internal set

    var currentPositionMs by mutableLongStateOf(0L)
        internal set

    var durationMs by mutableLongStateOf(0L)
        internal set

    var volume by mutableFloatStateOf(1.0f)
        internal set

    var isMuted by mutableStateOf(false)
        internal set

    var playbackSpeed by mutableFloatStateOf(1.0f)
        internal set

    internal var onPlayAction: (() -> Unit)? = null
    internal var onPauseAction: (() -> Unit)? = null
    internal var onSeekAction: ((Long) -> Unit)? = null
    internal var onSetVolumeAction: ((Float) -> Unit)? = null
    internal var onSetSpeedAction: ((Float) -> Unit)? = null
    internal var onReleaseAction: (() -> Unit)? = null

    fun play() {
        onPlayAction?.invoke()
    }

    fun pause() {
        onPauseAction?.invoke()
    }

    fun togglePlayPause() {
        if (state == PlayerState.PLAYING) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(positionMs: Long) {
        onSeekAction?.invoke(positionMs)
    }

    fun setVolumeLevel(newVolume: Float) {
        val clamped = newVolume.coerceIn(0f, 1f)
        volume = clamped
        isMuted = clamped == 0f
        onSetVolumeAction?.invoke(clamped)
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        onSetSpeedAction?.invoke(speed)
    }

    fun release() {
        onReleaseAction?.invoke()
    }
}

/**
 * Creates and remembers a [MediaPlayerController] instance.
 */
@Composable
fun rememberMediaPlayerController(): MediaPlayerController {
    return remember { MediaPlayerController() }
}
