package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Test for the {@link Partitions#lexicographicEnumeration(Set, int, int, BiFunction)} method, and consequently
 * the {@link PartitionsIterator} class.
 */
public class PartitionsIteratorTests {
    /**
     * The number of possible partitions of 10 elements with kmin=4 and kmax=6 should be exactly 99457.
     */
    @Test
    public void correctness1() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 4, 6, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(99457, partitions.size());
    }

    /**
     * The number of possible partitions of 10 elements with kmin=5 and kmax=5 should be exactly 42525.
     */
    @Test
    public void correctness2() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 5, 5, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(42525, partitions.size());
    }

    /**
     * The number of possible partitions of 10 elements with kmin=1 and kmax=5 should be exactly 86472.
     */
    @Test
    public void correctness3() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 1, 5, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
        }
        Assert.assertEquals(86472, partitions.size());
    }

    /**
     * The method should throw {@link NullPointerException} if the first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput1() {
        final Set<Integer> s = null;
        Partitions.lexicographicEnumeration(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput2() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicEnumeration(s, 1, 1, null);
    }

    /**
     * The method should throw {@link NullPointerException} if any element in {@code elements} is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput3() {
        final Set<Integer> s = Helper.newHashSet(1, 2, null);
        Partitions.lexicographicEnumeration(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if {@code elements} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyOfSet() {
        final Set<Integer> s = new HashSet<>();
        Partitions.lexicographicEnumeration(s, 1, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if kmin < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveKmin() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicEnumeration(s, 0, 1, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if kmax < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveKmax() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicEnumeration(s, 0, 0, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} is kmax is greater than the size of elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kBiggerThanN() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.lexicographicEnumeration(s, 2, 4, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} is kmin is greater than kmax.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kMinGreaterThanKmax() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Partitions.lexicographicEnumeration(s, 4, 3, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }
}
