pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Add just the next line, keep the other repositories,https://github.com/rafaeltonholo/svg-to-compose/issues/136#issuecomment-2726784993
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }


    /**
     * //temp try fix 2vg2compose,nope
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.jetbrains.kotlin") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
            }
        }
    }
    */
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // Add jitpack,for https://github.com/jaikeerthick/Composable-Graphs
    }
}

rootProject.name = "CashZ"
include(":app")
