import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.compose.hotreload) apply false
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.detekt).apply(false)
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