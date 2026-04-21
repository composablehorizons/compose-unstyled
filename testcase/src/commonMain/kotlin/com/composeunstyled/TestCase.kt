package com.composeunstyled

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KClass
import kotlin.time.Duration

data class TestResult(
    val name: String,
    val passed: Boolean,
    val error: Throwable? = null,
    val ignored: Boolean = false
)


fun testCase(
    name: String,
    expected: KClass<out Throwable>? = null,
    ignored: Boolean = false,
    assertions: suspend ComposeUiTest.() -> Unit
): TestResult {
    // This function is only used for standalone test cases outside of runTestSuite
    // For tests within runTestSuite, the testCase function in TestSuiteScope should be used
    if (ignored) {
        println("👋 Ignoring '$name'")
        return TestResult(name, passed = true, ignored = true)
    }
    val result = runCatching { runComposeUiTest { assertions() } }
    return when {
        result.isSuccess && expected == null -> {
            println("✅ '$name' passed")
            TestResult(name, passed = true)
        }

        result.isSuccess && expected != null -> {
            val error = AssertionError("❌ '$name' failed.\nExpected ${expected.simpleName} but none thrown.")
            println("❌ '$name' failed.\nExpected ${expected.simpleName} but none thrown.")
            TestResult(name, passed = false, error = error)
        }

        result.isFailure && expected == null -> {
            val error = result.exceptionOrNull() ?: AssertionError("Unknown error")
            println("❌ '$name' failed.\nReason: $error")
            TestResult(name, passed = false, error = error)
        }

        result.isFailure && expected != null -> {
            val ex = result.exceptionOrNull()!!
            if (expected.isInstance(ex)) {
                println("✅ '$name' passed")
                TestResult(name, passed = true)
            } else {
                val error =
                    AssertionError("❌ '$name' failed.\nExpected ${expected.simpleName}, got ${ex::class.simpleName}")
                println("❌ '$name' failed.\nExpected ${expected.simpleName}, got ${ex::class.simpleName}")
                TestResult(name, passed = false, error = error)
            }
        }

        else -> {
            val error = AssertionError("❌ '$name' failed.\nUnexpected test state.")
            println("❌ '$name' failed.\nUnexpected test state.")
            TestResult(name, passed = false, error = error)
        }
    }
}

fun runTestSuite(block: TestSuiteScope.() -> Unit) {
    val scope = TestSuiteScope()
    scope.block()
    runTest {
        val results = scope.testCases.map { testCase ->
            async {
                if (testCase.ignored) {
                    println("👋 Ignoring '${testCase.name}'")
                    return@async TestResult(testCase.name, passed = true, ignored = true)
                }
                val result = runCatching { runComposeUiTest { testCase.assertions(this) } }
                when {
                    result.isSuccess && testCase.expected == null -> {
                        TestResult(testCase.name, passed = true)
                    }

                    result.isSuccess && testCase.expected != null -> {
                        val error = AssertionError("Expected ${testCase.expected.simpleName} but none thrown.")
                        TestResult(testCase.name, passed = false, error = error)
                    }

                    result.isFailure && testCase.expected == null -> {
                        val error = result.exceptionOrNull() ?: AssertionError("Unknown error")
                        TestResult(testCase.name, passed = false, error = error)
                    }

                    result.isFailure && testCase.expected != null -> {
                        val ex = result.exceptionOrNull()!!
                        if (testCase.expected.isInstance(ex)) {
                            TestResult(testCase.name, passed = true)
                        } else {
                            val error =
                                AssertionError("Expected ${testCase.expected.simpleName}, got ${ex::class.simpleName}")
                            TestResult(testCase.name, passed = false, error = error)
                        }
                    }

                    else -> {
                        val error = AssertionError("Unexpected test state.")
                        TestResult(testCase.name, passed = false, error = error)
                    }
                }
            }
        }.awaitAll()

        // Report results and throw if any tests failed
        val failedTests = results.filter { !it.passed && !it.ignored }
        val passedTests = results.filter { it.passed && !it.ignored }
        val ignoredTests = results.filter { it.ignored }

        println("\n📊 Test Summary:")
        println("✅ Passed: ${passedTests.size}")
        println("❌ Failed: ${failedTests.size}")
        println("👋 Ignored: ${ignoredTests.size}")

        if (failedTests.isNotEmpty()) {
            val errorMessage = buildString {
                appendLine("Test suite failed with ${failedTests.size} failure(s):")
                failedTests.forEach { result ->
                    appendLine("  ❌ ${result.name}: ${result.error?.message}")
                    appendLine("Stack Trace: ${result.error?.stackTraceToString()}")
                }
                appendLine("---")
            }
            throw AssertionError(errorMessage)
        }
    }
}

class TestSuiteScope {
    internal val testCases = mutableListOf<TestCase>()

    fun testCase(
        name: String,
        expected: KClass<out Throwable>? = null,
        ignored: Boolean = false,
        assertions: suspend ComposeUiTest.() -> Unit
    ) {
        // Just register the test case for later parallel execution
        testCases.add(TestCase(name, expected, ignored, assertions))
    }
}

internal data class TestCase(
    val name: String,
    val expected: KClass<out Throwable>? = null,
    val ignored: Boolean = false,
    val assertions: suspend ComposeUiTest.() -> Unit
)

context(uiTest: ComposeUiTest)
fun Dp.toPx(): Float {
    val dpValue = this
    return with(uiTest.density) {
        dpValue.toPx()
    }
}

fun ComposeUiTest.advanceTimeBy(duration: Duration) {
    mainClock.advanceTimeBy(duration.inWholeMilliseconds)
}
