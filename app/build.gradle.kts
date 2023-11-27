import org.jetbrains.kotlin.cli.jvm.main

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    //id("com.google.gms.google-services")
    //id("com.google.firebase.crashlytics")
    id ("dagger.hilt.android.plugin")
    id ("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")


}

kapt{
    correctErrorTypes = true
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId ="com.hbeonlabs.driversalerts"
        minSdk= 25
        targetSdk= 33
        versionCode = 1
        versionName ="1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"

    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation ("androidx.core:core-ktx:1.10.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.8.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.6")
    implementation("com.google.firebase:firebase-analytics-ktx:21.2.2")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    // navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-dynamic-features-fragment:2.5.3")


    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")


    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")
    annotationProcessor("androidx.room:room-compiler:2.5.1")




    // Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")

    //Multidex
    implementation("androidx.multidex:multidex:2.0.1")


    // Dp SP Support
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")

    // Navigation Components
    implementation ("me.relex:circleindicator:2.1.6")

    //Image Picker Library
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    // Lottie dependency
    implementation ("com.airbnb.android:lottie:6.0.0")

    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.45")
    kapt ("com.google.dagger:hilt-android-compiler:2.45")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    implementation ("pub.devrel:easypermissions:3.0.0")
    implementation ("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    implementation ("androidx.camera:camera-core:1.3.0-alpha06")
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation ("androidx.camera:camera-view:1.3.0-alpha06")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha06")

    // Architectural Components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Activity KTX for viewModels()
    implementation ("androidx.activity:activity-ktx:1.7.1")

    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //DataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // WorkManager Kotlin + Coroutines
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Paging 3
    implementation ("androidx.paging:paging-runtime-ktx:3.2.0-alpha04")


    //Live streaming
    implementation("io.livekit:livekit-android:1.1.10")
    implementation("com.squareup.leakcanary:leakcanary-android:2.12")
}