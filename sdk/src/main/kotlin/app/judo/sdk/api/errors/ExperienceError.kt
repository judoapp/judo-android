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

package app.judo.sdk.api.errors

sealed class ExperienceError : Throwable() {

    data class NetworkError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class MalformedExperienceError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class ExperienceNotFoundError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    // TODO: 2021-03-23 - Add UnauthorizedAccessError for 401 responses

    data class UnexpectedError(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

    data class NotInitialized(
        override val message: String? = null,
        override val cause: Throwable? = null
    ): ExperienceError()

}