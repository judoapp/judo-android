package app.judo.sdk.core.data

import app.judo.sdk.api.models.Conditional
import app.judo.sdk.core.extensions.*
import app.judo.sdk.core.implementations.InterpolatorImpl
import app.judo.sdk.core.lang.Keyword
import app.judo.sdk.core.lang.ProtoInterpolator
import app.judo.sdk.core.lang.TokenizerImpl
import app.judo.sdk.utils.TestJSON
import org.junit.Assert
import org.junit.Test

class TheTree {

    @Test
    fun `Can be pruned`() {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.conditional_test_experience)!!

        val experienceTree = ExperienceTree(experience)

        val dataContext = dataContextOf(
            Keyword.USER.value to mapOf("isPremium" to false)
        )

        val tree = experienceTree.screenNodes.values.first().trunk

        // Act
        val prunedTree = tree.prune { subtree ->

            val node = subtree.value

            if (node is Conditional) {

                val resolution = node.resolve(dataContext, InterpolatorImpl(
                    tokenizer = TokenizerImpl(),
                    dataContext = dataContext
                ))
                println("Pruning ${node.name}: $resolution")

                return@prune resolution

            }

            true
        }

        prunedTree?.traverse { node ->
            println(node.value)
        }

        val actual = prunedTree?.flatten() ?: emptyList()

//        println(actual)

        // Assert
        Assert.assertTrue(actual.size == 3)
    }

    @Test
    fun `Can be copied`() {
        // Arrange
        val experience = JsonParser.parseExperience(TestJSON.conditional_test_experience)!!

        val experienceTree = ExperienceTree(experience)

        val tree = experienceTree.screenNodes.values.first().trunk

        val expected = tree.flatten()

        // Act
        val actual = tree.copy().flatten()

        // Assert
        Assert.assertEquals(expected, actual)
    }

}