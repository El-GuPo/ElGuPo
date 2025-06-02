plugins {
    alias(libs.plugins.android.application)
    id("io.freefair.lombok") version "8.4"
}

android {
    namespace = "com.ru.ami.hse.elgupo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ru.ami.hse.elgupo"
        minSdk = 26
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
    val lifecycle_version = "2.8.7"

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.mediarouter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    testImplementation("org.mockito:mockito-inline:5.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("org.mockito:mockito-android:5.18.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    implementation ("com.yandex.android:maps.mobile:4.5.0-full")

    implementation("androidx.mediarouter:mediarouter:1.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation ("androidx.recyclerview:recyclerview:1.4.0")
    implementation ("androidx.recyclerview:recyclerview-selection:1.2.0")
    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    //Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.navigation:navigation-runtime:2.7.7")


    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    implementation ("commons-validator:commons-validator:1.9.0")
}