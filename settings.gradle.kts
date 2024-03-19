enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
        maven(url = uri("https://jitpack.io"))
    }
}

rootProject.name = "core"
include(":androidApp")
include(":shared")