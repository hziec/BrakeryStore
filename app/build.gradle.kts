plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bakerystore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bakerystore"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    // Android cơ bản
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // RecyclerView hiển thị danh sách sản phẩm, giỏ hàng, lịch sử
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView để làm card sản phẩm đẹp hơn
    implementation("androidx.cardview:cardview:1.0.0")

    // Retrofit gọi API backend ASP.NET
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Gson converter parse JSON từ API
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Glide load ảnh sản phẩm từ URL
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}