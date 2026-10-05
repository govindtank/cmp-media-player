# cmp-media-player

[![JitPack](https://jitpack.io/v/govindtank/cmp-media-player.svg)](https://jitpack.io/#govindtank/cmp-media-player)

**Lightweight Cross-Platform Audio & Video Player Composable for Compose Multiplatform.**

A high-performance media player component for CMP powering seamless audio and video playback on Android (Media3 ExoPlayer) and iOS (AVPlayer & AVKit).

---

## Features

- 🎥 **Unified Video Composable**: Render remote URLs (HLS, DASH, MP4, WebM) or local media with standard playback controls.
- 🎵 **Headless Audio Composable**: Background & foreground audio streaming with reactive state updates.
- 🎛️ **MediaPlayerController**: Fine-grained programmatic control (`play()`, `pause()`, `seekTo()`, `setSpeed()`, `setVolumeLevel()`).
- 🔄 **Reactive State Management**: Real-time position tracking (`currentPositionMs`), total duration (`durationMs`), and state changes (`IDLE`, `BUFFERING`, `READY`, `PLAYING`, `PAUSED`, `ENDED`).
- 📐 **Custom Video Aspect Ratios**: Easily toggle between `FIT`, `FILL`, `FIXED_WIDTH`, and `FIXED_HEIGHT`.

---

## Installation

Add JitPack and the dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.govindtank:cmp-media-player:1.0.0")
}
```

---

## Quick Start

### Video Player

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.govindtank.player.*

@Composable
fun VideoScreen() {
    val controller = rememberMediaPlayerController()

    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            config = PlayerConfig(
                autoPlay = true,
                loop = false,
                showControls = true,
                resizeMode = VideoResizeMode.FIT
            ),
            controller = controller,
            onStateChanged = { state ->
                println("Current Player State: $state")
            }
        )

        Text("Progress: ${controller.currentPositionMs / 1000}s / ${controller.durationMs / 1000}s")
    }
}
```

### Audio Player

```kotlin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import io.github.govindtank.player.*

@Composable
fun AudioScreen() {
    val controller = rememberMediaPlayerController()

    AudioPlayer(
        url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        config = PlayerConfig(autoPlay = false),
        controller = controller
    )

    Button(onClick = { controller.togglePlayPause() }) {
        Text(if (controller.state == PlayerState.PLAYING) "Pause" else "Play")
    }
}
```

---

## Platform Engine Matrix

| Platform | Video Engine | Audio Engine |
| :--- | :--- | :--- |
| **Android** | AndroidX Media3 ExoPlayer (`PlayerView`) | Media3 ExoPlayer |
| **iOS** | Apple `AVPlayer` + `AVPlayerViewController` | Apple `AVPlayer` |

---

## License

Apache License 2.0
