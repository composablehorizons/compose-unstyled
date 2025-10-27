package com.composeunstyled

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import org.junit.Assume.assumeTrue


@OptIn(ExperimentalTestApi::class)
internal fun testCase(
    name: String,
    expected: KClass<out Throwable>? = null,
    ignored: Boolean = false,
    assertions: suspend ComposeUiTest.() -> Unit
) {
    if (ignored) {
        println("👋 Ignoring '$name'")
        assumeTrue(false)
        return
    }
    val result = runCatching { runComposeUiTest { assertions() } }
    when {
        result.isSuccess && expected == null -> println("✅ '$name' passed")
        result.isSuccess && expected != null -> error("❌ '$name' failed.\nExpected ${expected.simpleName} but none thrown.")
        result.isFailure && expected == null -> error("❌ '$name' failed.\nReason: ${result.exceptionOrNull()}")
        result.isFailure && expected != null -> {
            val ex = result.exceptionOrNull()!!
            if (expected.isInstance(ex)) println("✅ '$name' passed")
            else error("❌ '$name' failed.\nExpected ${expected.simpleName}, got ${ex::class.simpleName}")
        }
    }
}

@OptIn(ExperimentalTestApi::class)
internal fun runTestSuite(block: TestSuiteScope.() -> Unit) {
    val scope = TestSuiteScope()
    scope.block()
    runBlocking {
        scope.testCases.map { testCase ->
            async {
                if (testCase.ignored) {
                    println("👋 Ignoring '${testCase.name}'")
                    assumeTrue(false)
                    return@async
                }
                val result = runCatching { runComposeUiTest { testCase.assertions(this) } }
                when {
                    result.isSuccess && testCase.expected == null -> println("✅ '${testCase.name}' passed")
                    result.isSuccess && testCase.expected != null -> error("❌ '${testCase.name}' failed.\nExpected ${testCase.expected.simpleName} but none thrown.")
                    result.isFailure && testCase.expected == null -> error("❌ '${testCase.name}' failed.\nReason: ${result.exceptionOrNull()}")
                    result.isFailure && testCase.expected != null -> {
                        val ex = result.exceptionOrNull()!!
                        if (testCase.expected.isInstance(ex)) println("✅ '${testCase.name}' passed")
                        else error("❌ '${testCase.name}' failed.\nExpected ${testCase.expected.simpleName}, got ${ex::class.simpleName}")
                    }
                }
            }
        }.awaitAll()
    }
}

@OptIn(ExperimentalTestApi::class)
class TestSuiteScope {
    internal val testCases = mutableListOf<TestCase>()

    fun testCase(
        name: String,
        expected: KClass<out Throwable>? = null,
        ignored: Boolean = false,
        assertions: suspend ComposeUiTest.() -> Unit
    ) {
        testCases.add(TestCase(name, expected, ignored, assertions))
    }
}

@OptIn(ExperimentalTestApi::class)
internal data class TestCase(
    val name: String,
    val expected: KClass<out Throwable>? = null,
    val ignored: Boolean = false,
    val assertions: suspend ComposeUiTest.() -> Unit
)
