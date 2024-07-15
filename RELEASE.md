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

The following command uploads all kotlin multiplatform artifacts to Sonatype Nexus and then closes the release.

The commands that follows need to be run together. Otherwise you will get an error: `No staging repository with name sonatype created`

```shell
./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository
```

As soon as the version is available it will be listed at: https://repo1.maven.org/maven2/com/composables/