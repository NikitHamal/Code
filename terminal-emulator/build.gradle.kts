plugins {
    id("com.android.library")
}

android {
    namespace = "com.termux.emulator"

    this.ndkVersion = "27.2.12479018"

    defaultConfig {
        minSdk = 28
        compileSdk = 35

        externalNativeBuild {
            ndkBuild {
                cFlags += listOf("-std=c11", "-Wall", "-Wextra", "-Werror", "-Os", "-fno-stack-protector", "-Wl,--gc-sections")
            }
        }

        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = File("src/main/jni/Android.mk")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.9.1")
    testImplementation("junit:junit:4.13.2")
}
