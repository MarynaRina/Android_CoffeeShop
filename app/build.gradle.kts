plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.coffee_shop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.coffee_shop"
        minSdk = 24
        targetSdk = 35
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}
dependencies {
    implementation(platform(libs.firebase.bom))

    // Firebase libraries
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.appcheck.playintegrity)

    // Android libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Third-party libraries
    implementation(libs.circleimageview)
    implementation(libs.glide)
    implementation(libs.cloudinary.android)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Annotation processor for Glide or other tools
    annotationProcessor(libs.compiler)
}