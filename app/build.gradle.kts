import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.dagger.hilt.android)
}

val properties = Properties().apply {
    rootProject.file("local.properties").reader().use(::load)
}

val propOpenExchangeRatesBaseUrl = properties["openExchangeRates.baseUrl"] as String
val propOpenExchangeRatesApikey = properties["openExchangeRates.apikey"] as String

android {
    namespace = "emperorfin.android.multicurrencyconverter"
    compileSdk = 34

    defaultConfig {
        applicationId = "emperorfin.android.multicurrencyconverter"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OPEN_EXCHANGE_RATES_BASE_URL", propOpenExchangeRatesBaseUrl)
        buildConfigField("String", "OPEN_EXCHANGE_RATES_API_KEY", propOpenExchangeRatesApikey)
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
    buildFeatures {
        compose = true

        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.google.gson)
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.hilt.compiler)
    implementation(libs.google.accompanist.insets)
    implementation(libs.github.skydoves.sandwich)
    implementation(libs.github.skydoves.landscapist.coil)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.squareup.retrofit.converter.gson)

    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
}