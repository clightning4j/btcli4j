plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"

    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.8.1")
    implementation("io.github.clightning4j:jrpclightning:0.1.8")

    /*
    //Developing library
    api(fileTree("${project.projectDir}/devlib") {
        include("jrpclightning-0.1.9-SNAPSHOT-with-dependencies.jar")
    }) */
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "io.vincenzopalazzo.btcli4j.AppKt"
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to application.mainClassName)
        }
        from(configurations.runtimeClasspath.get()
                .onEach { println("add from dependencies: ${it.name}") }
                .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }

    register("createRunnableScript") {
        dependsOn("fatJar")
        file("${projectDir}/${project.name}-gen.sh").createNewFile()
        file("${projectDir}/${project.name}-gen.sh").writeText(
                """
                # Script generated from gradle! By clightning4j
                #!/bin/bash
                ${System.getProperties().getProperty("java.home")}/bin/java -jar ${project.buildDir.absolutePath}/libs/${project.name}-all.jar
                """.trimIndent())
    }
}