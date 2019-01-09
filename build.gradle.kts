plugins {
    id("java")
}

repositories {
    jcenter()
    mavenLocal()
}

val graalVersion = "1.0.0-rc10"

dependencies {
    compile("org.apache.httpcomponents:httpclient:4.5.2")
    compileOnly("com.oracle.substratevm:svm:GraalVM-$graalVersion")
}

tasks {
    withType<Jar> {
        archiveName = "${project.name}.$extension"
        from(configurations.compile.map {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            if (it.isDirectory) it else zipTree(it)
        })
        // exclude third-party manifest signature files
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        manifest {
            attributes(mapOf("Main-Class" to "Main"))
        }
    }
    create<Exec>("run") {
        commandLine("docker", "run", "--rm",
                "--volume", "${projectDir.absolutePath}:${projectDir.absolutePath}",
                "--workdir", projectDir.absolutePath,
                "oracle/graalvm-ce:$graalVersion",
                "./run.sh", graalVersion)
    }
    withType<Wrapper> {
        gradleVersion = "4.8"
        distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
    }
}