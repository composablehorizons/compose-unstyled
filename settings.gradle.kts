rootProject.name = "unstyled"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    google()
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
  repositories {
    google()
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
  }
}

include(":testcase")
include(":composeunstyled-build-modifier")
include(":composeunstyled-modal")
include(":composeunstyled-scrim")
include(":composeunstyled-dialog")
include(":composeunstyled-bottom-sheet")
include(":composeunstyled-escape-handler")
include(":composeunstyled-modal-bottom-sheet")
include(":composeunstyled-anchored")
include(":composeunstyled-dropdown-menu")
include(":composeunstyled-tooltip")
include(":composeunstyled-window-container-size")
include(":composeunstyled-button")
include(":composeunstyled-checkbox")
include(":composeunstyled-tri-state-checkbox")
include(":composeunstyled-disclosure")
include(":composeunstyled-progress")
include(":composeunstyled-toggle-switch")
include(":composeunstyled-tab-group")
include(":composeunstyled-icon")
include(":composeunstyled-text-field")
include(":composeunstyled-radio-group")
include(":composeunstyled-outline")
include(":composeunstyled-slider")
include(":composeunstyled-separators")
include(":composeunstyled-stack")
include(":composeunstyled-focus-ring")
include(":composeunstyled-primitives")
include(":composeunstyled-scroll-area")
include(":composeunstyled-scrollbars")
include(":composeunstyled-overlay")
include(":composeunstyled-theming")
include(":composeunstyled-colored-indication")
include(":composeunstyled-platformtheme")
include(":demo")
include(":demo-material-impl")
include(":demo-xml-theme")
include(":demo-system-ui-styling")
include(":playground-android")
