package app.judo.sdk.api.data

fun interface UserDataSupplier {

    fun supplyUserData(): Map<String, String>

}