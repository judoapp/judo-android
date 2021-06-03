package app.judo.sdk.ui.layout

import app.judo.sdk.api.models.Action
import app.judo.sdk.core.data.resolvers.ColorResolver
import app.judo.sdk.core.data.resolvers.GradientResolver
import app.judo.sdk.core.data.resolvers.StatusBarColorResolver

internal typealias ActionResolver = (Action) -> Unit

internal data class Resolvers(
    val colorResolver: ColorResolver,
    val gradientResolver: GradientResolver,
    val actionResolver: ActionResolver,
    val statusBarColorResolver: StatusBarColorResolver
)
