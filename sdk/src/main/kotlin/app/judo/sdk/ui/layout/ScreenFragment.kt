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

package app.judo.sdk.ui.layout

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import app.judo.sdk.R
import android.view.MenuItem as AndroidMenuItem
import app.judo.sdk.api.models.*
import app.judo.sdk.core.data.resolvers.ColorResolver
import app.judo.sdk.core.data.resolvers.GradientResolver
import app.judo.sdk.core.data.resolvers.StatusBarColorResolver
import app.judo.sdk.databinding.JudoSdkScreenFragmentLayoutBinding
import app.judo.sdk.ui.*
import app.judo.sdk.ui.extensions.*
import app.judo.sdk.ui.factories.ExperienceViewModelFactory
import app.judo.sdk.ui.layout.composition.*
import app.judo.sdk.ui.layout.composition.positioning.computePosition
import app.judo.sdk.ui.views.ExperienceMediaPlayerView
import app.judo.sdk.ui.views.ExperienceScrollView
import app.judo.sdk.ui.views.HorizontalExperienceScrollView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

internal class ScreenFragment : Fragment() {

    companion object {

        private const val KEY_SCREEN_ID = "SCREEN_ID"

        fun newInstance(screenID: String) = ScreenFragment().apply {
            arguments = bundleOf(KEY_SCREEN_ID to screenID)
        }
    }

    private var screen: Screen? = null
    private var _binding: JudoSdkScreenFragmentLayoutBinding? = null
    private val binding: JudoSdkScreenFragmentLayoutBinding
        get() = _binding!!

    private val model: ExperienceViewModel by viewModels({ requireParentFragment() }) {
        ExperienceViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun releaseMedia() {
        MediaPlayerInstanceManager.releaseInstances()
    }

    private var currentLifecycleObserver: LifecycleObserver? = null
        set(value) {
            field?.let { lifecycle.removeObserver(it) }
            value?.let { lifecycle.addObserver(it) }
            field = value
        }


    override fun onResume() {
        super.onResume()
        screen?.let { model.informScreenViewed(it) }
    }

    private fun setupAppBar(appBar: AppBar, menuItems: List<MenuItem>, resolvers: Resolvers) {
        binding.toolbar.setupJudoAppBar(appBar, menuItems, screen, resolvers) { requireActivity().onBackPressed() }
    }

    private fun initializeComponent(screenID: String) {
        binding.screenFrame.removeAllViews()
        binding.screenFrame2.removeAllViews()

        screen = model.getNodeByID<Screen>(screenID)

        val screen = this.screen ?: return
        val appearance = model.getAppearance()
        val colorResolver = ColorResolver(requireContext(), appearance)

        screen?.let {
            with(requireActivity().window) {
                statusBarColor =
                    colorResolver.resolveForColorInt(it.androidStatusBarBackgroundColor)
                setStatusBarIconTint(it.androidStatusBarStyle, appearance)
            }
        }
        screen?.backgroundColor?.let {
            val color = colorResolver.resolveForColorInt(screen.backgroundColor)
            (requireParentFragment() as? ExperienceFragment)?.view?.setBackgroundColor(color)
            binding.screenFrame.setBackgroundColor(color)
            binding.screenFrame2.setBackgroundColor(color)
        }

        val resolvers = Resolvers(
            colorResolver,
            GradientResolver(requireContext(), appearance),
            model::onAction,
            StatusBarColorResolver(requireActivity().window.statusBarColor)
        )

        model.screenLayoutCache.get(screenID)?.let {
            layoutFromCache(screenID, it, appearance, resolvers)
        } ?: listenForUpdates(screenID, appearance, resolvers)
    }

    private fun constructLayerNodeTrees(
        currentNode: TreeNode,
        nodes: List<Node>,
        appearance: Appearance
    ) {
        (currentNode.value as? NodeContainer)?.let { nodeContainer ->
            nodeContainer.getChildNodeIDs().forEach { childID ->
                val node = nodes.find { it.id == childID }
                node?.let {
                    val child = TreeNode(
                        node,
                        depth = currentNode.depth + 1,
                        appearance = appearance
                    )
                    currentNode.addChild(child)
                    constructLayerNodeTrees(child, nodes, appearance)
                }
            }
        }
    }

    private fun layoutFromCache(
        screenID: String,
        nodesForScreenInfo: NodesForScreenInfo,
        appearance: Appearance,
        resolvers: Resolvers
    ) {
        binding.screenFrame.doOnLayout {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                render(screenID, appearance, resolvers, nodesForScreenInfo, true)
            }
        }
    }

