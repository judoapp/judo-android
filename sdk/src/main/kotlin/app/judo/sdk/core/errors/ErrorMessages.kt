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

package app.judo.sdk.core.errors

internal object ErrorMessages {
        const val ACCESS_TOKEN_NOT_BLANK: String = "The accessToken can not be blank"
        const val DOMAIN_NAME_NOT_BLANK: String = "The domainName can not be blank"
        const val SDK_NOT_INITIALIZED: String = "The JudoSDK has not been initialized"
        const val EXPERIENCE_NOT_IN_MEMORY: String = "Tried to find a Experience in memory and it was not there"
        const val EXPERIENCE_NOT_FOUND: String = "Server could not find Experience"
        const val UNKNOWN_ERROR: String = "Unknown reason \uD83E\uDD37\u200D"

        @Suppress("FunctionName")
        fun EXTEND_EXPERIENCE_ACTIVITY(activityClass: Class<*>): String {
                return "Class: ${activityClass.canonicalName} does not extend ExperienceActivity."
        }

    }
