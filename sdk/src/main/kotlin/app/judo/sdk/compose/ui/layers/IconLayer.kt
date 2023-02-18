/*
 * Copyright (c) 2020-present, Rover Labs, Inc. All rights reserved.
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Rover.
 *
 * This copyright notice shall be included in all copies or substantial portions of
 * the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package app.judo.sdk.compose.ui.layers

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.judo.sdk.compose.model.nodes.Node
import app.judo.sdk.compose.model.values.ColorReference
import app.judo.sdk.compose.ui.Environment
import app.judo.sdk.compose.ui.layout.StripPackedJudoIntrinsics
import app.judo.sdk.compose.ui.modifiers.JudoModifiers
import app.judo.sdk.compose.ui.values.getComposeColor

@Composable
internal fun IconLayer(node: app.judo.sdk.compose.model.nodes.Icon) {
    IconLayer(
        node = node,
        materialName = node.icon.materialName,
        tint = node.color,
        iconSize = node.pointSize.dp
    )
}

@Composable
private fun IconLayer(node: Node, materialName: String, tint: ColorReference, iconSize: Dp) {
    val context = LocalContext.current
    val resourceId: Int = context.getMaterialIconID(materialName)

    LayerBox(JudoModifiers(node)) {
        Icon(
            painter = painterResource(id = resourceId),
            tint = tint.getComposeColor(Environment.LocalIsDarkTheme.current),
            modifier = Modifier.then(StripPackedJudoIntrinsics()).size(iconSize),
            contentDescription = ""
        )
    }
}

private fun Context.getMaterialIconID(iconName: String): Int {
    return if (iconName.endsWith(".fill")) {
        resources.getIdentifier(
            "judo_sdk_baseline_${iconName.substringBeforeLast(".fill")}",
            "drawable",
            this.packageName
        )
    } else {
        resources.getIdentifier("judo_sdk_${iconName}", "drawable", this.packageName)
    }
}
