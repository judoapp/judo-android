package app.judo.sdk.ui.layout.composition.sizing

import android.content.Context
import app.judo.sdk.api.models.Screen
import app.judo.sdk.ui.layout.composition.Dimension
import app.judo.sdk.ui.layout.composition.Dimensions
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computeSize

internal fun Screen.computeSize(context: Context, treeNode: TreeNode, parentConstraints: Dimensions) {

    val width = (parentConstraints.width as Dimension.Value).value
    val height = (parentConstraints.height as Dimension.Value).value

    this.sizeAndCoordinates = sizeAndCoordinates.copy(
        width = width,
        height = height
    )

    treeNode.children.forEach {
        it.computeSize(context, Dimensions(
            width = Dimension.Value(width),
            height = Dimension.Value(height)
        ))
    }
}