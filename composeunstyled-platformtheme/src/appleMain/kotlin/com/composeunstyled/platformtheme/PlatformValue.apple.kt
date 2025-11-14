package com.composeunstyled.platformtheme

internal actual inline fun <T> platformValue(
    android: () -> T,
    iOS: () -> T,
    jvm: (OperatingSystem) -> T,
    web: () -> T
): T {
    return iOS()
}
