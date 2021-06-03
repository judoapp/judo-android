package app.judo.sdk.api.models

internal interface NodeContainer : Node {
    fun getChildNodeIDs(): List<String>
}