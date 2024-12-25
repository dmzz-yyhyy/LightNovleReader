import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "indi.dmzz_yyhyy.lightnovelreader"
    compileSdk = 34

    defaultConfig {
        multiDexEnabled = true
        applicationId = "indi.dmzz_yyhyy.lightnovelreader"
        minSdk = 24
        targetSdk = 34
        // 版本号为x.y.z则versionCode为x*1000000+y*10000+z*100+debug版本号(开发需要时迭代, 两位数)
        versionCode = 1_00_00_011
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
        val hostname = System.getenv("HOSTNAME") ?: System.getenv("COMPUTERNAME") ?: try {
            InetAddress.getLocalHost().hostName
        } catch (_: Exception) {}
        resValue("string", "info_build_date", dateFormat.format(Date()))
        resValue("string", "info_build_host",
            System.getProperty("user.name") + "@" + hostname + "\n"
                    + " " + System.getProperty("os.name") + "/" + System.getProperty("os.arch"))
        setProperty("archivesBaseName", "LightNovelReader-${versionName}")
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isJniDebuggable = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // desugaring support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
    // android lib
    implementation("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    // compose
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.animation:animation-graphics-android:1.7.1")
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.material3:material3:1.3.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    // junit
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // hilt
    val hilt = "2.49"
    implementation("com.google.dagger:hilt-android:$hilt")
    kapt("com.google.dagger:hilt-android-compiler:$hilt")
    val androidXHilt = "1.2.0"
    implementation("androidx.hilt:hilt-common:$androidXHilt")
    implementation("androidx.hilt:hilt-compiler:$androidXHilt")
    kapt("androidx.hilt:hilt-compiler:$androidXHilt")
    implementation("androidx.hilt:hilt-work:$androidXHilt")
    implementation("androidx.hilt:hilt-navigation-compose:$androidXHilt")
    // navigation
    val navVersion = "2.8.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    // coil
    implementation("io.coil-kt:coil-compose:2.6.0")
    // jsoup
    implementation("org.jsoup:jsoup:1.18.1")
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    // markdown
    implementation("com.github.jeziellago:compose-markdown:0.5.2")
    // Ketch
    //noinspection GradleDependency
    implementation("com.github.khushpanchal:Ketch:1.0.0")
    // room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    implementation("androidx.room:room-guava:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    // Splash API
    implementation("androidx.core:core-splashscreen:1.0.1")
    val appCenterSdkVersion = "5.0.4"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
    val workVersion = "2.9.1"
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("androidx.work:work-rxjava2:$workVersion")
    implementation("androidx.work:work-gcm:$workVersion")
    androidTestImplementation("androidx.work:work-testing:$workVersion")
    implementation("androidx.work:work-multiprocess:$workVersion")
}

kapt {
    correctErrorTypes = true
}

configurations.implementation{
    exclude(group = "com.intellij", module = "annotations")
}

task("printVersion") {
    doFirst {
        println(android.defaultConfig.versionName)
    }
}

task("printVersionCode") {
    doFirst {
        println(android.defaultConfig.versionCode)
    }
}