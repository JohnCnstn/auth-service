object Versions {

    // Do not forget to update buildSrc/main/Versions.kt
    const val openapiGenerator = "3.3.4"      // https://mvnrepository.com/artifact/org.openapitools/openapi-generator

}

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openapitools:openapi-generator:${Versions.openapiGenerator}")
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
sourceSets["main"].java.srcDirs("main")
sourceSets["main"].resources.srcDirs("resources")
