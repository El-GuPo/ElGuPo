plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ru.ami.hse.elgupo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ru.ami.hse.elgupo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MAPKIT_API_KEY", "\"${rootProject.extra["MAPKIT_API_KEY"]}\"")
    }

    buildTypes {
        debug {
            resValue("string", "yandex_maps_key", "\"${rootProject.extra["MAPKIT_API_KEY"]}\"")
        }
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

    dataBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

//    implementation("com.yandex.android:maps.mobile:4.5.0-lite")
    implementation ("com.yandex.android:maps.mobile:4.5.0-full")

}