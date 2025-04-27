// Apply the Hilt Gradle plugin
apply(plugin = "dagger.hilt.android.plugin")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //added for compose plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"

    //serialization for foss lib
    kotlin("plugin.serialization") version "2.1.10"

    //nosql

    //realm
    //kotlin("kapt") version "1.5.21"
    //id("io.realm.kotlin") version("3.3.1") apply false
    //id("io.realm.kotlin")
    //try community fork,nope...,https://github.com/XilinJia/krdb
    //classpath ("io.github.xilinjia.krdb:gradle-plugin:y.y.y")

    //objectbox,nosql
    //id("java-library") // or org.jetbrains.kotlin.jvm for Kotlin projects.
    id("io.objectbox")
    //id("org.jetbrains.kotlin.jvm")

    //graph lib?

    //for bottomnav custom icon,https://github.com/rafaeltonholo/svg-to-compose/blob/main/svg-to-compose-gradle-plugin/README.md
    id("dev.tonholo.s2c") version "2.1.2-SNAPSHOT"

    //dependency injection
    id("kotlin-kapt")//redundant.
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")//do not specify version else may throw error.
    id("dagger.hilt.android.plugin")//MUST HAVE for actually dep inject
}

/**
//cpmf for,https://github.com/rafaeltonholo/svg-to-compose
//src to fix error if no optimize(false)>https://github.com/rafaeltonholo/svg-to-compose/issues/134#issuecomment-2722861566
 */
svgToCompose {
    processor {
        common {
            //optimize(true)
            optimize(false)
            recursive()
            icons {
                noPreview()//theme("noprofit.foss.ui.theme.TestTheme")//your project theme.
                minify()
            }
        }

        //note,IF svg not Vector Drawable XML then must be in raw folder.
        //this kinda broken?
        val outlinedIcons by creating {
            from(layout.projectDirectory.dir("src/main/res/raw/customoutlinedicon"))
            destinationPackage("foss.utils.autosvgcomposeui.uiiconsoutlined")
        }

        //code call eg(Note it is directly dependant on this package name and the file name in that dir)>foss.utils.autosvgcomposeui.uiicons.outlined.Statssvgrepo
        val filledIcons by creating {
            from(layout.projectDirectory.dir("src/main/res/raw/customfilledicon"))
            destinationPackage("foss.utils.autosvgcomposeuiiconsfilled")
        }
    }
}

