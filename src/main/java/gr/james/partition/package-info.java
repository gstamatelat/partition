/**
 * The package containing the utilities for working with partitions.
 * <p>
 * In mathematical terms, according to the book <i>Naive Set Theory (Halmos)</i>, a partition of a set {@code U} is a
 * set of nonempty subsets of {@code U} such that every element {@code u} in {@code U} is in exactly one of these
 * subsets.
 * <p>
 * This package provides the {@link gr.james.partition.Partition} interface which complies to this mathematical concept,
 * as well as two implementations with different characteristics. The {@link gr.james.partition.UnionFindPartition}
 * implementation is a Union-Find-Delete data structure with operations bounded by the inverse Ackermann function. The
 * {@link gr.james.partition.ImmutablePartition} class is an immutable implementation with constant time access to all
 * the supported methods.
 * <p>
 * The {@link gr.james.partition.Partitions} class contains auxiliary, static methods for partitions.
 */
package gr.james.partition;
