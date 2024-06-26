plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "de.htw_berlin.mob_sys.biketrackingberlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.htw_berlin.mob_sys.biketrackingberlin"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.osmdroid.android)
    implementation(libs.osmdroid.wms)
    implementation(libs.osmdroid.mapsforge)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")



    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // JUnit 4 für Android Tests
    testImplementation("junit:junit:4.13.2")

    // AndroidX Test Kernbibliotheken
    androidTestImplementation("androidx.test:core:1.4.0")

    // AndroidX JUnit Erweiterungen
    androidTestImplementation("androidx.test.ext:junit:1.1.3")

    // Mockito für Mocking von Abhängigkeiten in Tests
    testImplementation("org.mockito:mockito-core:3.11.2")

    // Room Test Support
    androidTestImplementation("androidx.room:room-testing:2.4.0")

    // Wenn Sie Espresso-Tests schreiben möchten (optional, für UI-Tests)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")



    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    implementation("com.google.maps.android:android-maps-utils:2.2.5")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
}