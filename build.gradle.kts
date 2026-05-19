plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.compose).apply(false)
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.library).apply(false)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.spotless)
}

val composeUnstyledDocsSource = layout.projectDirectory.dir("docs")
val generatedComposeUnstyledDocsPages = layout.buildDirectory.dir("generated/compose-unstyled-docs/pages")
val generatedComposeUnstyledDemoSources = layout.buildDirectory.dir("generated/compose-unstyled-docs/demo-sources")
val composeUnstyledDocsAssets = composeUnstyledDocsSource.dir("assets")

val composeUnstyledDemoSources = mapOf(
  "bottom-sheet" to "BottomSheetDemo.kt",
  "modal-bottom-sheet" to "ModalBottomSheetDemo.kt",
  "button" to "ButtonDemo.kt",
  "checkbox" to "CheckboxDemo.kt",
  "tristatecheckbox" to "TriStateCheckboxDemo.kt",
  "dialog" to "DialogDemo.kt",
  "disclosure" to "DisclosureDemo.kt",
  "dropdown-menu" to "DropdownMenuDemo.kt",
  "icon" to "IconDemo.kt",
  "modal" to "ModalDemo.kt",
  "progressindicator" to "ProgressIndicatorDemo.kt",
  "radiogroup" to "RadioGroupDemo.kt",
  "scrollbars" to "ScrollbarsDemo.kt",
  "separators" to "SeparatorsDemo.kt",
  "slider" to "SliderDemo.kt",
  "tabgroup" to "TabGroupDemo.kt",
  "textfield" to "TextFieldDemo.kt",
  "tooltip" to "TooltipDemo.kt",
  "toggleswitch" to "ToggleSwitchDemo.kt",
  "platform-theme" to "PlatformThemeDemo.kt",
  "window-container-size" to "WindowContainerSizeDemo.kt",
)

val generateComposeUnstyledApiReference by tasks.registering(Exec::class) {
  group = "documentation"
  description = "Expands Compose Unstyled API reference markers from Kotlin source."

  inputs.dir(composeUnstyledDocsSource.dir("pages"))
  inputs.files(fileTree(rootDir) {
    include("composeunstyled-*/src/**/*.kt")
  })
  outputs.dir(generatedComposeUnstyledDocsPages)

  commandLine(
    "node",
    layout.projectDirectory.file("scripts/generate-compose-unstyled-api.mjs").asFile.absolutePath,
    generatedComposeUnstyledDocsPages.get().asFile.absolutePath,
  )
}

