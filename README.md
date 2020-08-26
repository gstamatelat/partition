# Partition

In mathematical terms, according to the book *Naive Set Theory (Halmos)*, a
partition of a set `U` is a set of nonempty subsets of `U` such that every
element `u` in `U` is in exactly one of these subsets.

This package provides the `Partition` interface which complies to this
mathematical concept, as well as two implementations with different
characteristics. The `UnionFindPartition` implementation is a Union-Find-Delete
data structure with operations bounded by the inverse Ackermann function. The
`ImmutablePartition` class is an immutable implementation with constant time
access to all the supported methods.

## Using

Partition is published to
[jcenter](https://bintray.com/gstamatelat/partition/partition). You
can add a dependency from your project as follows:

Using Maven

```xml
<dependency>
  <groupId>gr.james</groupId>
  <artifactId>partition</artifactId>
  <version>0.4</version>
</dependency>
```

Using Gradle

```
compile 'gr.james:partition:0.4'
```

## Examples

Typical usage for a set of integers.

```java
Partition<Integer> p = new UnionFindPartition<>();
IntStream.range(0, 10).forEach(p::add);
p.union(0, 1);
p.union(1, 2);
System.out.println(p);
p.union(3, 4);
p.union(4, 5);
p.union(5, 6);
p.union(6, 7);
System.out.println(p);
p.union(8, 9);
System.out.println(p);
p.merge(10, 2);
System.out.println(p);
p.addSubset(new HashSet<>(Arrays.asList(11, 12)));
System.out.println(p);
```

Import from string.

```java
Partition<Integer> p = new UnionFindPartition<>("[[1,2][3]]", Integer::parseInt);
System.out.println(p);
```

Immutable partition (`UnsupportedOperationException`).

```java
Partition<Integer> p = new ImmutablePartition<>("[[1,2][3]]", Integer::parseInt);
System.out.println(p);
p.union(1, 3);
```

Enumerate all possible partitions of 3 elements.

```java
Partition<Integer> p = new UnionFindPartition<>();
IntStream.range(0, 3).forEach(p::add);
Iterator<Partition<Integer>> it = Partitions.lexicographicPartitions(
    p.elements(), UnionFindPartition::new);
while (it.hasNext()) {
    System.out.println(it.next());
}
```

Enumerate all possible partitions of 3 elements with at most 2 subsets.

```java
Partition<Integer> p = new UnionFindPartition<>();
IntStream.range(0, 3).forEach(p::add);
Iterator<Partition<Integer>> it = Partitions.lexicographicPartitionsAtMostK(
    p.elements(), UnionFindPartition::new);
while (it.hasNext()) {
    System.out.println(it.next());
}
```

Enumerate all possible partitions of 3 elements with exactly 2 subsets.

```java
Partition<Integer> p = new UnionFindPartition<>();
IntStream.range(0, 3).forEach(p::add);
Iterator<Partition<Integer>> it = Partitions.lexicographicPartitionsK(
    p.elements(), UnionFindPartition::new);
while (it.hasNext()) {
    System.out.println(it.next());
}
```
