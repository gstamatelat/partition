package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Tests for the ImmutablePartition implementation.
 */
public class ImmutablePartitionTests {
    /**
     * Copy an existing partition.
     */
    @Test
    public void copy() {
        final Random rng = new Random(82465L);
        final int size = 100;
        for (int reps = 500; reps > 0; reps--) {
            final Partition<Integer> p = new UnionFindPartition<>();
            for (int i = 0; i < size; i++) {
                p.add(i);
            }
            for (int i = 0; i < size / 2; i++) {
                p.union(rng.nextInt(size), rng.nextInt(size));
            }
            ImmutablePartition<Integer> immutablePartition = new ImmutablePartition<>(p);
            Assert.assertEquals(p, immutablePartition);
            for (int i = 0; i < size; i++) {
                Assert.assertEquals(p.subset(i), immutablePartition.subset(i));
            }
            for (int i = 0; i < 2 * size; i++) {
                Assert.assertEquals(p.contains(i), immutablePartition.contains(i));
            }
            Assert.assertEquals(p.size(), immutablePartition.size());
            Assert.assertEquals(p.subsetCount(), immutablePartition.subsetCount());
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Assert.assertEquals(p.connected(i, j), immutablePartition.connected(i, j));
                }
            }
        }
    }

    /**
     * The toString method must be the inverse of the string constructor
     */
    @Test
    public void stringConstructor() {
        final Random rng = new Random(82465L);
        final int size = 100;
        for (int reps = 500; reps > 0; reps--) {
            final Partition<Integer> p = new UnionFindPartition<>();
            for (int i = 0; i < size; i++) {
                p.add(i);
            }
            for (int i = 0; i < size / 2; i++) {
                p.union(rng.nextInt(size), rng.nextInt(size));
            }
            Partition<Integer> partitionCopy = new ImmutablePartition<>(p);
            Assert.assertEquals(partitionCopy, new ImmutablePartition<>(partitionCopy.toString(), Integer::parseInt));
        }
    }

    /**
     * Test the {@link Map} contructor.
     */
    @Test
    public void mapConstructor() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = new Object();
        m.put(1, gr1);
        m.put(2, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p = new ImmutablePartition<>(m);
        Assert.assertEquals(3, p.subsetCount());
        Assert.assertTrue(p.connected(1, 3));
        Assert.assertTrue(p.connected(2, 5));
    }

    /**
     * Test the implicit map contructor.
     */
    @Test
    public void implicitMapConstructor() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = new Object();
        m.put(1, gr1);
        m.put(2, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p1 = new ImmutablePartition<>(m.keySet(), m::get);
        final Partition<Integer> p2 = new ImmutablePartition<>(m);
        Assert.assertEquals(p1, p2);
    }

    /**
     * Implicit map constructor throws {@link NullPointerException} if first input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void implicitMapConstructorContainsNull1() {
        final Set<Integer> elements = null;
        final Partition<Integer> p = new ImmutablePartition<>(elements, i -> i);
    }

    /**
     * Implicit map constructor throws {@link NullPointerException} if second input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void implicitMapConstructorContainsNull2() {
        final Set<Integer> elements = new HashSet<>();
        final Partition<Integer> p = new ImmutablePartition<>(elements, null);
    }

    /**
     * Implicit map constructor throws {@link NullPointerException} if any key is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void implicitMapConstructorContainsNullKey() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = new Object();
        m.put(1, gr1);
        m.put(null, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p = new ImmutablePartition<>(m.keySet(), m::get);
        Assert.assertEquals(3, p.subsetCount());
        Assert.assertTrue(p.connected(1, 3));
        Assert.assertTrue(p.connected(2, 5));
    }

    /**
     * Implicit map constructor throws {@link NullPointerException} if any value is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void implicitMapConstructorContainsNullValue() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = null;
        m.put(1, gr1);
        m.put(2, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p = new ImmutablePartition<>(m.keySet(), m::get);
        Assert.assertEquals(3, p.subsetCount());
        Assert.assertTrue(p.connected(1, 3));
        Assert.assertTrue(p.connected(2, 5));
    }

    /**
     * Map constructor throws {@link NullPointerException} if input is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void mapConstructorContainsNull() {
        final Map<Integer, Object> m = null;
        final Partition<Integer> p = new ImmutablePartition<>(m);
    }

    /**
     * Map constructor throws {@link NullPointerException} if any key is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void mapConstructorContainsNullKey() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = new Object();
        m.put(1, gr1);
        m.put(null, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p = new ImmutablePartition<>(m);
        Assert.assertEquals(3, p.subsetCount());
        Assert.assertTrue(p.connected(1, 3));
        Assert.assertTrue(p.connected(2, 5));
    }

    /**
     * Map constructor throws {@link NullPointerException} if any value is {@code null}.
     */
    @Test(expected = NullPointerException.class)
    public void mapConstructorContainsNullValue() {
        final Map<Integer, Object> m = new HashMap<>();
        final Object gr1 = new Object();
        final Object gr2 = new Object();
        final Object gr3 = null;
        m.put(1, gr1);
        m.put(2, gr2);
        m.put(3, gr1);
        m.put(4, gr3);
        m.put(5, gr2);
        final Partition<Integer> p = new ImmutablePartition<>(m);
        Assert.assertEquals(3, p.subsetCount());
        Assert.assertTrue(p.connected(1, 3));
        Assert.assertTrue(p.connected(2, 5));
    }

    /**
     * The contains method must throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void containsNullPointerException() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.contains(null);
    }

    /**
     * The subset method must throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void subsetNullPointerException() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.subset(null);
    }

    /**
     * The subset method must throw IllegalArgumentException if the input is not in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void subsetIllegalArgumentException() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.subset(0);
    }

    /**
     * The connected method must throw NullPointerException if input 1 is null.
     */
    @Test(expected = NullPointerException.class)
    public void connectedNullPointerException1() {
        final UnionFindPartition<Integer> p = new UnionFindPartition<>();
        p.add(0);
        p.add(1);
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(p);
        immutablePartition.connected(null, 0);
    }

    /**
     * The connected method must throw NullPointerException if input 2 is null.
     */
    @Test(expected = NullPointerException.class)
    public void connectedNullPointerException2() {
        final UnionFindPartition<Integer> p = new UnionFindPartition<>();
        p.add(0);
        p.add(1);
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(p);
        immutablePartition.connected(0, null);
    }

    /**
     * The connected method must throw IllegalArgumentException if input 1 is not in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void connectedIllegalArgumentException1() {
        final UnionFindPartition<Integer> p = new UnionFindPartition<>();
        p.add(0);
        p.add(1);
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(p);
        immutablePartition.connected(2, 0);
    }

    /**
     * The connected method must throw IllegalArgumentException if input 2 is not in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void connectedIllegalArgumentException2() {
        final UnionFindPartition<Integer> p = new UnionFindPartition<>();
        p.add(0);
        p.add(1);
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(p);
        immutablePartition.connected(0, 2);
    }

    /**
     * The add method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.add(0);
    }

    /**
     * The addAndUnion method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addAndUnionUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.addAndUnion(0, 0);
    }

    /**
     * The addSubset method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void addSubsetUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.addSubset(Helper.newHashSet(0));
    }

    /**
     * The removeSubset method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void removeSubsetUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.removeSubset(0);
    }

    /**
     * The remove method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void removeUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.remove(0);
    }

    /**
     * The clear method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void clearUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.clear();
    }

    /**
     * The union method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void unionUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.union(0, 1);
    }

    /**
     * The split method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void splitUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.split(0);
    }

    /**
     * The move method must throw UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void moveUnsupported() {
        final Partition<Integer> immutablePartition = new ImmutablePartition<>(new UnionFindPartition<>());
        immutablePartition.move(0, 1);
    }
}
