package app.judo.sdk.ui.layout

import app.judo.sdk.api.models.*
import app.judo.sdk.api.models.Collection
import app.judo.sdk.core.environment.Environment
import java.util.*

internal class ExperienceNodeTransformer {
    fun transformExperienceNodesForLayout(experience: Experience): Experience {
        return addImplicitStacksForScrollContainers(experience)
    }

    private fun addImplicitStacksForScrollContainers(experience: Experience): Experience {
        val modifiedNodes = experience.nodes.toMutableList()
        val scrollContainers = experience.nodes.filterIsInstance<ScrollContainer>()
        scrollContainers.map { scrollContainer ->
            if (scrollContainer.childIDs.count() > 1) {
                val stackNode = if (scrollContainer.axis == Axis.VERTICAL) {
                    VStack(
                        id = UUID.randomUUID().toString(),
                        name = null,
                        spacing = 0f,
                        alignment = HorizontalAlignment.CENTER,
                        childIDs = scrollContainer.childIDs
                    )
                } else {
                    HStack(
                        id = UUID.randomUUID().toString(),
                        name = null,
                        spacing = 0f,
                        alignment = VerticalAlignment.CENTER,
                        childIDs = scrollContainer.childIDs
                    )
                }
                val newScrollContainer = scrollContainer.copy(childIDs = listOf(stackNode.id))
                modifiedNodes.remove(scrollContainer)
                modifiedNodes.add(newScrollContainer)
                modifiedNodes.add(stackNode)
            }
        }

        return experience.copy(nodes = modifiedNodes)
    }

    private fun removeDataSources(nodes: List<Node>): List<Node> {
        val modifiedNodes = nodes.toMutableList()
        val dataSources = nodes.filterIsInstance<DataSource>()
        val dataSourceIDs = dataSources.map { it.id }

        nodes.forEach {
            if (it is NodeContainer) {
                val childIDs = it.getChildNodeIDs()
                val theDataSourcesChildIDs = getDataSourceChildIDs(dataSources, childIDs)

                when (it) {
                    is HStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is VStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is ScrollContainer -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is ZStack -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is Screen -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                    is Carousel -> {
                        modifiedNodes.remove(it)
                        modifiedNodes.add(it.copy(childIDs = theDataSourcesChildIDs - dataSourceIDs))
                    }
                }
            }
        }
        modifiedNodes.removeAll(dataSources)

        return modifiedNodes
    }

    fun transformScreenNodesForLayout(screenID: String, nodes: List<Node>): List<Node> {
        val nodesForScreen = filterNodesForScreen(screenID, nodes)
        val dataSourcesRemovedNodes = removeDataSources(nodesForScreen)
        val collectionsRemovedNodes = removeCollections(dataSourcesRemovedNodes)
        val interpolationRequiredRemovedNodes = removeAnyNodesRequiringInterpolation(collectionsRemovedNodes)
        return removeImagesWithoutSizes(interpolationRequiredRemovedNodes)
    }

    private fun getDataSourceChildIDs(dataSources: List<DataSource>, ids: List<String>): MutableList<String> {

        val datasourceIds = dataSources.map { it.id }

        val nestedIDs = mutableListOf<String>()
        dataSources.forEach {
            if (it.id in ids) nestedIDs.addAll(getDataSourceChildIDs(dataSources, it.childIDs))
        }
        return (ids.filter { it !in datasourceIds } + nestedIDs).toMutableList()
    }

    private fun removeImagesWithoutSizes(nodes: List<Node>): List<Node> {
        val imagesWithoutSizes = getImagesWithoutSizes(nodes)
        return nodes.filter { it !in imagesWithoutSizes }
    }

    private fun removeCollections(nodes: List<Node>): List<Node> {
        return nodes.filter { it !is Collection }
    }

    private fun getImagesWithoutSizes(nodes: List<Node>): List<Image> {
        val imageBackgroundNodes = nodes.filter { (it is Backgroundable) && (it.background?.node is Image) }.map {
            ((it as Backgroundable).background?.node as Image)
        }
        val imageOverlayNodes = nodes.filter { (it is Overlayable) && (it.overlay?.node is Image) }.map {
            ((it as Overlayable).overlay?.node as Image)
        }

        return (nodes + imageBackgroundNodes + imageOverlayNodes).filterIsInstance<Image>().filter {
            !it.hasImageDimensions() && !it.interpolatedImageURL.contains(Regex(Environment.RegexPatterns.HANDLE_BAR_EXPRESSION_PATTERN))
        }
    }

    private fun String.requiresInterpolation() = this.contains(Regex(Environment.RegexPatterns.HANDLE_BAR_EXPRESSION_PATTERN))

    private fun removeAnyNodesRequiringInterpolation(nodes: List<Node>): List<Node> {
        val modifiedNodes = nodes.toMutableList()

        return modifiedNodes.filterNot { nodeRequiresInterpolation(it)
                    || (it is Overlayable && nodeRequiresInterpolation(it.overlay?.node))
                    || (it is Backgroundable && nodeRequiresInterpolation(it.background?.node))
        }
    }

    private fun nodeRequiresInterpolation(node: Node?): Boolean {
        return when (node) {
            is Image -> node.interpolatedImageURL.requiresInterpolation()
            is WebView -> node.interpolatedURL.requiresInterpolation()
            is Text -> node.interpolatedText.requiresInterpolation()
            is Audio -> node.interpolatedSourceURL.requiresInterpolation()
            is Video -> node.interpolatedSourceURL.requiresInterpolation()
            else -> false
        }
    }

    private fun getChildNodeIDs(nodeID: String, nodes: List<Node>): List<String> {
        return when(val node = nodes.find { it.id == nodeID }) {
            is NodeContainer -> listOf(node.id) + node.getChildNodeIDs().flatMap { childID -> getChildNodeIDs(childID, nodes) }
            is DataSource -> listOf(node.id) + node.childIDs.flatMap { childID -> getChildNodeIDs(childID, nodes) }
            is Collection -> listOf(node.id) + node.childIDs.flatMap { childID -> getChildNodeIDs(childID, nodes) }
            is Node -> listOf(node.id)
            else -> emptyList()
        }
    }

    private fun filterNodesForScreen(screenID: String, nodes: List<Node>): List<Node> {
        val currentScreenIDs = getChildNodeIDs(screenID, nodes)
        return nodes.filter { it.id in currentScreenIDs }
    }
}