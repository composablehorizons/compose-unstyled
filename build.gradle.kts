plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.compose).apply(false)
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.library).apply(false)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.spotless)
}

val composeUnstyledDocsSource = layout.projectDirectory.dir("docs")
val composeUnstyledDocsSourcesFile = composeUnstyledDocsSource.file("sources.yml")
val generatedComposeUnstyledDocsPages = layout.buildDirectory.dir("generated/compose-unstyled-docs/pages")
val generatedComposeUnstyledDemoSources = layout.buildDirectory.dir("generated/compose-unstyled-docs/demo-sources")
val composeUnstyledDocsAssets = composeUnstyledDocsSource.dir("assets")
val composeUnstyledPublishVersion = providers
  .gradleProperty("publishVersion")
  .orElse(libs.versions.unstyled)
  .get()

extra["publishVersion"] = composeUnstyledPublishVersion

data class DocsSourceSection(
  val root: String,
  val files: Map<String, String>,
)

data class DocsSources(
  val demos: DocsSourceSection,
)

fun readDocsSources(file: File): DocsSources {
  val roots = linkedMapOf<String, String>()
  val files = linkedMapOf("demos" to linkedMapOf<String, String>())
  var currentSection: String? = null
  var inFiles = false

  file.readLines().forEachIndexed { index, rawLine ->
    val lineWithoutComment = rawLine.replace(Regex("""\s+#.*$"""), "")
    if (lineWithoutComment.isBlank()) return@forEachIndexed

    val indent = lineWithoutComment.indexOfFirst { it.isWhitespace().not() }.let {
      if (it == -1) 0 else it
    }
    val line = lineWithoutComment.trim()

    if (indent == 0) {
      check(line.endsWith(":")) {
        "Invalid docs sources entry at ${file.relativeTo(rootDir)}:${index + 1}: $rawLine"
      }
      val section = line.removeSuffix(":")
      check(section in files.keys) {
        "Unsupported docs sources section '$section' at ${file.relativeTo(rootDir)}:${index + 1}"
      }
      currentSection = section
      inFiles = false
      return@forEachIndexed
    }

    check(currentSection != null) {
      "Invalid docs sources entry at ${file.relativeTo(rootDir)}:${index + 1}: $rawLine"
    }

    if (indent == 2) {
      if (line == "files:") {
        inFiles = true
        return@forEachIndexed
      }

      val separator = line.indexOf(": ")
      check(separator > 0 && line.take(separator) == "root") {
        "Invalid docs sources section entry at ${file.relativeTo(rootDir)}:${index + 1}: $rawLine"
      }
      roots[currentSection!!] = line.drop(separator + 2)
      inFiles = false
      return@forEachIndexed
    }

    check(indent == 4 && inFiles) {
      "Invalid docs sources mapping at ${file.relativeTo(rootDir)}:${index + 1}: $rawLine"
    }

    val separator = line.indexOf(": ")
    check(separator > 0) {
      "Invalid docs sources mapping at ${file.relativeTo(rootDir)}:${index + 1}: $rawLine"
    }
    files.getValue(currentSection!!)[line.take(separator)] = line.drop(separator + 2)
  }

  check(roots["demos"]?.isNotBlank() == true) {
    "Missing root for docs sources section 'demos' in ${file.relativeTo(rootDir)}"
  }
  check(files.getValue("demos").isNotEmpty()) {
    "Missing files for docs sources section 'demos' in ${file.relativeTo(rootDir)}"
  }

  return DocsSources(
    demos = DocsSourceSection(root = roots.getValue("demos"), files = files.getValue("demos")),
  )
}

val composeUnstyledDocsSources = readDocsSources(composeUnstyledDocsSourcesFile.asFile)
val composeUnstyledDemoSourceRoot = layout.projectDirectory.dir(composeUnstyledDocsSources.demos.root)
val composeUnstyledDemoSources = composeUnstyledDocsSources.demos.files

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

  inputs.file(composeUnstyledDocsSourcesFile)
  inputs.files(composeUnstyledDemoSources.values.distinct().map { composeUnstyledDemoSourceRoot.file(it) })
  outputs.dir(generatedComposeUnstyledDemoSources)

  doLast {
    val outputDirectory = generatedComposeUnstyledDemoSources.get().asFile
    outputDirectory.deleteRecursively()
    outputDirectory.mkdirs()

    composeUnstyledDemoSources.values.distinct().forEach { fileName ->
      val sourceFile = composeUnstyledDemoSourceRoot.file(fileName).asFile
      check(sourceFile.isFile) {
        "Missing demo source: ${sourceFile.relativeTo(rootDir)}"
      }
      val displaySource = sourceFile
        .readText()
        .replace(Regex("""\A\s*/\*.*?\*/\s*""", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("""(?m)^\s*package\s+[A-Za-z0-9_.]+\s*\R+"""), "")
        .trim('\n')

      val outputFile = outputDirectory.resolve(fileName)
      outputFile.parentFile?.mkdirs()
      outputFile.writeText(displaySource + "\n")
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

  inputs.file(composeUnstyledDocsSourcesFile)

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
            "title": "examples/$fileName",
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
