package app.judo.sdk.ui.layout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Layer
import app.judo.sdk.api.models.NodeContainer
import app.judo.sdk.core.data.resolvers.ColorResolver
import app.judo.sdk.core.data.resolvers.GradientResolver
import app.judo.sdk.core.data.resolvers.StatusBarColorResolver
import app.judo.sdk.databinding.JudoSdkScreenFragmentLayoutBinding
import app.judo.sdk.ui.*
import app.judo.sdk.ui.ExperienceViewModel
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.extensions.bundleOf
import app.judo.sdk.ui.factories.ExperienceViewModelFactory
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.positioning.computePosition
import app.judo.sdk.ui.views.CustomStyledPlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ScreenFragment : Fragment() {

    companion object {

        private const val KEY_SCREEN_ID = "SCREEN_ID"

        fun newInstance(screenID: String) = ScreenFragment().apply {
            arguments = bundleOf(KEY_SCREEN_ID to screenID)
        }
    }

    private var _binding: JudoSdkScreenFragmentLayoutBinding? = null
    private val binding: JudoSdkScreenFragmentLayoutBinding
        get() = _binding!!

    private val model: ExperienceViewModel by viewModels({ requireParentFragment() }) {
        ExperienceViewModelFactory()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = JudoSdkScreenFragmentLayoutBinding.inflate(inflater, container, false)

        arguments?.getString(KEY_SCREEN_ID)?.let { screenId ->
            container?.let { containerView ->
                initializeComponent(screenId)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        releaseMedia()
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        pauseMedia()
        super.onPause()
    }

    private fun releaseMedia() {
        model.getNodes().filter { it is Audio || it is Video }.forEach {
            binding.screenFrame.findViewWithTag<CustomStyledPlayerView>(it.id)?.player?.release()
        }
    }

    private fun pauseMedia() {
        model.getNodes().filter { it is Audio || it is Video }.forEach {
            binding.screenFrame.findViewWithTag<CustomStyledPlayerView>(it.id)?.player?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        playMedia()
    }

    private fun playMedia() {
        model.getNodes().filter { it is Audio && it.autoPlay || it is Video && it.autoPlay }.forEach {
            binding.screenFrame.findViewWithTag<CustomStyledPlayerView>(it.id)?.playIfVisibleOrPause()
        }
    }

    private fun initializeComponent(screenID: String) {
        binding.screenFrame.removeAllViews()

        val screen = model.getNodeByID<Screen>(screenID)
        val appearance = model.getAppearance()
        val colorResolver = ColorResolver(requireContext(), appearance)

        screen.let {
            with(requireActivity().window) {
                statusBarColor = colorResolver.resolveForColorInt(it.backgroundColor)
                setStatusBarIconTint(it.statusBarStyle, appearance)
            }
        }

        val color = colorResolver.resolveForColorInt(screen.backgroundColor)
        binding.screenFrame.setBackgroundColor(color)

        listenForUpdates(screenID)
    }

    private fun constructLayerNodeTrees(currentNode: TreeNode, nodes: List<Node>, appearance: Appearance) {
        (currentNode.value as? NodeContainer)?.let { nodeContainer ->
            nodeContainer.getChildNodeIDs().forEach { childID ->
                val node = nodes.find { it.id == childID }
                node?.let {
                    val child = TreeNode(node, depth = currentNode.depth + 1, appearance = appearance)
                    currentNode.addChild(child)
                    constructLayerNodeTrees(child, nodes, appearance)
                }
            }
        }
    }

    private fun listenForUpdates(screenID: String) {
        val appearance = model.getAppearance()

        val resolvers = Resolvers(
            ColorResolver(requireContext(), appearance),
            GradientResolver(requireContext(), appearance),
            model::onEvent,
            StatusBarColorResolver(requireActivity().window.statusBarColor)
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.nodesFlowForScreen(screenID).collect { nodes ->
                if (nodes.isNotEmpty()) {
                    binding.screenFrame.doOnLayout {

                        val screen = nodes.find { it.id == screenID } ?: return@doOnLayout
                        val screenNode = TreeNode(screen)
                        constructLayerNodeTrees(screenNode, nodes, appearance)

                        screenNode.clearSizeAndPositioning()

                        screenNode.computeSize(
                            requireContext(),
                            Dimensions(
                                Dimension.Value(binding.screenFrame.width.toFloat()),
                                Dimension.Value(binding.screenFrame.height.toFloat())
                            )
                        )

                        (screenNode.value as Screen).computePosition(requireContext(), screenNode)

                        val views = screenNode.children.reversed().flatMap {
                            it.toLayout(requireContext(), resolvers)
                        }

                        binding.screenFrame.removeAllViews()
                        binding.screenFrame.doOnPreDraw {
                            views.forEach {
                                binding.screenFrame.addView(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

