package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Test for the {@link Partitions#lexicographicPartitionsBetweenK(Set, int, int, BiFunction)} method, and consequently
 * the {@link PartitionsIterator} class.
 */
public class PartitionsIteratorBetweenKTests {
    /**
     * The number of possible partitions of 10 elements with kmin=4 and kmax=6 should be exactly 99457.
     */
    @Test
    public void correctness() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicPartitionsBetweenK(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 4, 6, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(99457, partitions.size());
    }

    /**
     * The method should throw {@link NullPointerException} if the first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput1() {
        final Set<Integer> s = null;
        Partitions.lexicographicPartitionsBetweenK(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput2() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitionsBetweenK(s, 1, 1, null);
    }

    /**
     * The method should throw {@link NullPointerException} if any element in {@code elements} is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput3() {
        final Set<Integer> s = Helper.newHashSet(1, 2, null);
        Partitions.lexicographicPartitionsBetweenK(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if {@code elements} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyOfSet() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitionsBetweenK(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if kmin < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveKmin() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicPartitionsBetweenK(s, 0, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if kmax < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveKmax() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicPartitionsBetweenK(s, 0, 0, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} is kmax is greater than the size of elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kBiggerThanN() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicPartitionsBetweenK(s, 2, 4, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} is kmin is greater than kmax.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kMinGreaterThanKmax() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Partitions.lexicographicPartitionsBetweenK(s, 4, 3, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }
}
