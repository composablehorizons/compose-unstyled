# Releasing to Maven Central

## Pre-requirements

Add your sonatype credentials to your `local.properties` file (create the file if missing) in this format:

```properties
sonatype.username=
sonatype.password=
sonatype.stagingProfileId=
signing.key=
signing.password=
signing.keyId=
```

## Release to Maven Central

The following script uploads all kotlin multiplatform artifacts to Sonatype Nexus and then closes the release.

It also updates the installation blocks through the docs, and drafts a release after it is done:

```shell
./scripts/release_version.sh
```

As soon as the version is available it will be listed at: https://repo1.maven.org/maven2/com/composables/core/

## Release to Maven Local

The following command uploads all kotlin multiplatform artifacts to Maven Local so that you can use the dependency
locally in other projects stored in your computer:

```shell
./gradlew publishToMavenLocal
```

When the task completes successfully, you can add the published artifact in your project. Make sure the other projects
use `mavenLocal()` in their `settings.gradle.kts` dependencies block.