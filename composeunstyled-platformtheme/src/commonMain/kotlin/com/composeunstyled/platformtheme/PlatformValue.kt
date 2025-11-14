package com.composeunstyled.platformtheme

internal expect inline fun <T> platformValue(
    android: () -> T = { TODO("Provide value for Android") },
    iOS: () -> T = { TODO("Provide value for iOS") },
    jvm: (OperatingSystem) -> T = { TODO("Provide value for JVM") },
    web: () -> T = { TODO("Provide value for Web") }
): T

internal enum class OperatingSystem {
    Mac, Windows, Linux, Unknown
}
