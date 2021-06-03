package app.judo.sdk.ui.layout.composition.positioning

import android.content.Context
import app.judo.sdk.api.models.*
import app.judo.sdk.ui.extensions.dp
import app.judo.sdk.ui.extensions.toPx
import app.judo.sdk.ui.layout.composition.*

internal fun ScrollContainer.computePosition(context: Context, treeNode: TreeNode, point: FloatPoint) {
    val offsetX = offset?.x?.dp?.toPx(context) ?: 0f
    val offsetY = offset?.y?.dp?.toPx(context) ?: 0f

    treeNode.children.forEach {
        // set frame alignment
        frame?.alignment?.let { alignment ->
            if (axis == Axis.VERTICAL) {
                when(alignment) {
                    Alignment.TOP -> {
                        it.setX(it.getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
                    }
                    Alignment.TOP_LEADING -> { }
                    Alignment.TOP_TRAILING -> {
                        it.setX(it.getX() + (sizeAndCoordinates.width - sizeAndCoordinates.contentWidth))
                    }
                    Alignment.BOTTOM -> {
                        it.setX(it.getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
                    }
                    Alignment.BOTTOM_LEADING -> { }
                    Alignment.BOTTOM_TRAILING -> {
                        it.setX(it.getX() + sizeAndCoordinates.width - sizeAndCoordinates.contentWidth)
                    }
                    Alignment.LEADING -> {}
                    Alignment.TRAILING -> {
                        it.setX(it.getX() + sizeAndCoordinates.width - sizeAndCoordinates.contentWidth)
                    }
                    Alignment.CENTER -> {
                        it.setX(it.getX() + ((sizeAndCoordinates.width - sizeAndCoordinates.contentWidth) / 2f))
                    }
                }
            } else {
                when(alignment) {
                    Alignment.TOP -> { }
                    Alignment.TOP_LEADING -> { }
                    Alignment.TOP_TRAILING -> { }
                    Alignment.BOTTOM -> {
                        it.setY(it.getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
                    }
                    Alignment.BOTTOM_LEADING -> {
                        it.setY(it.getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
                    }
                    Alignment.BOTTOM_TRAILING -> {
                        it.setY(it.getY() + sizeAndCoordinates.height - sizeAndCoordinates.contentHeight)
                    }
                    Alignment.LEADING -> {
                        it.setY(it.getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f))
                    }
                    Alignment.TRAILING -> {
                        it.setY(it.getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f))
                    }
                    Alignment.CENTER -> { it.setY(it.getY() + ((sizeAndCoordinates.height - sizeAndCoordinates.contentHeight) / 2f)) }
                }
            }
        }

        val horizontalPaddingWhenCentred = (((padding?.leading?.dp?.toPx(context) ?: 0f) - ((padding?.trailing?.dp?.toPx(context)) ?: 0f)) / 2f)
        val verticalPaddingWhenCentred = (((padding?.top?.dp?.toPx(context) ?: 0f) - ((padding?.bottom?.dp?.toPx(context)) ?: 0f)) / 2f)

        if (axis == Axis.VERTICAL) {
            when (frame?.alignment) {
                Alignment.TOP -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + horizontalPaddingWhenCentred)
                }
                Alignment.TOP_LEADING -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.TOP_TRAILING -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() - (padding?.trailing?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM -> {
                    it.setX(it.getX() + horizontalPaddingWhenCentred)
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM_LEADING -> {
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM_TRAILING -> {
                    it.setX(it.getX() - (padding?.trailing?.dp?.toPx(context) ?: 0f))
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                }
                Alignment.LEADING -> {
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                }
                Alignment.TRAILING -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() - (padding?.trailing?.dp?.toPx(context) ?: 0f))
                }
                Alignment.CENTER -> {
                    it.setX(it.getX() + horizontalPaddingWhenCentred)
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                }
                null -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
            }
        } else {
            when (frame?.alignment) {
                Alignment.TOP -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.TOP_LEADING -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.TOP_TRAILING -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM -> {
                    it.setY(it.getY() - (padding?.bottom?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM_LEADING -> {
                    it.setY(it.getY() - (padding?.bottom?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.BOTTOM_TRAILING -> {
                    it.setY(it.getY() - (padding?.bottom?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.LEADING -> {
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                    it.setY(it.getY() + verticalPaddingWhenCentred)
                }
                Alignment.TRAILING -> {
                    it.setY(it.getY() + verticalPaddingWhenCentred)
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
                Alignment.CENTER -> {
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                    it.setY(it.getY() + verticalPaddingWhenCentred)
                }
                null -> {
                    it.setY(it.getY() + (padding?.top?.dp?.toPx(context) ?: 0f))
                    it.setX(it.getX() + (padding?.leading?.dp?.toPx(context) ?: 0f))
                }
            }
        }
    }

    background?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, background.alignment)
    overlay?.node?.computeSingleNodeRelativePosition(this.sizeAndCoordinates.width, this.sizeAndCoordinates.height, overlay.alignment)
    background?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))
    overlay?.node?.computeSingleNodeCoordinates(context, FloatPoint(getX() + point.x + offsetX, getY() + point.y + offsetY))

    setX(getX() + point.x + offsetX)
    setY(getY() + point.y + offsetY)

    // set child positions to 0,0 because children are added to scroll container frame, not screen frame so should
    // be positioned relative to scroll frame
    treeNode.children.forEach {
        (it.value as Layer).computePosition(context, it, FloatPoint(0f, 0f))
    }
}