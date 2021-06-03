package app.judo.sdk.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NavBarAppearanceConfiguration(
    val statusBarStyle: StatusBarStyle,
    val titleColor: ColorVariants,
    val titleFont: Font,
    val buttonColor: ColorVariants,
    val buttonFont: Font,
    val backgroundColor: ColorVariants,
    val backgroundBlur: Boolean,
    val shadowColor: ColorVariants
)