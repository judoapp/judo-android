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
internal enum class NodeType(val code: String) {
    IMAGE("Image"),
    RECTANGLE("Rectangle"),
    WEB("WebView"),
    TEXT("Text"),
    SCREEN("Screen"),
    ZSTACK("ZStack"),
    SPACER("Spacer"),
    VSTACK("VStack"),
    HSTACK("HStack"),
    CAROUSEL("Carousel"),
    COLLECTION("Collection"),
    DATA_SOURCE("DataSource"),
    PAGE_CONTROL("PageControl"),
    SCROLL_CONTAINER("ScrollContainer"),
    AUDIO("Audio"),
    VIDEO("Video"),
    ICON("Icon"),
    DIVIDER("Divider"),
    APP_BAR("AppBar"),
    CONDITIONAL("Conditional"),
    MENU_ITEM("AppBarMenuItem");
}