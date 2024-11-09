plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.isar.imagine"
    compileSdk = 35
    packagingOptions {
        exclude("META-INF/*")
    }
    defaultConfig {
        applicationId = "com.isar.imagine"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    buildFeatures {
        viewBinding = true
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.database)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.object1.detection.common)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx.v252)
    //dexter
    implementation(libs.dexter)
    implementation(libs.retrofit)
    implementation(libs.play.services.vision)
    // gson converter
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.core)
    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)

    //poi for creating excel
    implementation(libs.poi.v530)
    implementation(libs.poi.ooxml)
    //pdf generator library
    implementation(libs.pdf.invoice.generator)


// Barcode scanning API
    implementation(libs.barcode.scanning.v1703)
// CameraX library

    implementation(libs.androidx.camera.camera2.v101)
    implementation(libs.androidx.camera.lifecycle.v101)
    implementation(libs.androidx.camera.view.v100alpha28)

    implementation(libs.mpandroidchart)

    implementation(libs.guava) // Use the latest version available

}