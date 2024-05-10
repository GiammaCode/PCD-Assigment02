plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //add vert.x module
    implementation("io.vertx:vertx-core:4.5.7")
    implementation("io.vertx:vertx-web:4.5.7")
    implementation("io.vertx:vertx-web-client:4.5.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")

    implementation ("org.jsoup:jsoup:1.14.3")
}

tasks.test {
    useJUnitPlatform()
}