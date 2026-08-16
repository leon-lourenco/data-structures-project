import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(26))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "testImplementation"(rootProject.libs.junit.jupiter)
        "testImplementation"(rootProject.libs.assertj.core)
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named("test"))
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    extensions.configure<JacocoPluginExtension> {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    // Hand-rolled JMH wiring instead of the me.champeau.jmh community plugin: that plugin's
    // last release (0.7.3, Jan 2025) is only tested up to Gradle 8.10/Java 21, and this repo
    // runs Gradle 9.7/Java 26. JMH's own annotation processor generates plain Java source (no
    // bytecode-format parsing like JaCoCo does), so it isn't exposed to the same version wall —
    // wiring it directly as a source set sidesteps the stale plugin instead of fighting it.
    val sourceSetContainer = extensions.getByType<SourceSetContainer>()
    val mainSourceSet = sourceSetContainer.getByName("main")
    val jmhSourceSet = sourceSetContainer.create("jmh") {
        java.srcDir("src/jmh/java")
        compileClasspath += mainSourceSet.output
        runtimeClasspath += output + mainSourceSet.output
    }

    configurations.named("jmhImplementation") {
        extendsFrom(configurations.getByName("implementation"))
    }

    dependencies {
        "jmhImplementation"(rootProject.libs.jmh.core)
        "jmhAnnotationProcessor"(rootProject.libs.jmh.generator.annprocess)
    }

    tasks.register<JavaExec>("jmh") {
        group = "benchmark"
        description = "Runs this module's JMH microbenchmarks."
        dependsOn("jmhClasses")
        mainClass.set("org.openjdk.jmh.Main")
        classpath = jmhSourceSet.runtimeClasspath
    }
}
