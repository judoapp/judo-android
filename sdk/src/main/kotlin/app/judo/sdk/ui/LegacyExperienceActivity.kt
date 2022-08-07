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

package app.judo.sdk.ui

import androidx.appcompat.app.AppCompatActivity
import app.judo.sdk.api.android.ExperienceFragmentFactory
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.databinding.JudoSdkExperienceActivityLayoutBinding
import app.judo.sdk.ui.extensions.commitNow

open class LegacyExperienceActivity : AppCompatActivity() {

    private lateinit var binding: JudoSdkExperienceActivityLayoutBinding
    private lateinit var fragment: ExperienceFragment

    override fun onStart() {
        super.onStart()
        navigateToExperienceFragment()
    }

    /**
     * Called during [ExperienceActivity.onStart] to show the [ExperienceFragment]
     * provided from the [ExperienceFragmentFactory].
     *
     * Unless overridden any calls after the first will have no effect.
     */
    open fun navigateToExperienceFragment() {
        if (!this::fragment.isInitialized) {

            binding = JudoSdkExperienceActivityLayoutBinding.inflate(layoutInflater)

            setContentView(binding.root)

            fragment = Environment.current
                .experienceFragmentFactory
                .create(intent)

            supportFragmentManager.commitNow {
                setPrimaryNavigationFragment(fragment)
                replace(binding.frame.id, fragment)
            }
        }
    }

}