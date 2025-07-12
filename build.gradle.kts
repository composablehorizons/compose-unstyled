import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.dokka").version("1.9.10").apply(false)
    id("io.github.gradle-nexus.publish-plugin").version("2.0.0-rc-1")
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(
                getLocalProperty("sonatype.stagingProfileId") ?: System.getenv("SONATYPE_STAGING_PROFILE_ID")
            )
            username.set(getLocalProperty("sonatype.username") ?: System.getenv("OSSRH_USERNAME"))
            password.set(getLocalProperty("sonatype.password") ?: System.getenv("OSSRH_PASSWORD"))
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}