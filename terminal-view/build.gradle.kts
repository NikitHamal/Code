plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux.view"

    dependencies {
        implementation("androidx.annotation:annotation:1.9.1")
        api(project(":terminal-emulator"))
    }

    defaultConfig {
        minSdk = 28
        compileSdk = 35

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
