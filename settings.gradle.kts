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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("app/libs")
        }
        maven {
            url = uri("https://jitpack.io")
            //url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        } // Nếu cần
    }
}

rootProject.name = "RingQrApp"
include(":app")
 