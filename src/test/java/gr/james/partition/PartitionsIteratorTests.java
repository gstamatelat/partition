package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Test for the {@link Partitions#lexicographicPartitions(Set, BiFunction)} method, and consequently the
 * {@link PartitionsIterator} class.
 */
public class PartitionsIteratorTests {
    /**
     * The number of possible partitions of 8 elements should be exactly 4140.
     */
    @Test
    public void correctness() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicPartitions(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8), ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(8, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(4140, partitions.size());
    }

    /**
     * The method should throw {@link NullPointerException} if the first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput1() {
        final Set<Integer> s = null;
        Partitions.lexicographicPartitions(s, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput2() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitions(s, null);
    }

    /**
     * The method should throw {@link NullPointerException} if any element in {@code elements} is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput3() {
        final Set<Integer> s = Helper.newHashSet(1, 2, null);
        Partitions.lexicographicPartitions(s, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if {@code elements} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyOfSet() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitions(s, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }
}
