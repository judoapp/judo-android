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

package app.judo.sdk.compose.ui.vendor.foundation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.ui.Modifier

/**
 * Modifier to apply the overscroll as specified by [OverscrollEffect]
 *
 * This modifier is a convenience method to call [OverscrollEffect.effectModifier], which
 * performs the actual overscroll logic. Note that this modifier is the representation of the
 * overscroll on the UI, to make overscroll events to be propagated to the [OverscrollEffect],
 * you have to pass it to [scrollable].
 *
 * @sample androidx.compose.foundation.samples.OverscrollSample
 *
 * @param overscrollEffect controller that defines the behavior and the overscroll state.
 */
@ExperimentalFoundationApi
fun Modifier.overscroll(overscrollEffect: OverscrollEffect): Modifier =
    this.then(overscrollEffect.effectModifier)