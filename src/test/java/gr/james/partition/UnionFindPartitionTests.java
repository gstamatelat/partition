package gr.james.partition;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Tests for the UnionFindPartition implementation.
 */
public class UnionFindPartitionTests {
    private static Supplier<UnionFindPartition<Integer>> unionFindVacantPartitionSupplier =
            UnionFindPartition::new;

    /**
     * Execute all methods to trigger the internal validate method.
     */
    @Test
    public void validate() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Random rng = new Random(1479651034875L);
        final int size = 1000;
        for (int i = 0; i < size; i++) {
            p.add(i);
        }
        for (int i = 0; i < 2 * size; i++) {
            p.size();
            p.subsetCount();
            p.elements();
            p.contains(2 * rng.nextInt(size));

            p.subsets();
            p.subsets().size();
            p.subsets().iterator();

            p.subset(rng.nextInt(size));
            p.subset(rng.nextInt(size)).size();
            p.subset(rng.nextInt(size)).iterator();

            p.connected(rng.nextInt(size), rng.nextInt(size));

            int x = rng.nextInt(size);
            p.remove(x);
            p.add(x);

            p.union(rng.nextInt(size), rng.nextInt(size));
            p.split(rng.nextInt(size));
        }
    }

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
            Partition<Integer> partitionCopy = new UnionFindPartition<>(p);
            Assert.assertEquals(p, partitionCopy);
            for (int i = 0; i < size; i++) {
                Assert.assertEquals(p.subset(i), partitionCopy.subset(i));
            }
            for (int i = 0; i < 2 * size; i++) {
                Assert.assertEquals(p.contains(i), partitionCopy.contains(i));
            }
            Assert.assertEquals(p.size(), partitionCopy.size());
            Assert.assertEquals(p.subsetCount(), partitionCopy.subsetCount());
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Assert.assertEquals(p.connected(i, j), partitionCopy.connected(i, j));
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
            Partition<Integer> partitionCopy = new UnionFindPartition<>(p.toString(), Integer::parseInt);
            Assert.assertEquals(p, partitionCopy);
        }
    }

    /**
     * Test for the simple partition [[1],[2,3],[4,5]].
     */
    @Test
    public void simple() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(1);
        p.add(2);
        p.add(3);
        p.add(4);
        p.add(5);
        p.union(2, 3);
        p.union(4, 5);
        Assert.assertEquals(
                Helper.newHashSet(
                        Helper.newHashSet(1),
                        Helper.newHashSet(2, 3),
                        Helper.newHashSet(4, 5)
                ),
                p.subsets()
        );
        Assert.assertEquals(38, p.hashCode());
    }

    /**
     * Add N elements, each on a separate subset.
     */
    @Test
    public void unconnected() {
        final UnionFindPartition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> set = new HashSet<>();
        final Set<Set<Integer>> subsets = new HashSet<>();
        final int size = 100;
        for (int i = 0; i < size; i++) {
            set.add(i);
            subsets.add(Collections.singleton(i));
        }
        for (int i = 0; i < size; i++) {
            boolean added = p.add(i);
            Assert.assertTrue(added);
        }
        for (int i = 0; i < size; i++) {
            boolean added = p.add(i);
            Assert.assertFalse(added);
        }

        Assert.assertEquals(size, p.size());
        Assert.assertEquals(size, p.subsetCount());
        Assert.assertEquals(set, p.elements());
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(p.contains(i));
        }
        for (int i = size; i < 2 * size; i++) {
            Assert.assertFalse(p.contains(i));
        }
        Assert.assertEquals(subsets, p.subsets());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(Collections.singleton(i), p.subset(i));
        }
        for (int i = 0; i < size; i++) {
            Assert.assertEquals((int) p.root(i), i);
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    Assert.assertTrue(p.connected(i, j));
                } else {
                    Assert.assertFalse(p.connected(i, j));
                }
            }
        }
    }

    /**
     * Add N elements, each on a separate subset using split.
     */
    @Test
    public void unconnectedSplit() {
        final UnionFindPartition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> set = new HashSet<>();
        final Set<Set<Integer>> subsets = new HashSet<>();
        final int size = 100;
        for (int i = 0; i < size; i++) {
            set.add(i);
            subsets.add(Collections.singleton(i));
        }
        p.addSubset(set);
        int split = 0;
        for (int i = 0; i < size; i++) {
            boolean splitted = p.split(i);
            if (!splitted) {
                split++;
            }
        }
        Assert.assertEquals(split, 1);

        Assert.assertEquals(size, p.size());
        Assert.assertEquals(size, p.subsetCount());
        Assert.assertEquals(set, p.elements());
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(p.contains(i));
        }
        for (int i = size; i < 2 * size; i++) {
            Assert.assertFalse(p.contains(i));
        }
        Assert.assertEquals(subsets, p.subsets());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(Collections.singleton(i), p.subset(i));
        }
        for (int i = 0; i < size; i++) {
            Assert.assertEquals((int) p.root(i), i);
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    Assert.assertTrue(p.connected(i, j));
                } else {
                    Assert.assertFalse(p.connected(i, j));
                }
            }
        }
    }

    /**
     * Add N elements, each pair on a different subset, using addSubset.
     */
    @Test
    public void halfUnconnectedAddSubset() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> set = new HashSet<>();
        final Set<Set<Integer>> subsets = new HashSet<>();
        final int size = 100;
        for (int i = 0; i < size; i++) {
            set.add(i);
        }
        for (int i = 0; i < size; i += 2) {
            subsets.add(Helper.newHashSet(i, i + 1));
        }
        for (int i = 0; i < size; i += 2) {
            p.addSubset(Helper.newHashSet(i, i + 1));
        }

        Assert.assertEquals(size, p.size());
        Assert.assertEquals(size / 2, p.subsetCount());
        Assert.assertEquals(set, p.elements());
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(p.contains(i));
        }
        for (int i = size; i < 2 * size; i++) {
            Assert.assertFalse(p.contains(i));
        }
        Assert.assertEquals(subsets, p.subsets());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(Helper.newHashSet(2 * (i / 2), 2 * (i / 2) + 1), p.subset(i));
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i == j) || (Math.abs(i - j) == 1 && Math.min(i, j) % 2 == 0)) {
                    Assert.assertTrue(p.connected(i, j));
                } else {
                    Assert.assertFalse(p.connected(i, j));
                }
            }
        }
    }

    /**
     * Add N elements, each pair on a different subset, using union.
     */
    @Test
    public void halfUnconnectedUnion() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> set = new HashSet<>();
        final Set<Set<Integer>> subsets = new HashSet<>();
        final int size = 100;
        for (int i = 0; i < size; i++) {
            set.add(i);
        }
        for (int i = 0; i < size; i += 2) {
            subsets.add(Helper.newHashSet(i, i + 1));
        }
        for (int i = 0; i < size; i += 2) {
            p.add(i);
            p.add(i + 1);
            p.union(i, i + 1);
        }

        Assert.assertEquals(size, p.size());
        Assert.assertEquals(size / 2, p.subsetCount());
        Assert.assertEquals(set, p.elements());
        for (int i = 0; i < size; i++) {
            Assert.assertTrue(p.contains(i));
        }
        for (int i = size; i < 2 * size; i++) {
            Assert.assertFalse(p.contains(i));
        }
        Assert.assertEquals(subsets, p.subsets());
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(Helper.newHashSet(2 * (i / 2), 2 * (i / 2) + 1), p.subset(i));
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((i == j) || (Math.abs(i - j) == 1 && Math.min(i, j) % 2 == 0)) {
                    Assert.assertTrue(p.connected(i, j));
                } else {
                    Assert.assertFalse(p.connected(i, j));
                }
            }
        }
    }

    /**
     * Create two partitions of a single subset, one using addSubset and the other using multiple unions, and check if
     * they are equal.
     */
    @Test
    public void unionAndSubsetEquals() {
        final Partition<Integer> p1 = unionFindVacantPartitionSupplier.get();
        final Partition<Integer> p2 = unionFindVacantPartitionSupplier.get();
        final int size = 100;
        final Set<Integer> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            p1.add(i);
            set.add(i);
        }
        for (int i = 0; i < size - 1; i++) {
            p1.union(i, i + 1);
        }
        p2.addSubset(set);
        Assert.assertEquals(p1, p2);
    }

    /**
     * The Partition.equals method must return false if other is not a Partition.
     */
    @Test
    public void equalsOnDifferentType() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        Assert.assertNotEquals(p, new Object());
    }

    /**
     * An empty partition's iterator must be empty.
     */
    @Test
    public void emptySubsets() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.remove(0);
        Assert.assertFalse(p.subsets().iterator().hasNext());
    }

    /**
     * The equals method must return true for inputs that point to the same reference.
     */
    @Test
    public void equalsReference() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        Assert.assertEquals(p, p);
    }

    /**
     * The subsets() contains method must return false if the input is not a Set or if it is empty or if any element is
     * not in the partition.
     */
    @Test
    public void subsetsContainsFalse() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        Assert.assertFalse(p.subsets().contains(""));
        Assert.assertFalse(p.subsets().contains(new HashSet<>()));
        Assert.assertFalse(p.subsets().contains(Helper.newLinkedHashSet(1, 0)));
        Assert.assertFalse(p.subsets().contains(Helper.newLinkedHashSet(0, 1)));
        p.add(1);
        p.add(2);
        p.union(0, 1);
        Assert.assertFalse(p.subsets().contains(Helper.newLinkedHashSet(0, 2)));
    }

    /**
     * The subset() contains method must return false if the input is not in the partition.
     */
    @Test
    public void subsetContainsFalse() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        Assert.assertFalse(p.subset(0).contains(1));
        Assert.assertFalse(p.subset(0).contains(""));
    }

    /**
     * The remove method must return false is the input is not in the partition.
     */
    @Test
    public void removeMissing() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        Assert.assertFalse(p.remove(0));
    }

    /**
     * The move method must return false if the inputs are in the same subset.
     */
    @Test
    public void moveFalse() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.add(1);
        p.union(0, 1);
        Assert.assertFalse(p.move(0, 1));
    }

    /**
     * The move method is equivalent to union.
     */
    @Test
    public void moveEquivalent() {
        final Partition<Integer> p1 = unionFindVacantPartitionSupplier.get();
        final Partition<Integer> p2 = unionFindVacantPartitionSupplier.get();
        p1.add(0);
        p1.add(1);
        p2.add(0);
        p2.add(1);
        p1.union(0, 1);
        p2.move(0, 1);
        Assert.assertEquals(p1, p2);
    }

    /**
     * The clear method resets to an empty partition.
     */
    @Test
    public void clear() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.add(1);
        p.union(0, 1);
        p.clear();
        Assert.assertEquals(p, unionFindVacantPartitionSupplier.get());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException1() {
        new UnionFindPartition<>("[[1,2],[]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException2() {
        new UnionFindPartition<>("[[1,2]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException3() {
        new UnionFindPartition<>("[[1,2],[3", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException4() {
        new UnionFindPartition<>("[[1,2],[]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException5() {
        new UnionFindPartition<>("[[1,2],[3,,4]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException6() {
        new UnionFindPartition<>("[[1,2],[,]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException7() {
        new UnionFindPartition<>("[[1,2],[3,3]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException8() {
        new UnionFindPartition<>("[[1,2],[3,4]|[5]]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException9() {
        new UnionFindPartition<>("[[1,2],[3,4],]", Function.identity());
    }

    /**
     * Various string constructor fail arguments.
     */
    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorIllegalArgumentException10() {
        new UnionFindPartition<>("[,[1,2],[3,4]]", Function.identity());
    }

    /**
     * The subset() contains method must throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void subsetContainsNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.subset(0).contains(null);
    }

    /**
     * The subsets() contains method must throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void subsetsContainsNullPointerException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.subsets().contains(null);
    }

    /**
     * The subsets() contains method must throw NullPointerException if any element in the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void subsetsContainsNullPointerException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.subsets().contains(Helper.newHashSet(null, 0));
    }

    /**
     * The subsets() iterator must return NoSuchElementException after iteration
     */
    @Test(expected = NoSuchElementException.class)
    public void subsetsNoSuchElement() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        Iterator<Set<Integer>> subsets = p.subsets().iterator();
        while (subsets.hasNext()) {
            subsets.next();
        }
        subsets.next();
    }

    /**
     * The subset() iterator must return NoSuchElementException after iteration
     */
    @Test(expected = NoSuchElementException.class)
    public void subsetNoSuchElement() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.add(1);
        p.union(0, 1);
        Iterator<Integer> subset = p.subset(0).iterator();
        while (subset.hasNext()) {
            subset.next();
        }
        subset.next();
    }

    /**
     * The contains method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void containsNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.contains(null);
    }

    /**
     * The subset method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void subsetNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.subset(null);
    }

    /**
     * The subset method should throw IllegalArgumentException if the input is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void subsetIllegalArgumentException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.subset(0);
    }

    /**
     * The connected method should throw NullPointerException if the input 1 is null.
     */
    @Test(expected = NullPointerException.class)
    public void connectedNullPointerException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.connected(null, 0);
    }

    /**
     * The connected method should throw NullPointerException if the input 2 is null.
     */
    @Test(expected = NullPointerException.class)
    public void connectedNullPointerException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.connected(0, null);
    }

    /**
     * The connected method should throw IllegalArgumentException if the input 1 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void connectedIllegalArgumentException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.connected(1, 0);
    }

    /**
     * The connected method should throw IllegalArgumentException if the input 2 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void connectedIllegalArgumentException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.connected(0, 1);
    }

    /**
     * The add method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void addNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(null);
    }

    /**
     * The remove method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void removeNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.remove(null);
    }

    /**
     * The addSubset method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void addSubsetNullPointerException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.addSubset(null);
    }

    /**
     * The addSubset method should throw NullPointerException if any element in the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void addSubsetNullPointerException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> subset = new LinkedHashSet<>();
        subset.add(0);
        subset.add(null);
        p.addSubset(subset);
    }

    /**
     * The addSubset method should throw NullPointerException if any element in the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void addSubsetNullPointerException3() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> subset = new LinkedHashSet<>();
        subset.add(null);
        subset.add(0);
        p.addSubset(subset);
    }

    /**
     * The addSubset method should throw IllegalArgumentException if any element in the input is already in the
     * partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addSubsetIllegalArgumentException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        final Set<Integer> subset = new LinkedHashSet<>();
        subset.add(0);
        subset.add(1);
        p.addSubset(subset);
    }

    /**
     * The addSubset method should throw IllegalArgumentException if any element in the input is already in the
     * partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addSubsetIllegalArgumentException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        final Set<Integer> subset = new LinkedHashSet<>();
        subset.add(1);
        subset.add(0);
        p.addSubset(subset);
    }

    /**
     * The addSubset method should throw IllegalArgumentException if the input is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addSubsetIllegalArgumentException3() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        final Set<Integer> subset = new HashSet<>();
        p.addSubset(subset);
    }

    /**
     * The union method should throw NullPointerException if the input 1 is null.
     */
    @Test(expected = NullPointerException.class)
    public void unionNullPointerException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.union(null, 0);
    }

    /**
     * The union method should throw NullPointerException if the input 2 is null.
     */
    @Test(expected = NullPointerException.class)
    public void unionNullPointerException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.union(0, null);
    }

    /**
     * The union method should throw IllegalArgumentException if the input 1 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void unionIllegalArgumentException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.union(1, 0);
    }

    /**
     * The union method should throw IllegalArgumentException if the input 2 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void unionIllegalArgumentException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.union(0, 1);
    }

    /**
     * The split method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void splitNullPointerException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.split(null);
    }

    /**
     * The split method should throw IllegalArgumentException if the input is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void splitIllegalArgumentException() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.split(1);
    }

    /**
     * The move method should throw NullPointerException if the input 1 is null.
     */
    @Test(expected = NullPointerException.class)
    public void moveNullPointerException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.move(null, 0);
    }

    /**
     * The move method should throw NullPointerException if the input 2 is null.
     */
    @Test(expected = NullPointerException.class)
    public void moveNullPointerException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.move(0, null);
    }

    /**
     * The move method should throw IllegalArgumentException if the input 1 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void moveIllegalArgumentException1() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.move(1, 0);
    }

    /**
     * The move method should throw IllegalArgumentException if the input 2 is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void moveIllegalArgumentException2() {
        final Partition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.move(0, 1);
    }

    /**
     * The root method should throw NullPointerException if the input is null.
     */
    @Test(expected = NullPointerException.class)
    public void rootNullPointerException() {
        final UnionFindPartition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.root(null);
    }

    /**
     * The root method should throw IllegalArgumentException if the input is not contained in the partition.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rootIllegalArgumentException() {
        final UnionFindPartition<Integer> p = unionFindVacantPartitionSupplier.get();
        p.add(0);
        p.root(1);
    }
}
