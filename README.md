# The Grand Data Structures Project

[![CI](https://github.com/leon-lourenco/data-structures-project/actions/workflows/ci.yml/badge.svg)](https://github.com/leon-lourenco/data-structures-project/actions/workflows/ci.yml)

**Docs site:** [leon-lourenco.github.io/data-structures-project](https://leon-lourenco.github.io/data-structures-project/) — every structure with a diagram, both examples, and its coverage report, browsable in English/Português/Español.

**Read this in:** [English](README.md) | [Português](README.pt-BR.md) | [Español](README.es.md)

A modular Java project demonstrating the classic data structures taught in a university CS
curriculum — one Gradle module per structure, each with its own README, a from-scratch
implementation, a second implementation applying that structure to a real scenario, and a JMH
microbenchmark that turns the textbook Big-O claim into a measured, reproducible number.
Everything is plain JVM: no hosted demo, no external services, `./gradlew build` and you're
done.

This is a portfolio project by [Leon Lourenço](https://github.com/leon-lourenco), a senior
backend engineer, built in public in scoped batches.

## A few real numbers

Every claim below is copied verbatim from an actual local JMH/JaCoCo run — see each module's
own README for the full table and how to reproduce it.

- **[Hash Table](hashing/hash-table)**, every key forced into one bucket: `get()` goes from
  134 ns to 179,653 ns as the table grows from 100 to 100,000 entries. Uniform hashing stays
  flat at ~3.7 ns the whole time. Same table, same code — the only variable is the quality of
  the key's `hashCode()`.
- **[AVL Tree](trees/avl-tree)** vs. **[Binary Search Tree](trees/binary-search-tree)**, the
  identical 100-key sorted-insert sequence: the plain BST's height degenerates to **100**; the
  AVL tree stays at **7**.
- **[Union-Find](graphs/union-find)** with path compression + union by rank, vs. without, under
  the identical worst-case input: **~3,046x** faster at 10,000 elements.
- **[Matrix](linear/matrix)**, summing every cell of the same square matrix: row-major traversal
  (matches how the backing array is actually laid out) vs. column-major (identical element
  count, jumps `dimension` slots per step) — the gap widens from ~1.8x at 10K cells to **~15.7x**
  at 1M cells as the working set outgrows cache.

## Why classic + applied + benchmark

A structure implemented from scratch proves you understand its mechanics — resizing, chaining,
rotations, traversal. It doesn't prove you know *when* to reach for it over the alternative, and
it doesn't prove the textbook Big-O claim actually holds in a real JVM. So every module carries
three things instead of one:

- **classic/** — the structure itself, hand-rolled (no relying on the equivalent
  `java.util` type as a shortcut), with tests that exercise its real edge cases (collisions,
  degenerate insertion order, rebalancing, resize/rehash).
- **applied/** — the same structure solving a real scenario, chosen by asking: what's the
  actual problem this structure solves, and where has that exact problem shown up? The mapping
  isn't fintech-only by default — it's deliberately pulled from wherever in the author's
  background (payments, insurance, telecom, mainframe modernization) the underlying problem is
  the most natural fit.
- **jmh/** — a JMH microbenchmark that measures the operation the module's complexity claim is
  about, usually as a direct A/B: O(1) vs O(n), average case vs. worst case, balanced vs.
  degenerate. The numbers quoted in each README are copied from a real local run, not estimated.

## Why Java?

Every module here is written in Java on purpose, not by default — it's the language this
project's author ships in production daily, so writing these structures without a `java.util`
shortcut is also a fluency demonstration, not just a data-structures one. That constraint is
part of why Java specifically fits: the language *ships* `HashMap`, `PriorityQueue`,
`ArrayDeque`, and `ConcurrentSkipListMap` as one-line imports, so deliberately writing around
them is a real exercise. A language without that temptation built in — C, say — wouldn't pose
the same choice, and reimplementing a hash table in C mostly exercises manual memory management
(malloc/free, buffer sizing) instead of the actual point: chaining, load factor, resize timing.

The other reason is tooling maturity. Every benchmark number in this repo is measured, not
estimated: JMH runs each benchmark through warmup iterations so the JIT has actually compiled
the hot path before anything gets timed, forks a fresh JVM per benchmark to avoid
cross-contamination, and uses blackholes to stop the JIT from optimizing away the very code
being measured. JaCoCo brings the same rigor to coverage — 100% here means every instruction and
branch genuinely ran under test. Building that level of methodological rigor from scratch in C
is its own separate project; on the JVM it's `./gradlew jmh`.

The honest tradeoff: JVM numbers include the JVM. JIT warm-up, garbage collection, and object
header overhead are folded into every nanosecond quoted in this repo — a C implementation of the
same structures would show a closer-to-the-metal view of cache locality and memory layout
instead. This repo doesn't pretend that layer is invisible; it leans on JMH's methodology
specifically to see the algorithmic shape (O(1) vs. O(log n) vs. O(n)) *through* the JVM rather
than around it.

## The 17 structures

All complete: classic/applied/benchmark implementation, its own README, and genuine 100%
JaCoCo instruction + branch coverage (not padded to hit the number — several modules found and
either fixed a real gap or simplified away a provably-unreachable defensive branch instead).

| Structure | Category | Applied scenario |
|-----------|----------|-------------------|
| [Matrix](linear/matrix) | Linear | Insurance premium-rating grid (insurer) |
| [Dynamic Array](linear/dynamic-array) | Linear | Insurance batch-ingestion buffer (insurer) |
| [Linked List](linear/linked-list) | Linear | Insurance claim workflow stages (insurer) |
| [Stack](linear/stack) | Linear | COBOL copybook bracket validator (legacy bank) |
| [Queue / Deque](linear/queue-deque) | Linear | Support ticket triage w/ VIP fast-track (telecom) |
| [Skip List](linear/skip-list) | Linear | Rate-limiter window index |
| [Binary Search Tree](trees/binary-search-tree) | Trees | BACEN PIX limit-tier lookup |
| [AVL Tree](trees/avl-tree) | Trees | Fraud-rule index (fraud platform) |
| [Heap / Priority Queue](trees/heap) | Trees | SLA escalation queue (telecom) |
| [Trie](trees/trie) | Trees | PIX-key prefix autocomplete (BACEN) |
| [B-Tree](trees/b-tree) | Trees | RDBMS index simulation (legacy bank) |
| [Hash Table](hashing/hash-table) | Hashing | PIX idempotency-key cache |
| [Bloom Filter](hashing/bloom-filter) | Hashing | Fraud/blocklist pre-check (insurer/fraud platform) |
| [Graph (BFS/DFS)](graphs/graph-bfs-dfs) | Graphs | AML network traversal (fraud/compliance team) |
| [Dijkstra](graphs/dijkstra) | Graphs | Cheapest interbank settlement routing |
| [Union-Find](graphs/union-find) | Graphs | Fraud-ring cluster detection (fraud platform) |
| [Minimum Spanning Tree](graphs/minimum-spanning-tree) | Graphs | Cell-tower backhaul planning (telecom) |

## Structure

Every structure module follows the same skeleton:

```
<category>/<structure>/
├── build.gradle.kts          # only present when the module needs extra dependencies
├── README.md                 # problem, solution, complexity, both examples, benchmark, coverage
└── src/
    ├── main/java/com/datastructures/<category>/<structure>/
    │   ├── classic/           # the from-scratch implementation
    │   └── applied/           # the real-scenario usage
    ├── test/java/...          # mirrors the classic/applied split
    └── jmh/java/com/datastructures/<category>/<structure>/benchmark/
        └── ...                # JMH microbenchmark(s) proving the complexity claim empirically
```

## Tech stack

Java 26, Gradle 9.7 (Kotlin DSL, wrapper committed — `./gradlew` works without installing
Gradle), JUnit 5, AssertJ, JaCoCo 0.8.15, JMH 1.37. No Spring, no framework — every module is
plain Java, since the point is the data structure, not a container.

**JMH wiring note:** the community `me.champeau.jmh` Gradle plugin's last release (0.7.3,
January 2025) is only tested up to Gradle 8.10/Java 21. Rather than fight a stale plugin
against Gradle 9.7/Java 26, each module's `src/jmh/java` is wired directly as a plain Gradle
source set (see the root `build.gradle.kts`) with JMH's own annotation processor generating the
benchmark runner classes — no third-party plugin in the loop.

## Running it

```bash
./gradlew build                                              # compiles every module
./gradlew test                                                # runs every module's tests
./gradlew :trees:binary-search-tree:jacocoTestReport          # per-module coverage report (HTML)
./gradlew :trees:binary-search-tree:jmh                       # per-module JMH benchmark run
```

No Docker, no database, no network calls — every test and benchmark runs against in-process
code. Coverage and benchmark numbers quoted in each module's README are copied from a real
local run (JDK 26.0.2 on this machine), not estimated.

## License

MIT — see [LICENSE](LICENSE).
