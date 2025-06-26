/*
 * Copyright © 2021-2025 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    id("com.android.application")
}

fun gitVersionCode(): Int {
    val result =
        providers.exec {
            commandLine = arrayListOf("git", "rev-list", "--count", "HEAD")
        }

    return result.standardOutput.asText.get().trimEnd().toInt()
}

fun gitVersionName(): String {
    val result =
        providers.exec {
            commandLine = arrayListOf("git", "describe", "--tags", "--always")
        }

    return result.standardOutput.asText.get().trimEnd().replace("-g", "-")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

android {
    namespace = "com.nerdoftheherd.stereoviewer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nerdoftheherd.stereoviewer"
        minSdk = 21
        targetSdk = 34
        versionCode = gitVersionCode()
        versionName = gitVersionName()
    }

    buildTypes {
        release {
            isCrunchPngs = false // Legacy launcher icons are pre-crunched
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
        }

        debug {
            applicationIdSuffix = ".debug"
        }
    }

    lint {
        warningsAsErrors = true
        textReport = true

        // Causes unrelated PR failures after a new Gradle release
        disable += "AndroidGradlePluginVersion"

        // Dependabot notifies us about new versions and failing the
        // build causes problems updating single dependencies via PRs
        disable += "GradleDependency"

        // GitHub Actions installs pre-release SDKs which triggers
        // this before the final SDK, AGP & Android Studio release
        disable += "OldTargetApi"
    }
}