android {
    namespace = "noprofit.foss"
    compileSdk = 35

    defaultConfig {
        applicationId = "noprofit.foss"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    //refer,needed to pass function into compose button,https://developer.android.com/jetpack/androidx/releases/compose-compiler
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    /*
    to fix,for svg convert..after more testing find not needed>
    Duplicate class org.jetbrains.annotations.Nls found in modules annotations-23.0.0.jar -> annotations-23.0.0 (org.jetbrains:annotations:23.0.0) and kotlin-gradle-plugin-2.1.10-gradle85.jar -> kotlin-gradle-plugin-2.1.10-gradle85 (org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10)
    Duplicate class org.jetbrains.annotations.Nls$Capitalization found in modules annotations-23.0.0.jar -> annotations-23.0.0 (org.jetbrains:annotations:23.0.0) and kotlin-gradle-plugin-2.1.10-gradle85.jar -> kotlin-gradle-plugin-2.1.10-gradle85 (org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10)
    configurations {
        all {
            resolutionStrategy {
                force("org.jetbrains:annotations:23.0.0")
            }
        }
    }


     */

    //solution for roboelectric setup MUST p2,https://github.com/robolectric/robolectric/issues/3328
    testOptions {
        unitTests {
            isIncludeAndroidResources  = true
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //compose appcompat
    implementation(libs.androidx.appcompat)

    //official compose core
    val composebom=platform("androidx.compose:compose-bom:2025.01.01")
    implementation(composebom)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.activity:activity-compose:1.10.1")

    //if pick use old material api
    //google old base material,needed for material ui layout
    implementation("com.google.android.material:material:1.12.0")
    //adapt compose into classic androidv1
    //implementation("androidx.compose.material:material:latest")
    //you should pick,v3
    //implementation("androidx.compose.material3:material3:latest")
    implementation ("androidx.compose.material3:material3:1.3.2")

    //ui
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-graphics:1.7.8")
    implementation ("androidx.compose.ui:ui-tooling:1.7.8")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.8")

    //compose modules,kinda optional
    //implementation ("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    //common lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    //optional
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //import for util(opt),untested
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")//minimum import for androidx.compose.runtime.livedata.observeAsState
    //implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    //sqllite step1,deps setup,src,https://www.slingacademy.com/article/best-practices-for-sqlite-integration-in-kotlin/
    implementation ("androidx.sqlite:sqlite:2.5.0")


    //for objectbox util,json in out
    implementation("com.google.code.gson:gson:2.12.1")//way1,use gson
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")//way2,kotlin serialization, util uses this.

    //foss lib for faster dev,but do not support compose.https://github.com/guolindev/PermissionX
    //implementation ("com.guolindev.permissionx:permissionx:1.8.1")

    // For using in Jetpack Compose,https://github.com/PatilShreyas/permission-flow-android
    //implementation ("dev.shreyaspatil.permission-flow:permission-flow-compose:2.0.0")

    //graph lib1,LOL need to edit the latest_version word in the doc to a number.... it WORKED!,https://github.com/ehsannarmani/ComposeCharts
    implementation ("io.github.ehsannarmani:compose-charts:0.1.2")

    //graphlib2
    //implementation ("com.himanshoe:charty:latest_version")

    //src,https://github.com/jaikeerthick/Composable-Graphs
    implementation("com.github.jaikeerthick:Composable-Graphs:1.2.3") //ex: v1.2.3


    //graph vico,src,https://www.patrykandpatrick.com/vico/guide/stable/getting-started
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m2)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.views)


    //src,https://stackoverflow.com/questions/30434451/how-to-copy-files-to-android-emulator-instance

    //svg convert, build tool 101
    //implementation("dev.tonholo.s2c:svg-to-compose-gradle-plugin:2.1.1")//this waiting for update to 2.1.2,else may throw error.//NOT NEEDED TO WORK ACTUALLy

    //nosql lib.
    /**
     * //realm
    implementation("io.realm:realm-android-library:10.19.0")
    kapt("io.realm:realm-annotations:10.19.0")
    kapt("io.realm:realm-annotations-processor:10.19.0")
    implementation ("io.realm.kotlin:library-base:1.16.0")
    // If using Device Sync
    implementation ("io.realm.kotlin:library-sync:1.16.0")
    // If using coroutines with the SDK
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    */

    //hm?NOPE,$$$
    //implementation("com.couchbase.lite:couchbase-lite-android-ktx:3.2.2")

    // ObjectBox dependencies,ai&official src,https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html
    //official site,https://objectbox.io/
    var objectboxVersion="4.2.0"
    /**
     * //basic deps?
    implementation("io.objectbox:objectbox-android:4.2.0")
    implementation("io.objectbox:objectbox-kotlin:4.2.0")
    kapt("io.objectbox:objectbox-processor:4.2.0")
    */

    //refer advanced??https://docs.objectbox.io/advanced/advanced-setup
    implementation("io.objectbox:objectbox-java:$objectboxVersion")
    // Kotlin extension functions
    implementation("io.objectbox:objectbox-kotlin:$objectboxVersion")
    // Annotation processor
    kapt("io.objectbox:objectbox-processor:$objectboxVersion")
    // Native library for Android
    implementation("io.objectbox:objectbox-android:$objectboxVersion")

    // Hilt dependencies,old version has error in newer kotlin,https://github.com/google/dagger/issues/4451
    //implementation("com.google.dagger:hilt-android:2.51.1")
    //kapt("com.google.dagger:hilt-android-compiler:2.51.1")//has error with old ver

    implementation("com.google.dagger:hilt-android:2.56")
    kapt("com.google.dagger:hilt-android-compiler:2.56")

    //optional depend on case
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")//old deps,refer,https://stackoverflow.com/questions/73940989/dagger-hilt-android-internal-lifecycle-defaultactivityviewmodelfactory-could-n
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")//optional for inject into some other place at runtime with provider style>class NoSQLHelperProvider @Inject constructor(val noSQLHelper: NoSQLHelper)

    //for utils library,security
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha07")

    //std kotlin lib for (hilt&security)
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")

    //for nosql to sql,reflectionm,follow $kotlin_version
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:2.1.0")

    //ml
    //implementation ("org.apache.commons:commons-math4-legacy-core:4.0-beta1")//if use v4
    //implementation ("org.apache.commons:commons-math4-core:4.0-beta1")
    implementation ("org.apache.commons:commons-math3:3.6.1")

    testImplementation(libs.junit)
    //extra test for project quality.
    //test file system
    testImplementation("org.mockito:mockito-core:3.12.4") // For mocking dependencies
    testImplementation("org.mockito:mockito-inline:3.12.4")
    //to combo mockito to test android context
    testImplementation ("org.robolectric:robolectric:4.14.1")//v4.6.1 will throw.Package targetSdkVersion=35 > maxSdkVersion=30
    testImplementation ("org.robolectric:shadows-framework:4.14.1")// Add Robolectric configuration
    testImplementation ("androidx.test:core:1.6.1")


    //extra?
    testImplementation("androidx.test.ext:junit:1.2.1")

    //androidTestImplementation(libs.androidx.espresso.core)
}

kapt{
    correctErrorTypes = true
}

// apply the plugin after the dependencies block so it does not automatically add objectbox-android
// which would conflict with objectbox-android-objectbrowser on debug builds
apply(plugin = "io.objectbox")
