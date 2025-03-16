import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

extra["MAPKIT_API_KEY"] = localProperties.getProperty("MAPKIT_API_KEY", "")
