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

@Suppress("SpellCheckingInspection")
enum class FontStyle(val code: String) {
    LARGE_TITLE("largeTitle"),
    TITLE_1("title"),
    TITLE_2("title2"),
    TITLE_3("title3"),
    HEADLINE("headline"),
    BODY("body"),
    CALLOUT("callout"),
    SUBHEADLINE("subheadline"),
    FOOTNOTE("footnote"),
    CAPTION_1("caption"),
    CAPTION_2("caption2");

    companion object {
        fun getStyleFromCode(code: String): FontStyle {
            return values().find { it.code == code } ?: throw RuntimeException("Incorrect font style code ${code}")
        }
    }
}