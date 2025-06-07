plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")


    id ("kotlin-kapt")

    //hilt
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.qrattendance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.qrattendance"
        minSdk = 28
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.0")

    // network
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    // hilt
    implementation("com.google.dagger:hilt-android:2.46")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    kapt ("com.google.dagger:hilt-compiler:2.46")

    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("com.airbnb.android:lottie:5.2.0")

    //
    implementation ("androidx.drawerlayout:drawerlayout:1.1.1")

    implementation ("com.airbnb.android:lottie:5.2.0")

    // logging interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}