/*
 * Copyright © 2021-2022 Matt Robinson
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.1")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
