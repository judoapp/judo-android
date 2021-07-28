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

package app.judo.example

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import app.judo.example.core.functions.InMemoryExperience
import app.judo.example.databinding.ActivityExampleMainBinding
import app.judo.sdk.api.Judo
import java.util.*

class ExampleMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExampleMainBinding
    private lateinit var launchButton: Button
    private lateinit var identifyButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleMainBinding.inflate(layoutInflater)
        launchButton = binding.launchButton
        identifyButton = binding.identifyButton
        resetButton = binding.resetButton

        setContentView(binding.root)

        launchButton.setOnClickListener {
            // try using any of the three sample show*() methods given below!
            showStockExperienceActivityFromURL(
                // Change the experience URL below
                "<JUDO-EXPERIENCE-URL>"
            )
        }

        identifyButton.setOnClickListener {
            Judo.identify(
                userId = "john@example.com",
                traits = hashMapOf(
                    "premiumTier" to true,
                    "name" to "John Doe",
                    "tags" to listOf("foo", "bar", "baz"),
                    "pointsBalance" to "50000"
                )
            )
        }

        resetButton.setOnClickListener {
            Judo.reset()
        }
    }

    // The three methods below demonstrate different ways of launching a Judo experience with an explicit Intent.

    /**
     * Shows how to launch Experiences programmatically using an Intent.
     */
    private fun showStockExperienceActivityFromURL(url: String) {
        Judo.makeIntent(
            context = this,
            url = url,
            ignoreCache = true
        ).run { startActivity(this) }
    }

    /**
     * This method show you how to use your own custom Experience subclass, if you need it (such as,
     * perhaps, to integrate with certain Analytics solutions).
     */
    private fun showCustomExperienceActivityWithAURL(url: String) {
        Judo.makeIntent(
            context = this,
            url = url,
            activityClass = ExampleCustomExperienceActivity::class.java,
            ignoreCache = true
        ).run { startActivity(this) }
    }

    /**
     * You can also provide an in-memory definition of a Judo experience.
     */
    private fun showCustomExperienceActivityWithAnInMemoryExperience() {
        val initialScreenId = UUID.randomUUID().toString()

        val initialScreenIdOverride = initialScreenId.takeIf { listOf(1, 2).random() == 2 }
        Judo.makeIntent(
            context = this,
            activityClass = ExampleCustomExperienceActivity::class.java,
            experience = InMemoryExperience(initialScreenId),
            screenId = initialScreenIdOverride
        ).run { startActivity(this) }
    }

    private fun showStockExperienceActivityWithAnInMemoryExperience() {
        val initialScreenId = UUID.randomUUID().toString()

        val initialScreenIdOverride = initialScreenId.takeIf { listOf(1, 2).random() == 2 }
        Judo.makeIntent(
            context = this,
            experience = InMemoryExperience(initialScreenId),
            screenId = initialScreenIdOverride
        ).run { startActivity(this) }
    }

}