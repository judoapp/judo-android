package app.judo.sdk.api.models

import app.judo.sdk.ui.layout.composition.SizeAndCoordinates

internal interface Layer : Node {
    val frame: Frame?
    var sizeAndCoordinates: SizeAndCoordinates
    var maskPath: MaskPath?
    fun determineLayoutPriority(): Float
}
