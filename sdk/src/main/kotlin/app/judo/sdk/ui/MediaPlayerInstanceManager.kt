package app.judo.sdk.ui

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import java.util.*

internal object MediaPlayerInstanceManager {
    private val instances = ArrayDeque<Pair<String, SimpleExoPlayer>>()
    private const val MAX_INSTANCES = 5

    fun getInstance(mediaViewID: String, context: Context): SimpleExoPlayer {
        return when {
            instances.find { it.first == mediaViewID } != null -> instances.find { it.first == mediaViewID }!!.second
            instances.count() < MAX_INSTANCES -> {
                val instance = SimpleExoPlayer.Builder(context).build()
                instances.addLast(mediaViewID to instance)
                instance
            }
            else -> {
                val instanceToRemove = instances.firstOrNull { !it.second.isPlaying } ?: instances.first()
                instances.remove(instanceToRemove)
                instanceToRemove.second.release()
                val instance = SimpleExoPlayer.Builder(context).build()
                instances.addLast(mediaViewID to instance)
                instance
            }
        }
    }

    fun instanceStillAvailable(mediaViewID: String): Boolean {
        return instances.any { it.first == mediaViewID }
    }

    fun releaseInstances() {
        instances.forEach {
            it.second.release()
        }
        instances.clear()
    }
}