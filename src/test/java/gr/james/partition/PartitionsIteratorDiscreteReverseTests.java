package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Test for the {@link Partitions#reverseLexicographicEnumeration(Set, int[], BiFunction)} method, and consequently the
 * {@link PartitionsIteratorDiscreteReverse} class.
 */
public class PartitionsIteratorDiscreteReverseTests {
    /**
     * The number of possible partitions of 10 elements with k={2,6,9} should be exactly 23383.
     */
    @Test
    public void correctness1() {
        final Iterator<Partition<Integer>> it = Partitions.reverseLexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{9, 2, 6, 6, 9}, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        int count = 0;
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
            count++;
        }
        Assert.assertEquals(23383, partitions.size());
        Assert.assertEquals(23383, count);
    }

    /**
     * The number of possible partitions of 10 elements with k={1} should be exactly 1.
     */
    @Test
    public void correctness2() {
        final Iterator<Partition<Integer>> it = Partitions.reverseLexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{1}, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        int count = 0;
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
            count++;
        }
        Assert.assertEquals(1, partitions.size());
        Assert.assertEquals(1, count);
    }

    /**
     * The number of possible partitions of 10 elements with k={1,10} should be exactly 2.
     */
    @Test
    public void correctness3() {
        final Iterator<Partition<Integer>> it = Partitions.reverseLexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{10, 1}, ImmutablePartition::new);
        final Set<Partition<Integer>> partitions = new HashSet<>();
        int count = 0;
        while (it.hasNext()) {
            final Partition<Integer> p = it.next();
            Assert.assertEquals(10, p.size());
            partitions.add(p);
            count++;
        }
        Assert.assertEquals(2, partitions.size());
        Assert.assertEquals(2, count);
    }

    /**
     * Should be the same enumeration with the forward enumeration but reverse.
     */
    @Test
    public void correctnessReverse() {
        final Iterator<Partition<Integer>> it = Partitions.lexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{3, 5}, ImmutablePartition::new);
        final Iterator<Partition<Integer>> itr = Partitions.reverseLexicographicEnumeration(Helper.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), new int[]{3, 5}, ImmutablePartition::new);
        final List<Partition<Integer>> partitions = new ArrayList<>();
        final List<Partition<Integer>> partitionsReverse = new ArrayList<>();
        while (it.hasNext() && itr.hasNext()) {
            final Partition<Integer> p = it.next();
            final Partition<Integer> pr = itr.next();
            partitions.add(p);
            partitionsReverse.add(pr);
        }
        Assert.assertTrue(!it.hasNext() && !itr.hasNext());
        Collections.reverse(partitionsReverse);
        Assert.assertEquals(partitions, partitionsReverse);
    }

    /**
     * The method should throw {@link NullPointerException} if the first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput1() {
        final Set<Integer> s = null;
        Partitions.reverseLexicographicEnumeration(s, new int[]{1, 2}, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput2() {
        final Set<Integer> s = new HashSet<>();
        Partitions.reverseLexicographicEnumeration(s, null, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link NullPointerException} if the third input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput3() {
        final Set<Integer> s = new HashSet<>();
        Partitions.reverseLexicographicEnumeration(s, new int[]{1, 2}, null);
    }

    /**
     * The method should throw {@link NullPointerException} if any element in {@code elements} is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void nullInput4() {
        final Set<Integer> s = Helper.newHashSet(1, 2, null);
        Partitions.reverseLexicographicEnumeration(s, new int[]{1}, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if {@code elements} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptyOfSet() {
        final Set<Integer> s = new HashSet<>();
        Partitions.reverseLexicographicEnumeration(s, new int[]{1}, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if any k is < 1.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notPositiveK() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.reverseLexicographicEnumeration(s, new int[]{1, 0}, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }

    /**
     * The method should throw {@link IllegalArgumentException} if all k are less than the size of elements.
     */
    @Test(expected = IllegalArgumentException.class)
    public void kBiggerThanN() {
        final Set<Integer> s = new HashSet<>(Arrays.asList(1, 2, 3));
        Partitions.reverseLexicographicEnumeration(s, new int[]{2, 4}, (elements, mapping) -> new ImmutablePartition<>(new UnionFindPartition<>()));
    }
}
