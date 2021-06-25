package app.judo.sdk.ui

import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import app.judo.sdk.ui.views.ExperienceMediaPlayerView

internal class MediaAwareLifecycleObserver(val mediaChildIDs: List<String>, val frame: ViewGroup) :
    DefaultLifecycleObserver {
    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mediaChildIDs.forEach {
            frame.findViewWithTag<ExperienceMediaPlayerView>(it)?.pausePlayer()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        mediaChildIDs.forEach {
            frame.findViewWithTag<ExperienceMediaPlayerView>(it)?.setupIfVisible()
        }
    }
}