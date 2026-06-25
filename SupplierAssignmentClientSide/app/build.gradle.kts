plugins {
    alias(libs.plugins.android.application)
    id("androidx.room") version "2.6.1"
}

android {
    namespace = "com.example.supplierassignment"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.supplierassignment"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.recyclerview)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.room.rxjava3)
    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.8.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.mockwebserver)
}