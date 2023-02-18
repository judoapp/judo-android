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

package app.judo.sdk.compose.ui.values

import app.judo.sdk.compose.model.values.ColorValue

internal val SystemColors: HashMap<String, SystemColor> = hashMapOf(
    Pair(
        "systemGray2",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.6823529411764706f, green = 0.6823529411764706f, blue = 0.6980392156862745f),
            dark = ColorValue(alpha = 1.0f, red = 0.38823529411764707f, green = 0.38823529411764707f, blue = 0.4f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.5568627450980392f, green = 0.5568627450980392f, blue = 0.5764705882352941f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.48627450980392156f, green = 0.48627450980392156f, blue = 0.5019607843137255f)
        )
    ),
    Pair(
        "link",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.47843137254901963f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.03529411764705882f, green = 0.5176470588235295f, blue = 1.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.47843137254901963f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.03529411764705882f, green = 0.5176470588235295f, blue = 1.0f)
        )
    ),
    Pair(
        "systemGreen",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.20392156862745098f, green = 0.7803921568627451f, blue = 0.34901960784313724f),
            dark = ColorValue(alpha = 1.0f, red = 0.18823529411764706f, green = 0.8196078431372549f, blue = 0.34509803921568627f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.1411764705882353f, green = 0.5411764705882353f, blue = 0.23921568627450981f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.18823529411764706f, green = 0.8588235294117647f, blue = 0.3568627450980392f)
        )
    ),
    Pair(
        "secondaryLabel",
        SystemColor(
            universal = ColorValue(alpha = 0.6f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            dark = ColorValue(alpha = 0.6f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f),
            highContrast = ColorValue(alpha = 0.8f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            darkHighContrast = ColorValue(alpha = 0.7f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f)
        )
    ),
    Pair(
        "systemPink",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.17647058823529413f, blue = 0.3333333333333333f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.21568627450980393f, blue = 0.37254901960784315f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.8274509803921568f, green = 0.058823529411764705f, blue = 0.27058823529411763f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.39215686274509803f, blue = 0.5098039215686274f)
        )
    ),
    Pair(
        "tertiarySystemFill",
        SystemColor(
            universal = ColorValue(alpha = 0.12f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f),
            dark = ColorValue(alpha = 0.24f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f),
            highContrast = ColorValue(alpha = 0.2f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f),
            darkHighContrast = ColorValue(alpha = 0.32f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f)
        )
    ),
    Pair(
        "opaqueSeparator",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.7764705882352941f, green = 0.7764705882352941f, blue = 0.7843137254901961f),
            dark = ColorValue(alpha = 1.0f, red = 0.2196078431372549f, green = 0.2196078431372549f, blue = 0.22745098039215686f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.7764705882352941f, green = 0.7764705882352941f, blue = 0.7843137254901961f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.2196078431372549f, green = 0.2196078431372549f, blue = 0.22745098039215686f)
        )
    ),
    Pair(
        "systemGray",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.5568627450980392f, green = 0.5568627450980392f, blue = 0.5764705882352941f),
            dark = ColorValue(alpha = 1.0f, red = 0.5568627450980392f, green = 0.5568627450980392f, blue = 0.5764705882352941f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.4235294117647059f, green = 0.4235294117647059f, blue = 0.4392156862745098f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.6823529411764706f, green = 0.6823529411764706f, blue = 0.6980392156862745f)
        )
    ),
    Pair(
        "systemGray3",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.7803921568627451f, green = 0.7803921568627451f, blue = 0.8f),
            dark = ColorValue(alpha = 1.0f, red = 0.2823529411764706f, green = 0.2823529411764706f, blue = 0.2901960784313726f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.6823529411764706f, green = 0.6823529411764706f, blue = 0.6980392156862745f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.32941176470588235f, green = 0.32941176470588235f, blue = 0.33725490196078434f)
        )
    ),
    Pair(
        "tertiarySystemGroupedBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.9490196078431372f, green = 0.9490196078431372f, blue = 0.9686274509803922f),
            dark = ColorValue(alpha = 1.0f, red = 0.17254901960784313f, green = 0.17254901960784313f, blue = 0.1803921568627451f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9411764705882353f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.21176470588235294f, green = 0.21176470588235294f, blue = 0.2196078431372549f)
        )
    ),
    Pair(
        "orange",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.5f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.5f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.5f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.5f, blue = 0.0f)
        )
    ),
    Pair(
        "systemFill",
        SystemColor(
            universal = ColorValue(alpha = 0.2f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            dark = ColorValue(alpha = 0.36f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            highContrast = ColorValue(alpha = 0.28f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            darkHighContrast = ColorValue(alpha = 0.44f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f)
        )
    ),
    Pair(
        "yellow",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 0.0f)
        )
    ),
    Pair(
        "tertiaryLabel",
        SystemColor(
            universal = ColorValue(alpha = 0.3f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            dark = ColorValue(alpha = 0.3f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f),
            highContrast = ColorValue(alpha = 0.7f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            darkHighContrast = ColorValue(alpha = 0.55f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f)
        )
    ),
    Pair(
        "tertiarySystemBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.17254901960784313f, green = 0.17254901960784313f, blue = 0.1803921568627451f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.21176470588235294f, green = 0.21176470588235294f, blue = 0.2196078431372549f)
        )
    ),
    Pair(
        "lightText",
        SystemColor(
            universal = ColorValue(alpha = 0.6f, red = 1.0f, green = 1.0f, blue = 1.0f),
            dark = ColorValue(alpha = 0.6f, red = 1.0f, green = 1.0f, blue = 1.0f),
            highContrast = ColorValue(alpha = 0.6f, red = 1.0f, green = 1.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 0.6f, red = 1.0f, green = 1.0f, blue = 1.0f)
        )
    ),
    Pair(
        "separator",
        SystemColor(
            universal = ColorValue(alpha = 0.29f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            dark = ColorValue(alpha = 0.6f, red = 0.32941176470588235f, green = 0.32941176470588235f, blue = 0.34509803921568627f),
            highContrast = ColorValue(alpha = 0.37f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            darkHighContrast = ColorValue(alpha = 0.6799999999999999f, red = 0.32941176470588235f, green = 0.32941176470588235f, blue = 0.34509803921568627f)
        )
    ),
    Pair(
        "systemRed",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.23137254901960785f, blue = 0.18823529411764706f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.27058823529411763f, blue = 0.22745098039215686f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.8431372549019608f, green = 0.0f, blue = 0.08235294117647059f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.4117647058823529f, blue = 0.3803921568627451f)
        )
    ),
    Pair(
        "black",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemYellow",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.8f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.8392156862745098f, blue = 0.0392156862745098f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.6980392156862745f, green = 0.3137254901960784f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.8313725490196079f, blue = 0.14901960784313725f)
        )
    ),
    Pair(
        "label",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f)
        )
    ),
    Pair(
        "clear",
        SystemColor(
            universal = ColorValue(alpha = 0.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            dark = ColorValue(alpha = 0.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 0.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 0.0f, red = 0.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemGroupedBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.9490196078431372f, green = 0.9490196078431372f, blue = 0.9686274509803922f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9411764705882353f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemIndigo",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.34509803921568627f, green = 0.33725490196078434f, blue = 0.8392156862745098f),
            dark = ColorValue(alpha = 1.0f, red = 0.3686274509803922f, green = 0.3607843137254902f, blue = 0.9019607843137255f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.21176470588235294f, green = 0.20392156862745098f, blue = 0.6392156862745098f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.49019607843137253f, green = 0.47843137254901963f, blue = 1.0f)
        )
    ),
    Pair(
        "purple",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.0f, blue = 0.5f),
            dark = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.0f, blue = 0.5f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.0f, blue = 0.5f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.0f, blue = 0.5f)
        )
    ),
    Pair(
        "quaternarySystemFill",
        SystemColor(
            universal = ColorValue(alpha = 0.08f, red = 0.4549019607843137f, green = 0.4549019607843137f, blue = 0.5019607843137255f),
            dark = ColorValue(alpha = 0.18f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f),
            highContrast = ColorValue(alpha = 0.16f, red = 0.4549019607843137f, green = 0.4549019607843137f, blue = 0.5019607843137255f),
            darkHighContrast = ColorValue(alpha = 0.26f, red = 0.4627450980392157f, green = 0.4627450980392157f, blue = 0.5019607843137255f)
        )
    ),
    Pair(
        "systemOrange",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.5843137254901961f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.6235294117647059f, blue = 0.0392156862745098f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.788235294117647f, green = 0.20392156862745098f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.7019607843137254f, blue = 0.25098039215686274f)
        )
    ),
    Pair(
        "darkText",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemTeal",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.18823529411764706f, green = 0.6901960784313725f, blue = 0.7803921568627451f),
            dark = ColorValue(alpha = 1.0f, red = 0.25098039215686274f, green = 0.7843137254901961f, blue = 0.8784313725490196f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.5098039215686274f, blue = 0.6f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.36470588235294116f, green = 0.9019607843137255f, blue = 1.0f)
        )
    ),
    Pair(
        "systemGray5",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.8980392156862745f, green = 0.8980392156862745f, blue = 0.9176470588235294f),
            dark = ColorValue(alpha = 1.0f, red = 0.17254901960784313f, green = 0.17254901960784313f, blue = 0.1803921568627451f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.8470588235294118f, green = 0.8470588235294118f, blue = 0.8627450980392157f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.21176470588235294f, green = 0.21176470588235294f, blue = 0.2196078431372549f)
        )
    ),
    Pair(
        "systemBlue",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.47843137254901963f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0392156862745098f, green = 0.5176470588235295f, blue = 1.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.25098039215686274f, blue = 0.8666666666666667f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.25098039215686274f, green = 0.611764705882353f, blue = 1.0f)
        )
    ),
    Pair(
        "secondarySystemGroupedBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.10980392156862745f, green = 0.10980392156862745f, blue = 0.11764705882352941f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.1411764705882353f, green = 0.1411764705882353f, blue = 0.14901960784313725f)
        )
    ),
    Pair(
        "gray",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.5f, blue = 0.5f),
            dark = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.5f, blue = 0.5f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.5f, blue = 0.5f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.5f, green = 0.5f, blue = 0.5f)
        )
    ),
    Pair(
        "systemPurple",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.6862745098039216f, green = 0.3215686274509804f, blue = 0.8705882352941177f),
            dark = ColorValue(alpha = 1.0f, red = 0.7490196078431373f, green = 0.35294117647058826f, blue = 0.9490196078431372f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.5372549019607843f, green = 0.26666666666666666f, blue = 0.6705882352941176f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.8549019607843137f, green = 0.5607843137254902f, blue = 1.0f)
        )
    ),
    Pair(
        "red",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 0.0f, blue = 0.0f)
        )
    ),
    Pair(
        "systemGray6",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.9490196078431372f, green = 0.9490196078431372f, blue = 0.9686274509803922f),
            dark = ColorValue(alpha = 1.0f, red = 0.10980392156862745f, green = 0.10980392156862745f, blue = 0.11764705882352941f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9411764705882353f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.1411764705882353f, green = 0.1411764705882353f, blue = 0.14901960784313725f)
        )
    ),
    Pair(
        "green",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 1.0f, blue = 0.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 1.0f, blue = 0.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 1.0f, blue = 0.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 1.0f, blue = 0.0f)
        )
    ),
    Pair(
        "placeholderText",
        SystemColor(
            universal = ColorValue(alpha = 0.3f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            dark = ColorValue(alpha = 0.3f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f),
            highContrast = ColorValue(alpha = 0.7f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            darkHighContrast = ColorValue(alpha = 0.55f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f)
        )
    ),
    Pair(
        "secondarySystemFill",
        SystemColor(
            universal = ColorValue(alpha = 0.16f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            dark = ColorValue(alpha = 0.32f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            highContrast = ColorValue(alpha = 0.24f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f),
            darkHighContrast = ColorValue(alpha = 0.4f, red = 0.47058823529411764f, green = 0.47058823529411764f, blue = 0.5019607843137255f)
        )
    ),
    Pair(
        "quaternaryLabel",
        SystemColor(
            universal = ColorValue(alpha = 0.18f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            dark = ColorValue(alpha = 0.16f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f),
            highContrast = ColorValue(alpha = 0.55f, red = 0.23529411764705882f, green = 0.23529411764705882f, blue = 0.2627450980392157f),
            darkHighContrast = ColorValue(alpha = 0.4f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9607843137254902f)
        )
    ),
    Pair(
        "systemGray4",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.8196078431372549f, green = 0.8196078431372549f, blue = 0.8392156862745098f),
            dark = ColorValue(alpha = 1.0f, red = 0.22745098039215686f, green = 0.22745098039215686f, blue = 0.23529411764705882f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.7372549019607844f, green = 0.7372549019607844f, blue = 0.7529411764705882f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.26666666666666666f, green = 0.26666666666666666f, blue = 0.27450980392156865f)
        )
    ),
    Pair(
        "blue",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 1.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.0f, green = 0.0f, blue = 1.0f)
        )
    ),
    Pair(
        "white",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            dark = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            highContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 1.0f, green = 1.0f, blue = 1.0f)
        )
    ),
    Pair(
        "secondarySystemBackground",
        SystemColor(
            universal = ColorValue(alpha = 1.0f, red = 0.9490196078431372f, green = 0.9490196078431372f, blue = 0.9686274509803922f),
            dark = ColorValue(alpha = 1.0f, red = 0.10980392156862745f, green = 0.10980392156862745f, blue = 0.11764705882352941f),
            highContrast = ColorValue(alpha = 1.0f, red = 0.9215686274509803f, green = 0.9215686274509803f, blue = 0.9411764705882353f),
            darkHighContrast = ColorValue(alpha = 1.0f, red = 0.1411764705882353f, green = 0.1411764705882353f, blue = 0.14901960784313725f)
        )
    )
)
