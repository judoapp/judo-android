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

package app.judo.sdk.api.models

import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.utils.Translator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    val title: String,
    override var action: Action? = null,
    val showAsAction: MenuItemVisibility,
    val iconMaterialName: String,
    val contentDescription: String? = null,
    val actionDescription: String? = null,
) : Node, Actionable, SupportsTranslation, SupportsInterpolation {

    override val typeName = NodeType.MENU_ITEM.code

    @Transient
    override var translator: Translator = Translator { it }

    @Transient
    override var interpolator: Interpolator? = null

    internal val translatedTitle: String
        get() = translator.translate(title)

    internal val translatedContentDescription: String?
        get() = contentDescription?.let(translator::translate) ?: contentDescription

    internal val translatedActionDescription: String?
        get() = actionDescription?.let(translator::translate) ?: actionDescription

    internal val interpolatedTitle: String
        get() {
            return interpolator?.interpolate(translatedTitle) ?: translatedTitle
        }

    internal val interpolatedContentDescription: String?
        get() {
            return translatedContentDescription?.let {
                interpolator?.interpolate(it)
            } ?: translatedContentDescription
        }

    internal val interpolatedActionDescription: String?
        get() {
            return translatedActionDescription?.let {
                interpolator?.interpolate(it)
            } ?: translatedActionDescription
        }

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }

}