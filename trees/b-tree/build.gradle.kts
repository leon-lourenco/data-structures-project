// This module's benchmark measures its B-tree's height directly against
// trees:binary-search-tree's BinarySearchTree for the same key set (see src/jmh) — that's the
// only reason this module depends on another one.
dependencies {
    implementation(project(":trees:binary-search-tree"))
}
