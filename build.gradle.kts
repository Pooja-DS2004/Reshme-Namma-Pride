// Top-level build file
buildscript {
    extra["compose_version"] = "1.5.4"
    extra["room_version"] = "2.6.1"
    extra["nav_version"] = "2.7.6"
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
