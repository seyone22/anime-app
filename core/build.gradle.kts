plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    kotlin("plugin.serialization")
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.seyone22.core"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Ktor Client (Networking)
    implementation("io.ktor:ktor-client-core:3.4.0")
    implementation("io.ktor:ktor-client-cio:3.4.0") // Engine
    implementation("io.ktor:ktor-client-content-negotiation:3.4.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("com.apollographql.apollo:apollo-runtime:4.0.0")
    // Coroutines (for suspend functions)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

apollo {
    service("service") {
        // This is the package where your GetAiringScheduleQuery, etc., will be generated
        packageName.set("com.seyone22.core.data.remote")

        // FIX: The path must be relative to the 'core' directory.
        // Since the file is inside core/src/..., we start from "src/..."
        schemaFile.set(file("src/main/java/com/seyone22/core/data/remote/schema.graphqls"))

        // This block allows you to run :core:downloadApolloSchema easily from the IDE
        introspection {
            endpointUrl.set("https://graphql.anilist.co")
            // Point the download output to the same location used above
            schemaFile.set(file("src/main/java/com/seyone22/core/data/remote/schema.graphqls"))
        }
    }
}