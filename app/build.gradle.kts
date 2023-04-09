import org.jetbrains.kotlin.cli.jvm.main

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    //id("com.google.gms.google-services")
    //id("com.google.firebase.crashlytics")
    id ("dagger.hilt.android.plugin")
    id ("androidx.navigation.safeargs.kotlin")


}

kapt{
    correctErrorTypes = true
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId ="com.hbeonlabs.driversalerts"
        minSdk=Versions.MIN_Sdk
        targetSdk=Versions.TARGET_SDK
        versionCode =Versions.VERSION_CODE
        versionName =Versions.VERSION_NAME

        testInstrumentationRunner =
                "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
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
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(Libs.KTS_CORE_LIB)
    implementation(Libs.APP_COMPAT_LIB)
    implementation (Libs.MATERIAL_LIB)
    implementation (Libs.CONSTRAINT_LAYOUT_LIB)
    implementation(Libs.ROOM_RUN_TIME)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation(files("libs/webRtcLib.aar"))
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    kapt(Libs.ROOM_COMPILER)
    implementation(Libs.ROOM_KTX)
    testImplementation(Libs.JUNIT_LIB)
    androidTestImplementation(Libs.JUNIT_EXT_LIB)
    androidTestImplementation(Libs.ESPRESSO_CORE)
    // ViewModel
    implementation(Libs.VIEW_MODEL)

    // LiveData
    implementation(Libs.LIVE_DATA)
    // Annotation processor
    kapt(Libs.LIFECYCLE_COMPILER)
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation(Libs.LIFECYCELE_JAVA_SUPPORT)
    //Koin -- Dependency  Injection

    implementation(Libs.COROUTINE_LIB)
    //Firebase
   /* implementation( platform(Libs.FIREBASE_BOM))
    implementation(Libs.FIREBASE_CRASHLYTICS)*/
    //Logging
    implementation(Libs.TIMBER_LIB)
    //Memory Leak
    // debugImplementation(Libs.LEAK_CANARY_LIB)
    //Multidex
    implementation(Libs.MULTIDEX_LIB)
    // Dp SP Support
    implementation(Libs.DP_LIB)
    implementation(Libs.SP_LIB)

    // Navigation Components
    implementation (Libs.NAV_FRAGMENT_LIB)
    implementation (Libs.NAV_UI_LIB)
    implementation (Libs.VIEW_PAGER_DOTS)

    //Image Picker Library
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    // Lottie dependency
    implementation ("com.airbnb.android:lottie:5.2.0")

    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.44.2")
    kapt ("com.google.dagger:hilt-android-compiler:2.44.2")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    implementation (Libs.EASY_PERMISIONS)
    implementation ("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    implementation ("androidx.camera:camera-core:1.3.0-alpha03")
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha03")
    implementation ("androidx.camera:camera-view:1.3.0-alpha03")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha03")

    implementation("io.socket:socket.io-client:1.0.0") {
        exclude("org.json", "json")
    }
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    // Architectural Components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    implementation("io.socket:socket.io-client:1.0.0") {
        exclude("org.json", "json")
    }
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
}