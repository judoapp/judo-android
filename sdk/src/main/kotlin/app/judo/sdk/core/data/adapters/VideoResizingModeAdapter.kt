package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.VideoResizingMode
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class VideoResizingModeAdapter {
    @ToJson
    fun toJson(videoResizingMode: VideoResizingMode) = videoResizingMode.code

    @FromJson
    fun fromJson(alignment: String) =
        VideoResizingMode.values().find { it.code == alignment } ?: throw RuntimeException("Incorrect code $alignment")
}
