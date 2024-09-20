plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.multiplatform)
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(17)
    }

    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(project(":core"))
            implementation(project(":demo-scrollarea"))
            implementation(project(":demo-dialog"))
            implementation(project(":demo-icon"))
            implementation(project(":demo-menu"))
            implementation(project(":demo-sheet"))
            implementation(project(":demo-modalsheet"))
            implementation(project(":demo-separators"))
        }
    }
}
