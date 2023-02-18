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
 * [DocumentColor]s are shared colors, that may be referenced in multiple places throughout the document.
 * They also feature an overrides & selectors feature for applying different colors depending
 * on [darkMode] or [highContrast] mode.
 */
@JsonClass(generateAdapter = true)
internal data class DocumentColor(

    /**
     * The ID of Document Color, typically a UUID.
     */
    val id: String,

    /**
     * The designer-visible name assigned to the color.
     */
    val name: String,

    /**
     * The base version of the colour.  Sometimes referred to as the “Universal” colour.
     * If any of the other options are not supplied or do not apply, use this version of the colour.
     */
    val default: ColorValue,

    /**
     * The version of the colour to use when the user has enabled Dark Mode.
     */
    val darkMode: ColorValue?,

    /**
     * The version of the colour to use when the user has the “High Contrast” accessibility setting enabled.
     */
    val highContrast: ColorValue?,

    /**
     * The version of the colour to use when the user has both the Dark Mode and
     * “High Contrast” accessibility setting enabled.
     */
    val darkModeHighContrast: ColorValue?
)
