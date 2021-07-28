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

package app.judo.sdk.ui.extensions

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.view.View
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.*
import androidx.lifecycle.*
import app.judo.sdk.api.models.Appearance
import app.judo.sdk.api.models.StatusBarStyle
import app.judo.sdk.ui.extensions.createViewModelLazy
import java.io.Serializable
import kotlin.reflect.KClass

internal fun Window.setStatusBarIconTint(statusBarStyle: StatusBarStyle, appearance: Appearance) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        decorView.windowInsetsController?.systemBarsAppearance
        val barAppearance = if (statusBarStyle.isDarkIconTint(context, appearance)) APPEARANCE_LIGHT_STATUS_BARS else 0
        decorView.windowInsetsController?.setSystemBarsAppearance(barAppearance, APPEARANCE_LIGHT_STATUS_BARS)
    } else {
        decorView.systemUiVisibility =
            if (statusBarStyle.isDarkIconTint(context, appearance)) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
    }
}

internal fun StatusBarStyle.isDarkIconTint(context: Context, appearance: Appearance): Boolean {
    return when (this) {
        StatusBarStyle.DEFAULT -> context.isLightMode(appearance)
        StatusBarStyle.LIGHT -> false
        StatusBarStyle.DARK -> true
        StatusBarStyle.INVERTED -> context.isDarkMode(appearance)
    }
}

internal fun Context.isDarkMode(appearance: Appearance): Boolean {
    return when (appearance) {
        Appearance.DARK -> true
        Appearance.LIGHT -> false
        Appearance.AUTO -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}

internal fun Context.isLightMode(appearance: Appearance) = !isDarkMode(appearance)

internal fun Context.getMaterialIconID(iconName: String): Int {
    return if (iconName.endsWith(".fill")) {
        resources.getIdentifier("judo_sdk_baseline_${iconName.substringBeforeLast(".fill")}", "drawable", this.packageName)
    } else {
        resources.getIdentifier("judo_sdk_${iconName}", "drawable", this.packageName)
    }
}

internal fun Uri.toCustomTabsIntent(@ColorInt toolbarColor: Int = Color.BLACK): Intent {
    val customTabsIntentHolder = CustomTabsIntent.Builder()
        .setToolbarColor(toolbarColor)
        .build()
    customTabsIntentHolder.intent.data = this
    return customTabsIntentHolder.intent
}

internal fun bundleOf(vararg pairs: Pair<String, Any?>) = Bundle(pairs.size).apply {
    for ((key, value) in pairs) {
        when (value) {
            null -> putString(key, null) // Any nullable type will suffice.

            // Scalars
            is Boolean -> putBoolean(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Short -> putShort(key, value)

            // References
            is Bundle -> putBundle(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Parcelable -> putParcelable(key, value)

            // Scalar arrays
            is BooleanArray -> putBooleanArray(key, value)
            is ByteArray -> putByteArray(key, value)
            is CharArray -> putCharArray(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is FloatArray -> putFloatArray(key, value)
            is IntArray -> putIntArray(key, value)
            is LongArray -> putLongArray(key, value)
            is ShortArray -> putShortArray(key, value)

            // Reference arrays
            is Array<*> -> {
                val componentType = value::class.java.componentType!!
                @Suppress("UNCHECKED_CAST") // Checked by reflection.
                when {
                    Parcelable::class.java.isAssignableFrom(componentType) -> {
                        putParcelableArray(key, value as Array<Parcelable>)
                    }
                    String::class.java.isAssignableFrom(componentType) -> {
                        putStringArray(key, value as Array<String>)
                    }
                    CharSequence::class.java.isAssignableFrom(componentType) -> {
                        putCharSequenceArray(key, value as Array<CharSequence>)
                    }
                    Serializable::class.java.isAssignableFrom(componentType) -> {
                        putSerializable(key, value)
                    }
                    else -> {
                        val valueType = componentType.canonicalName
                        throw IllegalArgumentException(
                            "Illegal value array type $valueType for key \"$key\"")
                    }
                }
            }

            // Last resort. Also we must check this after Array<*> as all arrays are serializable.
            is Serializable -> putSerializable(key, value)

            else -> {
                if (Build.VERSION.SDK_INT >= 18 && value is IBinder) {
                    putBinder(key, value)
                } else if (Build.VERSION.SDK_INT >= 21 && value is Size) {
                    putSize(key, value)
                } else if (Build.VERSION.SDK_INT >= 21 && value is SizeF) {
                    putSizeF(key, value)
                } else {
                    val valueType = value.javaClass.canonicalName
                    throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
                }
            }
        }
    }
}

@MainThread
internal inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> ViewModelProvider.Factory)
): Lazy<VM> = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
internal fun <VM : ViewModel> Fragment.createViewModelLazy(
    viewModelClass: KClass<VM>,
    storeProducer: () -> ViewModelStore,
    factoryProducer: (() -> ViewModelProvider.Factory)
): Lazy<VM> {
    val factoryPromise = factoryProducer
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}

internal inline fun FragmentManager.commitNow(
    allowStateLoss: Boolean = false,
    body: FragmentTransaction.() -> Unit
) {
    val transaction = beginTransaction()
    transaction.body()
    if (allowStateLoss) {
        transaction.commitNowAllowingStateLoss()
    } else {
        transaction.commitNow()
    }
}

inline fun Canvas.withSave(block: Canvas.() -> Unit) {
    val checkpoint = save()
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

internal operator fun Rect.component1() = this.left
internal operator fun Rect.component2() = this.top
internal operator fun Rect.component3() = this.right
internal operator fun Rect.component4() = this.bottom

internal fun Drawable.toBitmap(
    @Px width: Int = intrinsicWidth,
    @Px height: Int = intrinsicHeight,
    config: Bitmap.Config? = null
): Bitmap {
    if (this is BitmapDrawable) {
        if (config == null || bitmap.config == config) {
            // Fast-path to return original. Bitmap.createScaledBitmap will do this check, but it
            // involves allocation and two jumps into native code so we perform the check ourselves.
            if (width == intrinsicWidth && height == intrinsicHeight) {
                return bitmap
            }
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }

    val (oldLeft, oldTop, oldRight, oldBottom) = bounds

    val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
    setBounds(0, 0, width, height)
    draw(Canvas(bitmap))

    setBounds(oldLeft, oldTop, oldRight, oldBottom)
    return bitmap
}


@RequiresApi(26)
@ColorLong
internal infix fun @receiver:ColorLong Long.convertTo(colorSpace: ColorSpace.Named) = Color.convert(this, ColorSpace.get(colorSpace))

internal fun String.toUri(): Uri = Uri.parse(this)
