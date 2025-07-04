plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.ringqrapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ringqrapp"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources.excludes.add("META-INF/NOTICE.txt")
        resources.excludes.add("META-INF/LICENSE.txt")
        // Thêm dòng này vào
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }
    buildFeatures{
        viewBinding = true
        //compose = true // Nếu bạn sử dụng Jetpack Compose
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    //implementation(mapOf("name" to "qring_sdk_20250516", "ext" to "aar"))
    //implementation(files("libs/qring_sdk_20250516.aar")) nếu bạn không dùng flatDir
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation (libs.xxpermissions)
    
    // Firebase
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database)

    //eventbus
    implementation(libs.eventbus)

    //qiuckadapter
    implementation (libs.baserecyclerviewadapterhelper)
    implementation (libs.androidx.localbroadcastmanager)
    implementation (libs.fileselector)

    implementation (libs.loadingdialog)
    implementation(libs.okhttp)
    implementation (libs.fast.android.networking.android.networking)


    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.constraintlayout)
    kapt(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    implementation (libs.roundedimageview)
    
    // Testing
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)

    //lotties
    implementation (libs.lottie)


    implementation (libs.mpandroidchart)
}
