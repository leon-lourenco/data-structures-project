# The Grand Data Structures Project

[![CI](https://github.com/leon-lourenco/data-structures-project/actions/workflows/ci.yml/badge.svg)](https://github.com/leon-lourenco/data-structures-project/actions/workflows/ci.yml)

**Leia em:** [English](README.md) | [Português](README.pt-BR.md) | [Español](README.es.md)

Um projeto Java modular que demonstra as estruturas de dados clássicas ensinadas num curso
universitário de Ciência da Computação — um módulo Gradle por estrutura, cada um com seu
próprio README, uma implementação do zero, uma segunda implementação aplicando essa estrutura
a um cenário real, e um microbenchmark JMH que transforma a afirmação de complexidade (Big-O)
do livro-texto num número medido e reproduzível. Tudo é JVM puro: sem demo hospedada, sem
serviços externos, `./gradlew build` e pronto.

Este é um projeto de portfólio de [Leon Lourenço](https://github.com/leon-lourenco),
engenheiro backend sênior, construído em público em lotes escopados.

## Alguns números reais

Cada afirmação abaixo foi copiada literalmente de uma execução local real de JMH/JaCoCo — veja
o README de cada módulo para a tabela completa e como reproduzi-la.

- **[Hash Table](hashing/hash-table)**, com toda chave forçada a colidir no mesmo bucket:
  `get()` vai de 134 ns a 179.653 ns conforme a tabela cresce de 100 para 100.000 entradas. Com
  hashing uniforme, o custo fica travado em ~3,7 ns o tempo todo. Mesma tabela, mesmo código —
  a única variável é a qualidade do `hashCode()` da chave.
- **[AVL Tree](trees/avl-tree)** vs. **[Binary Search Tree](trees/binary-search-tree)**, na
  mesma sequência de inserção ordenada de 100 chaves: a altura da BST comum degenera para
  **100**; a AVL Tree se mantém em **7**.
- **[Union-Find](graphs/union-find)** com compressão de caminho + união por rank, contra a
  versão sem essas otimizações, sob a mesma entrada de pior caso: **~3.046x** mais rápido com
  10.000 elementos.

## Por que classic + applied + benchmark

Uma estrutura implementada do zero prova que você entende sua mecânica — redimensionamento,
encadeamento, rotações, travessia. Não prova que você sabe *quando* recorrer a ela em vez da
alternativa, e não prova que a afirmação de Big-O do livro-texto realmente se sustenta numa JVM
real. Por isso cada módulo carrega três coisas em vez de uma:

- **classic/** — a estrutura em si, feita à mão (sem depender do tipo equivalente do
  `java.util` como atalho), com testes que exercitam seus casos-limite reais (colisões, ordem
  de inserção degenerada, rebalanceamento, resize/rehash).
- **applied/** — a mesma estrutura resolvendo um cenário real, escolhido perguntando: qual é o
  problema real que essa estrutura resolve, e onde esse problema exato já apareceu? O
  mapeamento não é fintech-apenas por padrão — é deliberadamente puxado de onde quer que, na
  trajetória do autor (pagamentos, seguros, telecom, modernização de mainframe), o problema
  subjacente se encaixe melhor.
- **jmh/** — um microbenchmark JMH que mede a operação sobre a qual a afirmação de complexidade
  do módulo trata, normalmente como um A/B direto: O(1) vs O(n), caso médio vs pior caso,
  balanceado vs degenerado. Os números citados em cada README são copiados de uma execução
  local real, não estimados.

## As 16 estruturas

Todas completas: implementação classic/applied/benchmark, README próprio, e cobertura genuína
de 100% de instrução + branch no JaCoCo (não inflada pra bater o número — vários módulos
encontraram e corrigiram uma lacuna real, ou simplificaram um branch defensivo comprovadamente
inatingível, em vez disso).

| Estrutura | Categoria | Cenário aplicado |
|-----------|----------|-------------------|
| [Dynamic Array](linear/dynamic-array) | Linear | Buffer de ingestão em lote de seguros (seguradora) |
| [Linked List](linear/linked-list) | Linear | Estágios do fluxo de sinistros de seguros (seguradora) |
| [Stack](linear/stack) | Linear | Validador de colchetes de copybook COBOL legado (banco legado) |
| [Queue / Deque](linear/queue-deque) | Linear | Triagem de tickets de suporte com fast-track VIP (telecom) |
| [Skip List](linear/skip-list) | Linear | Índice de janela de rate-limiter |
| [Binary Search Tree](trees/binary-search-tree) | Trees | Consulta de faixa de limite PIX do BACEN |
| [AVL Tree](trees/avl-tree) | Trees | Índice de regras de fraude (plataforma antifraude) |
| [Heap / Priority Queue](trees/heap) | Trees | Fila de escalonamento por SLA (telecom) |
| [Trie](trees/trie) | Trees | Autocomplete de prefixo de chave PIX (BACEN) |
| [B-Tree](trees/b-tree) | Trees | Simulação de índice de banco de dados relacional (banco legado) |
| [Hash Table](hashing/hash-table) | Hashing | Cache de chave de idempotência PIX |
| [Bloom Filter](hashing/bloom-filter) | Hashing | Pré-checagem de lista de bloqueio/fraude (seguradora/plataforma antifraude) |
| [Graph (BFS/DFS)](graphs/graph-bfs-dfs) | Graphs | Travessia de rede para AML (equipe de fraude/compliance) |
| [Dijkstra](graphs/dijkstra) | Graphs | Roteamento de liquidação interbancária mais barato |
| [Union-Find](graphs/union-find) | Graphs | Detecção de clusters de fraude (plataforma antifraude) |
| [Minimum Spanning Tree](graphs/minimum-spanning-tree) | Graphs | Planejamento de backhaul de torres de celular (telecom) |

## Estrutura

Todo módulo de estrutura segue o mesmo esqueleto:

```
<category>/<structure>/
├── build.gradle.kts          # presente só quando o módulo precisa de dependências extras
├── README.md                 # problema, solução, complexidade, os dois exemplos, benchmark, cobertura
└── src/
    ├── main/java/com/datastructures/<category>/<structure>/
    │   ├── classic/           # a implementação do zero
    │   └── applied/           # o uso no cenário real
    ├── test/java/...          # espelha a divisão classic/applied
    └── jmh/java/com/datastructures/<category>/<structure>/benchmark/
        └── ...                # microbenchmark(s) JMH provando a afirmação de complexidade empiricamente
```

## Stack técnica

Java 26, Gradle 9.7 (Kotlin DSL, wrapper commitado — `./gradlew` funciona sem instalar o
Gradle), JUnit 5, AssertJ, JaCoCo 0.8.15, JMH 1.37. Sem Spring, sem framework — todo módulo é
Java puro, já que o ponto é a estrutura de dados, não um container.

**Nota sobre a integração do JMH:** o plugin Gradle da comunidade `me.champeau.jmh` teve seu
último lançamento (0.7.3, janeiro de 2025) testado só até Gradle 8.10/Java 21. Em vez de lutar
contra um plugin desatualizado usando Gradle 9.7/Java 26, o `src/jmh/java` de cada módulo é
conectado diretamente como um source set Gradle puro (veja o `build.gradle.kts` raiz), com o
próprio annotation processor do JMH gerando as classes de execução do benchmark — nenhum
plugin de terceiros no meio do caminho.

## Rodando o projeto

```bash
./gradlew build                                              # compila todos os módulos
./gradlew test                                                # roda os testes de todos os módulos
./gradlew :trees:binary-search-tree:jacocoTestReport          # relatório de cobertura por módulo (HTML)
./gradlew :trees:binary-search-tree:jmh                       # execução do benchmark JMH por módulo
```

Sem Docker, sem banco de dados, sem chamadas de rede — todo teste e benchmark roda contra
código em processo. Os números de cobertura e benchmark citados no README de cada módulo são
copiados de uma execução local real (JDK 26.0.2 nesta máquina), não estimados.

## Licença

MIT — veja [LICENSE](LICENSE).
