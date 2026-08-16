// Kruskal's algorithm needs a cycle check ("are these two nodes already in the same
// component?") on every candidate edge. Rather than duplicate that logic, this module depends
// directly on this repo's own graphs:union-find module and reuses its optimized UnionFind
// (path compression + union by rank) exactly as a real codebase would reuse its own library.
dependencies {
    implementation(project(":graphs:union-find"))
}
