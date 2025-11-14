package com.composeunstyled.platformtheme

internal actual inline fun <T> platformValue(
    android: () -> T,
    iOS: () -> T,
    jvm: (OperatingSystem) -> T,
    web: () -> T
): T {
    val os = operatingSystem()
    return jvm(os)
}

internal fun operatingSystem(): OperatingSystem {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("mac") -> OperatingSystem.Mac
        os.contains("win") -> OperatingSystem.Windows
        else -> OperatingSystem.Unknown
    }
}
