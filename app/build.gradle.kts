import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").inputStream().use {
        load(it)
    }
}

val giphyApiKey = localProperties.getProperty("GIPHY_API_KEY")
    ?.takeIf { it.isNotBlank() }
    ?: error("Добавь GIPHY_API_KEY в local.properties")

android {
    namespace = "com.esyhonotdead.vk_edu_kotlin_hw2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.esyhonotdead.vk_edu_kotlin_hw2"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "GIPHY_API_KEY",
            "\"$giphyApiKey\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    implementation(libs.androidx.room3.paging)
    val paging_version = "3.5.3"
    val room_version = "3.0.3"

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.paging.compose)
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("androidx.room3:room3-runtime:$room_version")
    ksp("androidx.room3:room3-compiler:$room_version")
}
