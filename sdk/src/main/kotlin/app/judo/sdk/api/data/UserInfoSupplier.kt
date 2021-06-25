package app.judo.sdk.api.data

fun interface UserInfoSupplier {

    fun supplyUserInfo(): Map<String, String>

}