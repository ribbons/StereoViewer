/*
 * Copyright © 2021 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    id("com.android.application")
}

android {
    compileSdk = 25

    defaultConfig {
        applicationId = "com.nerdoftheherd.stereoviewer"
        minSdk = 21
        targetSdk = 25
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        isWarningsAsErrors = true
        textReport = true
        textOutput("stdout")
    }
}
