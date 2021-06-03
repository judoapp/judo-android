package app.judo.sdk.utils

import org.junit.Assert

infix fun <A, B> A.shouldEqual(other: B) {
    Assert.assertEquals(this, other)
}

fun <A> A.assertIsNotNull() {
    Assert.assertNotNull(this)
}