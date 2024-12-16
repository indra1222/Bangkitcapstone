plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.example.sibantubaru"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.sibantubaru"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Google Maps
    implementation(libs.google.maps)
    implementation(libs.google.location)

    // Image Loading
    implementation(libs.glide)

    // RecyclerView
    implementation(libs.recyclerview)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.gson.core)
    implementation(libs.kotlin.parcelize.runtime)

    implementation(libs.circleimageview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase Authentication
    implementation (libs.firebase.auth)
    implementation (libs.firebase.firestore)

    // Google Sign-In
    implementation (libs.play.services.auth)

    // Material Design
    implementation (libs.material.v1100)

    // Coroutines for async tasks
    implementation (libs.kotlinx.coroutines.play.services)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.androidx.appcompat.v161)

    implementation (libs.retrofit.v2110)
    implementation (libs.converter.gson.v2110)
    implementation (libs.okhttp)
    implementation (libs.glide.v4130)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    implementation (libs.androidx.viewpager2)
    implementation (libs.material.v140)
    implementation (libs.dotsindicator)


}
