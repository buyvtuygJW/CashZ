// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    //ext.objectboxVersion = "4.2.0" // For Groovy build scripts
    val objectboxVersion by extra("4.2.0") // For KTS build scripts
    val kotlin_version by extra("2.1.0") // Update to the latest version
    val hilt_version by extra("2.56")   // Update to the latest version,based on https://github.com/piashcse/Hilt-MVVM-Compose-Movie/blob/master/gradle/libs.versions.toml


    repositories {
        mavenCentral()
    }

    dependencies {
        //base?
        classpath("com.android.tools.build:gradle:8.9.2")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")

        //object box deps
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")

        //hilt must have
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")//must at least 2.53 with kotlin 2.1 else error.

    }
}


/**
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    //dependency,eg from official,https://developer.android.com/training/dependency-injection/hilt-android#kts
    id("com.google.dagger.hilt.android") version "2.53" apply false
    id("com.android.library") version "7.4.0" apply false

}