    private fun listenForUpdates(screenID: String, appearance: Appearance, resolvers: Resolvers) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            // mutex to prevent multiple fires of the logic in launch
            val mutex = Mutex()
            model.nodesFlowForScreen(screenID)
                .onEach { binding.swipeToRefresh.isRefreshing = false }
                .distinctUntilChanged()
                .collect { nodesForScreenInfo ->
                    binding.screenFrame.doOnLayout {
                        launch {
                            mutex.withLock {
                                setSwipeToRefresh(nodesForScreenInfo.swipeToRefresh) { model.refreshNodes(screenID) }
                                render(screenID, appearance, resolvers, nodesForScreenInfo, false)
                            }
                        }
                    }
                }
        }
    }

    private suspend fun render(
        screenID: String,
        appearance: Appearance,
        resolvers: Resolvers,
        nodesForScreenInfo: NodesForScreenInfo,
        fromCache: Boolean
    ) {
        val (incomingFrame, outgoingFrame) = if (binding.screenFrame.tag == null) {
            binding.screenFrame.tag = "active"
            binding.screenFrame to binding.screenFrame2
        } else {
            binding.screenFrame.tag = null
            binding.screenFrame2 to binding.screenFrame
        }

        val nodes = nodesForScreenInfo.nodes
        val swipeToRefresh = nodesForScreenInfo.swipeToRefresh
        val collectionChildIDs = nodesForScreenInfo.collectionNodeIDs
        val canCache = nodesForScreenInfo.canCache

        val screen = nodes.find { it.id == screenID } ?: return
        val screenNode = TreeNode(screen)

        val mediaNodeIDs = nodes.filter { it is PlaysMedia }.map { it.id }

        withContext(Dispatchers.Default) {

            constructLayerNodeTrees(screenNode, nodes, appearance)

            val appBar = screenNode.children.find { it.value is AppBar }
            withContext(Dispatchers.Main) {
                appBar?.let {
                    setupAppBar(
                        it.value as AppBar,
                        it.children.map { child -> child.value as MenuItem },
                        resolvers
                    )
                    screenNode.removeChild(it.value.id)
                }
            }

            if (!fromCache) computeSizeAndPositioning(screenNode, appBar != null)

            // remove nodes that can be lazily loaded and group by their nearest scroll ancestor
            val scrollIDsToRemovedNodes = nodesToBeLazilyLoaded(nodes, collectionChildIDs, screenNode)
            val verticalScrollIDsToRemovedNodes =
                scrollIDsToRemovedNodes.filter { removedGroup ->
                    (nodes.find { it.id == removedGroup.first } as? ScrollContainer)?.axis == Axis.VERTICAL
                }
            val horizontalScrollIDsToRemovedNodes =
                scrollIDsToRemovedNodes.filter { removedGroup ->
                    (nodes.find { it.id == removedGroup.first } as? ScrollContainer)?.axis == Axis.HORIZONTAL
                }

            // construct views
            val views = screenNode.children.reversed().flatMap { it.toLayout(requireContext(), resolvers) }
            val toCache = if (canCache) NodesForScreenInfo(
                nodes,
                swipeToRefresh,
                collectionChildIDs,
                false
            ) else null

            // capture old vertical scroll state
            val verticalScrollIDsToPosition = captureVerticalScrollState(nodes, outgoingFrame)
            val horizontalScrollIDsToPosition = captureHorizontalScrollState(nodes, outgoingFrame)

            // add views when frame laid out, restore scroll state and setup scroll views for lazy loading
            withContext(Dispatchers.Main) {
                if (!fromCache) model.readyToUpdate(screenID, toCache)
                views.forEach { incomingFrame.addView(it) }
                incomingFrame.doOnLayout {
                    incomingFrame.visibility = View.VISIBLE
                    outgoingFrame.visibility = View.INVISIBLE
                    outgoingFrame.removeAllViews()

                    setupVerticalLazyLoading(verticalScrollIDsToRemovedNodes, mediaNodeIDs, incomingFrame, resolvers)
                    setupHorizontalLazyLoading(horizontalScrollIDsToRemovedNodes, mediaNodeIDs, incomingFrame, resolvers)

                    restoreVerticalScrollState(verticalScrollIDsToPosition, incomingFrame)
                    restoreHorizontalScrollState(horizontalScrollIDsToPosition, incomingFrame)

                    if (fromCache) {
                        setSwipeToRefresh(nodesForScreenInfo.swipeToRefresh) {
                            model.refreshNodes(screenID)
                            listenForUpdates(screenID, appearance, resolvers)
                        }
                    }

                    currentLifecycleObserver = MediaAwareLifecycleObserver(mediaNodeIDs, incomingFrame)
                }
            }
        }
    }

    private fun computeSizeAndPositioning(screenNode: TreeNode, accountForAppBar: Boolean) {
        screenNode.clearSizeAndPositioning()

        val appBarHeight = if (accountForAppBar) 56.dp.toPx(requireContext()) else 0f
        screenNode.computeSize(
            requireContext(),
            Dimensions(
                Dimension.Value(binding.rootFrame.width.toFloat()),
                Dimension.Value(binding.rootFrame.height.toFloat() - appBarHeight)
            )
        )
        (screenNode.value as Screen).computePosition(requireContext(), screenNode)
    }

    private fun nodesToBeLazilyLoaded(
        nodes: List<Node>,
        collectionChildIDs: List<String>,
        screenNode: TreeNode
    ): List<Pair<String, MutableList<TreeNode>>> {
        val nodesToRemove = nodes.filter {
            it.id in collectionChildIDs && !(it as Layer).sizeAndCoordinates.intersects(
                0f, 0f, binding.screenFrame.width.toFloat() * 1.5f,
                binding.screenFrame.height.toFloat() * 1.2f
            )
        }
        val removed = nodesToRemove.mapNotNull {
            val nodeToRemove = screenNode.findNodeWithID(it.id)
            val nearestScrollID = nodeToRemove?.findNearestAncestorID<ScrollContainer>()
            screenNode.findNodeWithID(it.id)?.parent?.removeChild(it.id)

            if (nearestScrollID != null) nearestScrollID to nodeToRemove else null
        }

        val removedGroups = removed.groupBy { it.first }
            .map { it.key to it.value.map { value -> value.second }.toMutableList() }
        return removedGroups
    }

    private fun setupHorizontalLazyLoading(
        horizontalRemovedGroups: List<Pair<String, MutableList<TreeNode>>>,
        mediaChildIDs: List<String>,
        incomingFrame: FrameLayout,
        resolvers: Resolvers
    ) {
        val groupsToCoordinates = horizontalRemovedGroups.map {
            it.first to it.second.map { removedNode ->
                removedNode to ((removedNode.value as? Layer)?.sizeAndCoordinates?.x ?: 0f)
            }.toMutableList()
        }
        groupsToCoordinates.forEach { (scrollID, groupToCoordinates) ->
            val scrollView = incomingFrame.findViewWithTag<HorizontalExperienceScrollView>(
                UUID.fromString(scrollID)
            )

            if (scrollView.canScrollHorizontally(1)) {
                scrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
                    lifecycleScope.launchWhenResumed {
                        val toAdd: List<Pair<TreeNode, Float>> =
                            groupToCoordinates.filter { it.second < scrollX + scrollView.width.toFloat() }
                        groupToCoordinates.removeAll(toAdd)
                        val viewsToAdd =
                            toAdd.flatMap { it.first.toLayout(requireContext(), resolvers) }
                        val scrollFrame = ((scrollView as? ViewGroup)?.getChildAt(0) as? ViewGroup)

                        viewsToAdd.forEach { scrollFrame?.addView(it) }
                    }
                }
            } else {
                scrollView.doOnPreDraw {
                    lifecycleScope.launchWhenResumed {
                        val scrollFrame = ((scrollView as? ViewGroup)?.getChildAt(0) as? ViewGroup)
                        val views = groupToCoordinates.flatMap { it.first.toLayout(requireContext(), resolvers) }
                        views.forEach { scrollFrame?.addView(it) }
                    }
                }
            }

            mediaChildIDs.forEach {
                scrollView.findViewWithTag<ExperienceMediaPlayerView>(it)?.setupIfVisible()
            }
        }
    }

    private fun setupVerticalLazyLoading(
        verticalRemovedGroups: List<Pair<String, MutableList<TreeNode>>>,
        mediaChildIDs: List<String>,
        incomingFrame: FrameLayout,
        resolvers: Resolvers
    ) {
        val groupsToCoordinates = verticalRemovedGroups.map {
            it.first to it.second.map { removedNode ->
                removedNode to ((removedNode.value as? Layer)?.sizeAndCoordinates?.y ?: 0f)
            }.toMutableList()
        }
        groupsToCoordinates.forEach { (scrollID, groupToCoordinates) ->
            val scrollView =
                incomingFrame.findViewWithTag<ExperienceScrollView>(UUID.fromString(scrollID))

            if (scrollView.canScrollVertically(1)) {
                scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    lifecycleScope.launchWhenResumed {
                        val toAdd: List<Pair<TreeNode, Float>> =
                            groupToCoordinates.filter { it.second < scrollY + scrollView.height.toFloat() }
                        groupToCoordinates.removeAll(toAdd)
                        val viewsToAdd =
                            toAdd.flatMap { it.first.toLayout(requireContext(), resolvers) }
                        val scrollFrame = ((scrollView as? ViewGroup)?.getChildAt(0) as? ViewGroup)

                        viewsToAdd.forEach { scrollFrame?.addView(it) }
                    }
                }
            } else {
                scrollView.doOnPreDraw {
                    lifecycleScope.launchWhenResumed {
                        val scrollFrame = ((scrollView as? ViewGroup)?.getChildAt(0) as? ViewGroup)
                        val views = groupToCoordinates.flatMap {
                            it.first.toLayout(requireContext(), resolvers)
                        }
                        views.forEach { scrollFrame?.addView(it) }
                    }
                }
            }
            mediaChildIDs.forEach {
                scrollView.findViewWithTag<ExperienceMediaPlayerView>(it)?.setupIfVisible()
            }
        }
    }

    private fun restoreHorizontalScrollState(
        horizontalScrollIDsToPosition: List<Pair<String, Int>>,
        incomingFrame: FrameLayout
    ) {
        horizontalScrollIDsToPosition.forEach {
            val scrollView = incomingFrame.findViewWithTag<HorizontalExperienceScrollView>(UUID.fromString(it.first))
            scrollView?.scrollX = it.second
        }
    }

    private fun restoreVerticalScrollState(
        verticalScrollIDsToPosition: List<Pair<String, Int>>,
        incomingFrame: FrameLayout
    ) {
        verticalScrollIDsToPosition.forEach {
            val scrollView = incomingFrame.findViewWithTag<ExperienceScrollView>(UUID.fromString(it.first))
            scrollView?.scrollY = it.second
        }
    }

    private fun captureHorizontalScrollState(
        nodes: List<Node>,
        outgoingFrame: FrameLayout
    ): List<Pair<String, Int>> {
        val horizontalScrollIDs =
            nodes.filter { it is ScrollContainer && it.axis == Axis.HORIZONTAL }.map { it.id }

        return horizontalScrollIDs.map {
            val scrollView = outgoingFrame.findViewWithTag<HorizontalExperienceScrollView>(
                UUID.fromString(
                    it
                )
            )
            val x = scrollView?.scrollX ?: 0
            it to x
        }
    }

    private fun captureVerticalScrollState(
        nodes: List<Node>,
        outgoingFrame: FrameLayout
    ): List<Pair<String, Int>> {
        val verticalScrollIDs =
            nodes.filter { it is ScrollContainer && it.axis == Axis.VERTICAL }.map { it.id }

        return verticalScrollIDs.map {
            val scrollView =
                outgoingFrame.findViewWithTag<ExperienceScrollView>(UUID.fromString(it))
            val y = scrollView?.scrollY ?: 0
            it to y
        }
    }

    private fun setSwipeToRefresh(swipeToRefresh: Boolean, onRefresh: () -> Unit) {
        binding.swipeToRefresh.isRefreshing = false
        if (swipeToRefresh) {
            binding.swipeToRefresh.isEnabled = true
            binding.swipeToRefresh.setOnRefreshListener {
                onRefresh()
            }
        } else {
            binding.swipeToRefresh.setOnRefreshListener(null)
            binding.swipeToRefresh.isEnabled = false
        }
    }
}
