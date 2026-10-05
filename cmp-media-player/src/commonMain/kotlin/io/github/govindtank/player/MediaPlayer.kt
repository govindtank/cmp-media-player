package io.github.govindtank.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform initializer (e.g. passing Android Context).
 */
expect fun mediaPlayerInit(context: Any? = null)

/**
 * Cross-platform video player composable.
 *
 * @param url Remote media stream or local file path.
 * @param modifier Compose layout modifier.
 * @param config Playback configuration (autoPlay, loop, resizeMode, controls).
 * @param controller Optional controller for programmatically controlling playback.
 * @param onStateChanged Callback invoked when player state transitions.
 */
@Composable
expect fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    config: PlayerConfig = PlayerConfig(),
    controller: MediaPlayerController = rememberMediaPlayerController(),
    onStateChanged: ((PlayerState) -> Unit)? = null
)

/**
 * Cross-platform background/foreground audio player composable.
 *
 * @param url Remote audio stream or local file path.
 * @param config Playback configuration (autoPlay, loop).
 * @param controller Optional controller for playback commands and position tracking.
 * @param onStateChanged Callback invoked when audio state transitions.
 */
@Composable
expect fun AudioPlayer(
    url: String,
    config: PlayerConfig = PlayerConfig(showControls = false),
    controller: MediaPlayerController = rememberMediaPlayerController(),
    onStateChanged: ((PlayerState) -> Unit)? = null
)
