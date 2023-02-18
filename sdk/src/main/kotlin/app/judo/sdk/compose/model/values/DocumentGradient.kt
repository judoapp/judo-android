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

package app.judo.sdk.compose.model.values

import com.squareup.moshi.JsonClass

/**
 * [DocumentGradient] represents a [GradientValue] that is embedded within the document.
 * As such, this Document-level gradient definition can be used in multiple places within the [ExperienceModel].
 *
 * This is in contrast to a [GradientReference.CustomGradient], a single-use custom defined gradient
 * that can only be used in that specific place, and holds its own [GradientValue].
*/
@JsonClass(generateAdapter = true)
internal data class DocumentGradient(

    /**
     * The ID of Document Gradient, typically a UUID.
     */
    val id: String,

    /**
     * The designer-visible name assigned to the gradient.
     */
    val name: String,

    /**
     * The base version of the gradient.  Sometimes referred to as the “Universal” value.
     * If any of the other options are not supplied or do not apply, this version of the gradient is used instead.
     */
    val default: GradientValue,

    /**
     * The version of the gradient to use when the user has enabled Dark Mode.
     */
    val darkMode: GradientValue?,

    /**
     * The version of the gradient to use when the user has the “High Contrast” accessibility setting enabled.
     */
    val highContrast: GradientValue?,

    /**
     * The version of the gradient to use when the user has both the Dark Mode and “High Contrast” accessibility setting enabled.
     */
    val darkModeHighContrast: GradientValue?
)
