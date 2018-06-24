# Partition

In mathematical terms, according to the book *Naive Set Theory (Halmos)*, a
partition of a set `U` is a set of nonempty subsets of `U` such that every
element `u` in `U` is in exactly one of these subsets.

This package provides the `Partition` interface which complies to this
mathematical concept, as well as two implementations with different
characteristics. The `UnionFindPartition` implementation is a union-find-delete
data structure with operations bounded by the inverse Ackermann function. The
`ImmutablePartition` class is an immutable implementation with constant time
access to all the supported methods.
