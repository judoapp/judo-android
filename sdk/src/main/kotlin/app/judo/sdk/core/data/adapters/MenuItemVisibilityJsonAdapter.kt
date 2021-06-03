package app.judo.sdk.core.data.adapters

import app.judo.sdk.api.models.MenuItemVisibility
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class MenuItemVisibilityJsonAdapter {

    @FromJson
    fun fromJson(visibility: String) =
        MenuItemVisibility.values().find { it.code == visibility } ?: throw RuntimeException("Incorrect code")

    @ToJson
    fun toJson(visibility: MenuItemVisibility) = visibility.code
}