val generateComposeUnstyledDemoSources by tasks.registering {
  group = "documentation"
  description = "Prepares Compose Unstyled demo sources for display in documentation."

  val demoSourceDirectory = project(":demo").layout.projectDirectory.dir("src/commonMain/kotlin")

  inputs.files(composeUnstyledDemoSources.values.map { demoSourceDirectory.file(it) })
  outputs.dir(generatedComposeUnstyledDemoSources)

  doLast {
    val outputDirectory = generatedComposeUnstyledDemoSources.get().asFile
    outputDirectory.deleteRecursively()
    outputDirectory.mkdirs()

    composeUnstyledDemoSources.values.distinct().forEach { fileName ->
      val sourceFile = demoSourceDirectory.file(fileName).asFile
      check(sourceFile.isFile) {
        "Missing demo source: ${sourceFile.relativeTo(rootDir)}"
      }
      val displaySource = sourceFile
        .readText()
        .replace(Regex("""\A\s*/\*.*?\*/\s*""", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("""(?m)^\s*package\s+[A-Za-z0-9_.]+\s*\R+"""), "")
        .trim('\n')

      outputDirectory.resolve(fileName).writeText(displaySource + "\n")
    }
  }
}

tasks.register<Sync>("bundleComposeUnstyledDocs") {
  group = "documentation"
  description = "Bundles current Compose Unstyled docs and the demo web app."

  dependsOn(":demo:wasmJsBrowserDistribution")
  dependsOn(generateComposeUnstyledApiReference)
  dependsOn(generateComposeUnstyledDemoSources)

  val bundleDirectory = layout.buildDirectory.dir("docs-bundle/compose-unstyled")
  val demoDistribution = project(":demo").layout.buildDirectory.dir("dist/wasmJs/productionExecutable")

  into(bundleDirectory)
  from(composeUnstyledDocsSource.file("docs.yml"))
  from(generatedComposeUnstyledDocsPages) {
    into("pages")
  }
  from(generatedComposeUnstyledDemoSources) {
    into("demo-sources")
  }
  from(composeUnstyledDocsAssets) {
    into("assets")
  }
  from(demoDistribution) {
    into("apps/composeunstyled-v2-demos")
  }

  doFirst {
    val pages = fileTree(generatedComposeUnstyledDocsPages) {
      include("**/*.md")
    }.files
    val legacyPatterns = listOf(
      "unstyledV2Previews",
      "module=\"docsApp\"",
      "module=\"unstyledV2Previews\"",
      "```compose",
      "<ApiReference",
      "<ComposeApp",
    )
    val unstyledDemoPattern = Regex("""<UnstyledDemo\s+id="([A-Za-z0-9._-]+)"\s*/>""")

    pages.forEach { page ->
      val text = page.readText()
      legacyPatterns.forEach { pattern ->
        check(pattern !in text) {
          "Legacy Compose preview reference '$pattern' found in ${page.relativeTo(rootDir)}"
        }
      }

      Regex("""<UnstyledDemo\b[^>]*>""").findAll(text).forEach { match ->
        val demoId = unstyledDemoPattern.matchEntire(match.value)?.groupValues?.get(1)
        check(demoId != null) {
          "Invalid UnstyledDemo marker in ${page.relativeTo(rootDir)}: ${match.value}"
        }
        check(composeUnstyledDemoSources.containsKey(demoId)) {
          "No demo source registered for '$demoId' in ${page.relativeTo(rootDir)}"
        }
      }
    }
  }

  doLast {
    val outputDirectory = bundleDirectory.get().asFile
    val demoManifest = composeUnstyledDemoSources.entries.joinToString(",\n") { (id, fileName) ->
      """
          "$id": {
            "app": "composeunstyled-v2-demos",
            "source": "demo-sources/$fileName",
            "language": "kotlin"
          }""".trimIndent()
    }
    outputDirectory.resolve("manifest.json").writeText(
      """
      {
        "schemaVersion": 1,
        "library": "compose-unstyled",
        "target": {
          "sourceLibrary": "unstyled-2x"
        },
        "content": {
          "navigation": "docs.yml",
          "pages": "pages",
          "assets": "assets",
          "assetsPublicPath": "composeunstyled-v2-assets"
        },
        "apps": {
          "composeunstyled-v2-demos": {
            "path": "apps/composeunstyled-v2-demos",
            "entry": "index.html",
            "idQueryParameter": "id"
          }
        },
        "demos": {
      $demoManifest
        }
      }
      """.trimIndent() + "\n",
    )
  }
}

subprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

  plugins.withId(rootProject.libs.plugins.kotlin.multiplatform.get().pluginId) {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
      sourceSets.matching { it.name == "androidInstrumentedTest" }.configureEach {
        dependencies {
          implementation(rootProject.libs.androidx.test.runner)
        }
      }
    }
  }

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target(
        fileTree(project.projectDir) {
          include("src/**/*.kt")
          exclude("src/**/kotlin/androidx/**/*.kt")
          exclude("src/**/kotlin/com/composeunstyled/ContextDpToPx.kt")
        }
      )
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to 2,
          "continuation_indent_size" to 2,
          "ktlint_standard_filename" to "disabled",
        )
      )
      licenseHeaderFile(rootProject.file("$rootDir/spotless/copyright.kt"))
    }
    format("kts") {
      target("**/*.kts")
      targetExclude("**/build/**/*.kts")
      licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/**/*.xml")
      licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
    }
  }
}
