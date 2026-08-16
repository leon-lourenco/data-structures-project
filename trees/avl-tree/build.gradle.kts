// Only this module needs a project dependency: the benchmark directly contrasts AvlTree against
// this repo's already-built plain BinarySearchTree (see src/jmh and the classic AvlTreeTest),
// reusing it rather than reimplementing a second unbalanced BST just for comparison purposes.
// Everything else (java/jacoco/jmh wiring) is already applied globally by the root
// subprojects {} block.
dependencies {
    "implementation"(project(":trees:binary-search-tree"))
}
