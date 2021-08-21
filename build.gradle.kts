plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
    id("org.jmailen.kotlinter") version "3.3.0"
    //TODO: https://github.com/mike-neck/graalvm-native-image-plugin
    //id("org.mikeneck.graalvm-native-image") version "1.3.0"
    application
    maven
}

configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.1"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("io.github.clightning4j:jrpclightning:0.2.1-SNAPSHOT")
    implementation("io.github.clightning4j:lite-bitcoin-rpc:0.0.1-rc2-SNAPSHOT")

    //Developing library
    /*api(fileTree("${project.projectDir}/devlibs") {
        include("jrpclightning-0.1.9-SNAPSHOT-with-dependencies.jar")
    }) */
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("io.vincenzopalazzo.btcli4j.AppKt")
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to application.mainClass)
        }
        from(
            configurations.runtimeClasspath.get()
                .onEach { println("add from dependencies: ${it.name}") }
                .map { if (it.isDirectory) it else zipTree(it) }
        )
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }

    register("createRunnableScript") {
        dependsOn("fatJar")
        file("$projectDir/${project.name}-gen.sh").createNewFile()
        file("$projectDir/${project.name}-gen.sh").writeText(
            """
                # Script generated from gradle! By clightning4j
                #!/bin/bash
                ${System.getProperties().getProperty("java.home")}/bin/java -jar ${project.buildDir.absolutePath}/libs/${project.name}-all.jar
            """.trimIndent()
        )
    }
}

