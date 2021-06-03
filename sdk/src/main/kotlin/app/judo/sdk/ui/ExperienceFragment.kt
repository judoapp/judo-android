package app.judo.sdk.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.R
import app.judo.sdk.api.errors.ExperienceError
import app.judo.sdk.api.models.Action
import app.judo.sdk.api.models.Action.*
import app.judo.sdk.api.models.Experience
import app.judo.sdk.api.models.Screen
import app.judo.sdk.api.models.SegueStyle
import app.judo.sdk.core.controllers.current
import app.judo.sdk.core.environment.Environment
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_INTENT
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_KEY
import app.judo.sdk.core.environment.Environment.Keys.EXPERIENCE_URL
import app.judo.sdk.core.environment.Environment.Keys.IGNORE_CACHE
import app.judo.sdk.core.environment.Environment.Keys.LOAD_FROM_MEMORY
import app.judo.sdk.core.environment.Environment.Keys.SCREEN_ID
import app.judo.sdk.databinding.JudoSdkExperienceFragmentLayoutBinding
import app.judo.sdk.databinding.JudoSdkUnsupportedVersionLayoutBinding
import app.judo.sdk.ui.events.ExperienceRequested
import app.judo.sdk.ui.extensions.getCustomTabsIntent
import app.judo.sdk.ui.extensions.toUri
import app.judo.sdk.ui.extensions.viewModels
import app.judo.sdk.ui.factories.ExperienceViewModelFactory
import app.judo.sdk.ui.layout.ScreenFragment
import app.judo.sdk.ui.models.ExperienceState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import okhttp3.MediaType
import okhttp3.RequestBody

open class ExperienceFragment : Fragment() {

    companion object {

        private const val TAG = "ExperienceFragment"

        @MainThread
        fun ExperienceFragment.applyArguments(
            intent: Intent
        ): ExperienceFragment = apply {
            arguments = (arguments ?: Bundle()).apply {
                putParcelable(EXPERIENCE_INTENT, intent)
            }
        }

    }

    protected lateinit var container: FrameLayout

    private var _binding: JudoSdkExperienceFragmentLayoutBinding? = null
    private val binding: JudoSdkExperienceFragmentLayoutBinding
        get() = _binding!!

    private var _unsupportedBinding: JudoSdkUnsupportedVersionLayoutBinding? = null
    private val unsupportedBinding: JudoSdkUnsupportedVersionLayoutBinding
        get() = _unsupportedBinding!!

    private val model: ExperienceViewModel by viewModels {
        ExperienceViewModelFactory()
    }

    private var hostStatusBarState: StatusBarState? = null

