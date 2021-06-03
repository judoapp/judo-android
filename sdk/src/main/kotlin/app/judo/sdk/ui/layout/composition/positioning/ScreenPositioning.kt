package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.FloatPoint
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.Screen
import app.judo.sdk.ui.layout.composition.TreeNode
import app.judo.sdk.ui.layout.composition.computePosition
import app.judo.sdk.ui.layout.composition.getHeight
import app.judo.sdk.ui.layout.composition.getWidth

internal fun Screen.computePosition(context: Context, treeNode: TreeNode) {
    // set positions
    treeNode.children.forEach {
        if (it.getHeight() > sizeAndCoordinates.height) {
            val y = 0f
            val x = (treeNode.getWidth() - it.getWidth()) / 2f

            (it.value as Layer).computePosition(context, it, FloatPoint(x, y))
        } else {
            val y =  ((treeNode.getHeight() - it.getHeight()) / 2f)
            val x = (treeNode.getWidth() - it.getWidth()) / 2f

            (it.value as Layer).computePosition(context, it, FloatPoint(x, y))
        }
    }
}