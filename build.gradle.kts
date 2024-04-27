import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.dokka").version("1.9.10").apply(false)
    id("io.github.gradle-nexus.publish-plugin").version("2.0.0-rc-1")
}

nexusPublishing {
    repositories {
        sonatype {
            with(gradleLocalProperties(rootDir)) {
                stagingProfileId.set(getProperty("sonatype.stagingProfileId") ?: System.getenv("SONATYPE_STAGING_PROFILE_ID"))
                username.set(getProperty("sonatype.username") ?: System.getenv("OSSRH_USERNAME"))
                password.set(getProperty("sonatype.password") ?: System.getenv("OSSRH_PASSWORD"))
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            }
        }
    }
}