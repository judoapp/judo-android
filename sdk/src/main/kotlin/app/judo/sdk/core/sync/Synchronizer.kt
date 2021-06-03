package app.judo.sdk.core.sync

interface Synchronizer {

    suspend fun performSync(
        prefetchAssets: Boolean = false,
        onComplete: () -> Unit = {},
    )

}
