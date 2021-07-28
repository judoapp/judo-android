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

package app.judo.sdk.core.implementations

import app.judo.sdk.core.services.ProfileService

class TheProfileService: ProfileService {
    override var anonymousId: String = "6B722BB7-2DD6-4A0E-AAB2-A31B5BDE1B26"

    override var userId: String? = "51C37C89-3A51-4A47-ADE1-C01B81DA964A"

    override fun reset() {
        /* no-op in mock */
    }

    override fun identify(userId: String?, traits: Map<String, Any>) {
        /* no-op in mock */
    }

    override var userInfo: Map<String, Any> = emptyMap()

    override val traits: Map<String, Any>
        get() {
            /* no-op in mock */
            return emptyMap()
        }
}