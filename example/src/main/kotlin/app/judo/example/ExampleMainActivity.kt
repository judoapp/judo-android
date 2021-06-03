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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExampleMainBinding.inflate(layoutInflater)
        launchButton = binding.launchButton
        setContentView(binding.root)

        launchButton.setOnClickListener {
            // try using any of the three sample show*() methods given below!
            showStockExperienceActivityFromURL(
                // Change the experience URL below
                "https://brand1.judo.app/myExperience"
            )
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