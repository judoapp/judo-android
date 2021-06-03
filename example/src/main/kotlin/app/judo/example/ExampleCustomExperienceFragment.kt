package app.judo.example

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Experience
import app.judo.sdk.ui.ExperienceFragment
import app.judo.example.databinding.ExampleCustomExperienceFragmentBinding

class ExampleCustomExperienceFragment : ExperienceFragment() {

    companion object {
        private const val TAG = "MyExperienceFragment"
    }

    private var _binding: ExampleCustomExperienceFragmentBinding? = null
    private lateinit var binding: ExampleCustomExperienceFragmentBinding
    private lateinit var root: ConstraintLayout
    private lateinit var status: TextView
    private lateinit var ctaButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ExampleCustomExperienceFragmentBinding.inflate(
            layoutInflater,
            container,
            false
        )
        root = binding.root
        status = binding.status
        ctaButton = binding.callToActionButton.apply {
            setOnClickListener { activity?.finish() }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Add any custom layout to the provided FrameLayout container
        container.addView(binding.root)
    }

    override fun onUnsupportedAndroidPlatformVersion() {
        status.text = getString(R.string.message_empty_state)
    }

    override fun onEmpty() {
        status.text = getString(R.string.message_empty_state)
    }

    override fun onLoading() {
        binding.root.setBackgroundColor(getColor(requireContext(), R.color.colorWarning))
        status.text = getString(R.string.message_loading_state)
    }

    override fun onError(error: ExperienceError) {
        val errorMessage = when (error) {
            is ExperienceError.NetworkError -> {
                getString(R.string.message_error_state_network)
            }

            is ExperienceError.MalformedExperienceError -> {
                getString(R.string.message_error_state_server)
            }

            is ExperienceError.ExperienceNotFoundError -> {
                getString(R.string.message_error_state_server)
            }

            is ExperienceError.NotInitialized -> {
                getString(R.string.message_error_state_unknown)
            }

            is ExperienceError.UnexpectedError -> {
                getString(R.string.message_error_state_unknown)
            }
        }

        status.text = errorMessage
        ctaButton.visibility = View.VISIBLE
        root.setBackgroundColor(getColor(requireContext(), R.color.colorError))
    }

    override fun onRetrieved(experience: Experience) {
        Log.i(TAG, "Experience retrieved: ${experience.id}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}