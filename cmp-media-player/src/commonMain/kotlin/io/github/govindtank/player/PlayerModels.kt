package io.github.govindtank.player

/**
 * Playback states for cross-platform media players.
 */
enum class PlayerState {
    IDLE,
    BUFFERING,
    READY,
    PLAYING,
    PAUSED,
    ENDED,
    ERROR
}

/**
 * Scaling mode for video rendering.
 */
enum class VideoResizeMode {
    FIT,
    FILL,
    FIXED_WIDTH,
    FIXED_HEIGHT
}

/**
 * Common configuration options for audio/video playback.
 */
data class PlayerConfig(
    val autoPlay: Boolean = true,
    val loop: Boolean = false,
    val showControls: Boolean = true,
    val volume: Float = 1.0f,
    val playbackSpeed: Float = 1.0f,
    val resizeMode: VideoResizeMode = VideoResizeMode.FIT
)
