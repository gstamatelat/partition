package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Test for the {@link Partitions#lexicographicPartitionsK(Set, int, BiFunction)} method, and consequently the
 * {@link PartitionsIteratorK} class.
 */
public class PartitionsIteratorKTests {
    /**
     * The number of possible partitions of 10 elements with k=6 should be exactly 22827.
     */
    @Test
    public void correctness() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicPartitionsK(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 6, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(22827, partitions.size());
    }

    /**
     * The method should throw {@link NullPointerException} if the first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput1() {
        final Set<Integer> s = null;
        Partitions.lexicographicPartitionsK(s, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput2() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitionsK(s, 1, null);
    }

    /**
     * The method should throw {@link NullPointerException} if any element in {@code elements} is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput3() {
        final Set<Integer> s = Helper.newHashSet(1, 2, null);
        Partitions.lexicographicPartitionsK(s, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if {@code elements} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyOfSet() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicPartitionsK(s, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if k < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveK() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicPartitionsK(s, 0, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} is k is greater than the size of elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kBiggerThanN() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicPartitionsK(s, 4, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }
}
