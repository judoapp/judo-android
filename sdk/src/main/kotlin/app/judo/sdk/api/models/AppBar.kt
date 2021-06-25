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

import android.graphics.Typeface
import app.judo.sdk.core.lang.Interpolator
import app.judo.sdk.core.utils.Translator
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppBar(
    override val id: String,
    override val name: String? = null,
    override val metadata: Metadata? = null,
    /**
     * Show or hide the up arrow button.
     */
    val hideUpIcon: Boolean,
    /**
     * Color for all the icons.
     */
    val buttonColor: ColorVariants,
    /**
     * The title should be a signpost for the AppBar's current position in the navigation hierarchy
     * and the content contained there.
     */
    val title: String,
    val titleFont: Font,
    val titleColor: ColorVariants,
    val backgroundColor: ColorVariants,
    val childIDs: List<String> = emptyList()
) : NodeContainer, SupportsTranslation, SupportsInterpolation {

    override fun getChildNodeIDs(): List<String> = childIDs

    override val typeName = NodeType.APP_BAR.code

    @Transient
    internal var typeface: Typeface? = null

    @Transient
    override var translator: Translator = Translator { it }

    @Transient
    override var interpolator: Interpolator? = null

    internal val translatedTitle: String
        get() = translator.translate(title)

    internal val interpolatedTitle: String
        get() {
            val translatedText = translatedTitle

            return interpolator?.interpolate(translatedText) ?: translatedText
        }

    override fun <R> accept(visitor: Visitor<R>): R {
        return visitor.visit(this)
    }
}