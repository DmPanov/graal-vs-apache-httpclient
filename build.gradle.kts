import com.palantir.gradle.docker.DockerRunPlugin

plugins {
    id("java")
    id("com.palantir.docker") version "0.20.1"
    id("com.palantir.docker-run") version "0.20.1"
}

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    compile("org.apache.httpcomponents:httpclient:4.5.2")
    compileOnly("com.oracle.substratevm:svm:GraalVM-1.0.0-rc8")
}

val nativeImageBuildContainer = "${project.name}-native-image-build-container"

docker {
    name = nativeImageBuildContainer
    setDockerfile(file("src/main/resources/Dockerfile"))
}

dockerRun {
    name = nativeImageBuildContainer
    image = nativeImageBuildContainer
    daemonize = false
    clean = true
    volumes(mapOf(tasks.getByName("dockerRun").temporaryDir.absolutePath to "/example"))
}

tasks {
    val jar = getByName<Jar>("jar") {
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
    getByName<Exec>("dockerRun") {
        dependsOn(jar, getByName("docker"))
        doFirst {
            copy {
                into(temporaryDir)
                from(jar.archivePath)
            }
        }
    }
    withType<Wrapper> {
        gradleVersion = "4.8"
        distributionUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-all.zip"
    }
}