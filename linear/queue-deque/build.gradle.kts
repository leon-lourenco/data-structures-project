// The benchmark contrasts this module's circular-buffer removeFirst() against
// linear:dynamic-array's DynamicArray.remove(0) at the same sizes, so the JMH source set needs
// a compile/runtime dependency on that module. Everything else (java, jacoco, the jmh source
// set itself, JMH core/annotation-processor deps) is already wired globally in the root
// build.gradle.kts's subprojects {} block.
dependencies {
    "jmhImplementation"(project(":linear:dynamic-array"))
}