    private val roots = ArrayDeque<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = JudoSdkExperienceFragmentLayoutBinding.inflate(inflater, container, false)
        _unsupportedBinding =
            JudoSdkUnsupportedVersionLayoutBinding.inflate(inflater, container, false)
        this.container = binding.container
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hostStatusBarState = captureHostStatusBarState()
            listenForActions()
            listenForStateChanges()
            requestExperience()
        } else {
            onUnsupportedAndroidPlatformVersion()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            restoreHostStatusBarState()
        }
        _unsupportedBinding = null
        _binding = null
    }

    private fun captureHostStatusBarState(): StatusBarState {
        val window = requireActivity().window

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            StatusBarState.RStatusBarState(
                window.statusBarColor,
                window.decorView.windowInsetsController?.systemBarsAppearance
            )
        } else {
            StatusBarState.BelowRStatusBarState(
                window.statusBarColor,
                window.decorView.windowSystemUiVisibility
            )
        }
    }

    private fun requestExperience() {
        val parcelable = arguments?.getParcelable<Intent>(EXPERIENCE_INTENT)
        val intent = activity?.intent
        (parcelable ?: intent)?.let {
            model.onEvent(
                ExperienceRequested(
                    it
                )
            )
        }
    }

    private fun listenForStateChanges() {

        render(model.stateFlow.value)

        lifecycleScope.launchWhenStarted {
            model.stateFlow.collect { state ->
                render(state)
            }
        }
    }

    private fun render(state: ExperienceState) {
        when (state) {
            is ExperienceState.Empty -> onEmpty()

            is ExperienceState.Loading -> onLoading()

            is ExperienceState.Error -> onError(error = state.error)

            is ExperienceState.Retrieved -> {
                onRetrieved(experience = state.experience)
                container.removeAllViews()
                navigate(
                    destination = state.screenId ?: state.experience.initialScreenID,
                    style = SegueStyle.MODAL,
                    addToBackStack = false
                )
            }
        }
    }

    private fun listenForActions() {
        lifecycleScope.launchWhenStarted {
            model.eventFlow.filterIsInstance<Action>().collect { action ->
                handleAction(action)
            }
        }
    }

    private fun restoreHostStatusBarState() {
        val window = requireActivity().window
        when (val state = hostStatusBarState) {
            is StatusBarState.RStatusBarState -> {
                state.systemBarAppearance?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        window.decorView.windowInsetsController?.setSystemBarsAppearance(
                            it,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                }
                window.statusBarColor = state.color
            }
            is StatusBarState.BelowRStatusBarState -> {
                window.decorView.systemUiVisibility = state.windowSystemVisibility
                window.statusBarColor = state.color
            }
        }
    }

    private fun showSoftFailureDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage("Failed to load Experience")
            setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
                close()
            }
            setPositiveButton("Try Again") { dialogInterface, _ ->
                dialogInterface.dismiss()
                requestExperience()
            }
        }.show()
    }

    private fun showHardFailureDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage("Something went wrong")
            setNegativeButton("Ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
                close()
            }
        }.show()
    }

    private fun popStackOrFinish() {
        if (roots.isEmpty()) {
            restoreHostStatusBarState()
            close()
        } else {
            roots.removeFirstOrNull()?.let { id ->
                childFragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    private fun navigate(destination: String, style: SegueStyle, addToBackStack: Boolean = true) {

        val containerId = binding.container.id

        val tag = if (style == SegueStyle.MODAL) {
            /*
            * MODAL conceptually represents the start of a new stack of screens.
            */
            if (addToBackStack)
                roots.addFirst(destination)
            destination
        } else {
            null
        }

        val screenFragment = initializeScreenFragment(destination)

        childFragmentManager.beginTransaction().apply {

            setPrimaryNavigationFragment(screenFragment)

            if (addToBackStack) {
                addToBackStack(tag)
                when (style) {
                    SegueStyle.PUSH -> {
                        // Slide In Fade Out
                        setCustomAnimations(
                            R.anim.judo_sdk_in_from_right,
                            R.anim.judo_sdk_fade_out,
                            R.anim.judo_sdk_fade_in,
                            R.anim.judo_sdk_out_to_right
                        )
                    }
                    // Fade In Fade Out
                    SegueStyle.MODAL -> {
                        setCustomAnimations(
                            R.anim.judo_sdk_fade_in,
                            R.anim.judo_sdk_fade_out,
                            R.anim.judo_sdk_fade_in,
                            R.anim.judo_sdk_fade_out
                        )
                    }

                }
                replace(containerId, screenFragment, tag)
                commit()
            } else {
                replace(containerId, screenFragment, tag)
                commitNow()
            }
        }
    }

    private fun initializeScreenFragment(screenID: String): ScreenFragment {
        val screens =
            (model.stateFlow.value as ExperienceState.Retrieved).experience.nodes<Screen>()
        val initialScreen = screens.find { node -> node.id == screenID }!!
        return ScreenFragment.newInstance(initialScreen.id)
    }

    open fun handleAction(action: Action) {
        when (action) {
            is Close -> {
                popStackOrFinish()
            }

            is PerformSegue -> {
                navigate(action.screenID, action.segueStyle)
            }

            is OpenURL -> {
                if (action.dismissExperience) requireActivity().finish()
                startActivity(Intent(Intent.ACTION_VIEW, action.url.toUri()))
            }

            is PresentWebsite -> {
                //TODO: enable changing toolbar color
                startActivity(action.url.toUri().getCustomTabsIntent())
            }
            is Custom -> {
                if (action.dismissExperience) requireActivity().finish()
            }
        }

    }

    open fun close() {
        requireActivity().finish()
    }

    /**
     * Called during [Fragment.onViewCreated] if the Android SDK version is below 23.
     */
    open fun onUnsupportedAndroidPlatformVersion() {
        container.addView(unsupportedBinding.root)
    }

    /**
     * Called during [Fragment.onViewCreated] and before any data is loaded.
     */
    open fun onEmpty() {
        /* no-op */
    }

    /**
     * Called whenever a [Experience] is being retrieved from a data source.
     *
     * This function will be skipped if retrieving a [Experience] from memory.
     */
    open fun onLoading() {
        /* no-op */
    }

    /**
     * Called when there is some kind of [ExperienceError].
     *
     *
     * A basic [AlertDialog] will be displayed with one of the two configurations
     * depending on two failure scenarios, "hard" and "soft" failure.
     *
     *
     * For soft failures:
     * A two button dialog offering an opportunity to retry loading the Experience or exit the activity.
     *
     *
     * For hard failures:
     * A one button dialog forcing the user to exit the activity.
     */
    open fun onError(error: ExperienceError) {
        when (error) {
            is ExperienceError.NetworkError -> {
                showSoftFailureDialog()
            }

            is ExperienceError.MalformedExperienceError -> {
                showHardFailureDialog()
            }

            is ExperienceError.ExperienceNotFoundError -> {
                showHardFailureDialog()
            }

            is ExperienceError.UnexpectedError -> {
                showHardFailureDialog()
            }

            is ExperienceError.NotInitialized -> {
                showHardFailureDialog()
            }
        }
    }

    /**
     * Called whenever a [Experience] has been successfully retrieved from a data source.
     */
    open fun onRetrieved(experience: Experience) {
        /* no-op */
    }
}

private sealed class StatusBarState {
    data class RStatusBarState(@ColorInt val color: Int, val systemBarAppearance: Int?) :
        StatusBarState()

    data class BelowRStatusBarState(@ColorInt val color: Int, val windowSystemVisibility: Int) :
        StatusBarState()
}
