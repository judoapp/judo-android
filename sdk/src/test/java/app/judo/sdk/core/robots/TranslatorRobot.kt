package app.judo.sdk.core.robots

import android.os.LocaleList

internal class TranslatorRobot : AbstractTestRobot() {

    fun printLocales() {


        val list = LocaleList.getDefault()
        println(
            list
        )

        println(list.toLanguageTags())
    }


}
