package gr.james.partition;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Auxiliary, static methods for partitions.
 */
public final class Partitions {
    private Partitions() {
    }

    /**
     * Returns an iterator that iterates through all possible partitions of a set with the number of subsets between
     * {@code kmin} and {@code kmax} in lexicographic order.
     * <p>
     * Specifically, the iterator will enumerate all possible partitions that can exist from the set {@code elements}
     * and contain at minimum {@code kmin} subsets and at maximum {@code kmax} subsets. The {@code factory} function
     * dictates how the new {@link Partition} will be constructed and is invoked for every call of the
     * {@link Iterator#next()} method. Each {@code next()} call will return a new {@link Partition}. Changes to the
     * {@code elements} set will not reflect to the returned iterator after this method has been invoked.
     * <p>
     * The algorithm used in this method is inspired by <a href="https://stackoverflow.com/a/30898130/296631">this
     * answer in StackOverflow.com</a>. A slight modification appears in <em>The Art of Computer Programming (Section
     * 7.2.1.5)</em> as <em>Algorithm H</em> and is a manifestation of the method by George Hutchinson in
     * <a href="https://doi.org/10.1145/367651.367661">Partitioning algorithms for finite sets</a>. Specifically the
     * pseudocode is as follows:
     * <pre><code>
     * 1. Initialize a vector v with all zeroes, where v[i] determines the subset of element i.
     * 2. Initialize an auxiliary vector m, also with all zeroes.
     * 3. The first partition to be returned is the initial value of v.
     * 4. Set i = n - 1 and loop through the values of v in reverse up to i = 1 (excluding the first element).
     *    If (v[i] &lt; n - 1) and (v[i] &le; m[i]), then increment v[i] by 1 and exit the loop.
     * 5. If there was no change in v as a result of step 4, then the iteration is over.
     * 6. Loop from j = i + 1 to j = n - 1:
     *    Set v[j] = 0.
     *    Set m[j] = max(v[j - 1], m[j - 1]).
     * 7. Return the next partition v and go back to step 4.
     * </code></pre>
     * This implementation has been adapted to guarantee block sizes from {@code kmin} to {@code kmax}.
     * <p>
     * Typically, the implicit map constructor should be used as {@code factory}, for example:
     * <pre><code>
     * Partitions.lexicographicEnumeration(set, kmin, kmax, UnionFindPartition::new);
     * </code></pre>
     * or
     * <pre><code>
     * Partitions.lexicographicEnumeration(set, kmin, kmax, ImmutablePartition::new);
     * </code></pre>
     * The {@link Iterator#next()} method runs in constant amortized time but it will invoke the {@code factory} method
     * which, if implemented properly, should run in linear time in respect to the number of elements. The
     * {@link Iterator} returned by this method uses extra memory that is linear in size in respect to the size of
     * {@code elements}.
     *
     * @param elements the element set
     * @param kmin     the minimum number of subsets in the enumerated partitions
     * @param kmax     the maximum number of subsets in the enumerated partitions
     * @param factory  a function that creates a {@link Partition} from an element set and a mapping function
     * @param <T>      the element type
     * @return an {@link Iterator} that iterates through all possible partitions of {@code elements} with the specified
     * number of blocks
     * @throws NullPointerException     if {@code elements} or {@code factory} is {@code null}
     * @throws NullPointerException     if any element in {@code elements} is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty
     * @throws IllegalArgumentException if {@code kmin} or {@code kmax} is not positive
     * @throws IllegalArgumentException if {@code kmin} or {@code kmax} is greater than the number of elements in
     *                                  {@code elements}
     * @throws IllegalArgumentException if {@code kmin} is greater than {@code kmax}
     */
    public static <T> Iterator<Partition<T>> lexicographicEnumeration(Set<T> elements,
                                                                      int kmin,
                                                                      int kmax,
                                                                      BiFunction<Set<T>, Function<T, Object>, Partition<T>> factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (elements.size() < 1) {
            throw new IllegalArgumentException();
        }
        if (kmin < 1 || kmax < 1) {
            throw new IllegalArgumentException();
        }
        if (kmin > elements.size() || kmax > elements.size()) {
            throw new IllegalArgumentException();
        }
        if (kmin > kmax) {
            throw new IllegalArgumentException();
        }
        final PartitionsIterator pi = new PartitionsIterator(elements.size(), kmin, kmax);
        final Map<T, Integer> indices = new HashMap<>();
        int i = 0;
        for (T e : elements) {
            if (e == null) {
                throw new NullPointerException();
            }
            indices.put(e, i++);
        }
        return new Iterator<Partition<T>>() {
            private int[] nextList = pi.next();

            @Override
            public boolean hasNext() {
                return nextList != null;
            }

            @Override
            public Partition<T> next() {
                final Partition<T> newPartition = factory.apply(indices.keySet(), e -> nextList[indices.get(e)]);
                nextList = pi.next();
                return newPartition;
            }
        };
    }

    /**
     * Returns an iterator that iterates through all possible partitions of a set with the number of subsets in
     * {@code k} in lexicographic order.
     * <p>
     * Specifically, the iterator will enumerate all possible partitions that can exist from the set {@code elements}
     * and contain {@code c} number of blocks, where {@code c} is in {@code k}. The {@code factory} function dictates
     * how the new {@link Partition} will be constructed and is invoked for every call of the {@link Iterator#next()}
     * method. Each {@code next()} call will return a new {@link Partition}. Changes to the {@code elements} set will
     * not reflect to the returned iterator after this method has been invoked.
     * <p>
     * The algorithm used in this method is a modification of the algorithm used in
     * {@link #lexicographicEnumeration(Set, int, int, BiFunction)}.
     * <p>
     * Typically, the implicit map constructor should be used as {@code factory}, for example:
     * <pre><code>
     * Partitions.lexicographicEnumeration(set, k, UnionFindPartition::new);
     * </code></pre>
     * or
     * <pre><code>
     * Partitions.lexicographicEnumeration(set, k, ImmutablePartition::new);
     * </code></pre>
     * The {@link Iterator#next()} method runs in constant amortized time but it will invoke the {@code factory} method
     * which, if implemented properly, should run in linear time in respect to the number of elements. The
     * {@link Iterator} returned by this method uses extra memory that is linear in size in respect to the size of
     * {@code elements}.
     *
     * @param elements the element set
     * @param k        the number of blocks that partitions can have
     * @param factory  a function that creates a {@link Partition} from an element set and a mapping function
     * @param <T>      the element type
     * @return an {@link Iterator} that iterates through all possible partitions of {@code elements} with the specified
     * number of blocks
     * @throws NullPointerException     if {@code elements} or {@code factory} is {@code null}
     * @throws NullPointerException     if any element in {@code elements} is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty
     * @throws IllegalArgumentException if {@code k} is empty
     * @throws IllegalArgumentException if any element in {@code k} is less than {@code 0} or greater than the size of
     *                                  {@code elements}
     */
    public static <T> Iterator<Partition<T>> lexicographicEnumeration(Set<T> elements,
                                                                      int[] k,
                                                                      BiFunction<Set<T>, Function<T, Object>, Partition<T>> factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (elements.size() < 1) {
            throw new IllegalArgumentException();
        }
        if (k.length < 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < k.length; i++) {
            if (k[i] < 1 || k[i] > elements.size()) {
                throw new IllegalArgumentException();
            }
        }
        final PartitionsIteratorDiscrete pi = new PartitionsIteratorDiscrete(elements.size(), Arrays.copyOf(k, k.length));
        final Map<T, Integer> indices = new HashMap<>();
        int i = 0;
        for (T e : elements) {
            if (e == null) {
                throw new NullPointerException();
            }
            indices.put(e, i++);
        }
        return new Iterator<Partition<T>>() {
            private int[] nextList = pi.next();

            @Override
            public boolean hasNext() {
                return nextList != null;
            }

            @Override
            public Partition<T> next() {
                final Partition<T> newPartition = factory.apply(indices.keySet(), e -> nextList[indices.get(e)]);
                nextList = pi.next();
                return newPartition;
            }
        };
    }

    /**
     * Returns an iterator that iterates through all possible partitions of a set with the number of subsets between
     * {@code kmin} and {@code kmax} in reverse lexicographic order.
     * <p>
     * Specifically, the iterator will enumerate all possible partitions that can exist from the set {@code elements}
     * and contain at minimum {@code kmin} subsets and at maximum {@code kmax} subsets. The {@code factory} function
     * dictates how the new {@link Partition} will be constructed and is invoked for every call of the
     * {@link Iterator#next()} method. Each {@code next()} call will return a new {@link Partition}. Changes to the
     * {@code elements} set will not reflect to the returned iterator after this method has been invoked.
     * <p>
     * The algorithm used in this method is a modification of the algorithm used in
     * {@link #lexicographicEnumeration(Set, int, int, BiFunction)}
     * <p>
     * Typically, the implicit map constructor should be used as {@code factory}, for example:
     * <pre><code>
     * Partitions.reverseLexicographicEnumeration(set, kmin, kmax, UnionFindPartition::new);
     * </code></pre>
     * or
     * <pre><code>
     * Partitions.reverseLexicographicEnumeration(set, kmin, kmax, ImmutablePartition::new);
     * </code></pre>
     * The {@link Iterator#next()} method runs in constant amortized time but it will invoke the {@code factory} method
     * which, if implemented properly, should run in linear time in respect to the number of elements. The
     * {@link Iterator} returned by this method uses extra memory that is linear in size in respect to the size of
     * {@code elements}.
     *
     * @param elements the element set
     * @param kmin     the minimum number of subsets in the enumerated partitions
     * @param kmax     the maximum number of subsets in the enumerated partitions
     * @param factory  a function that creates a {@link Partition} from an element set and a mapping function
     * @param <T>      the element type
     * @return an {@link Iterator} that iterates through all possible partitions of {@code elements} with the specified
     * number of blocks
     * @throws NullPointerException     if {@code elements} or {@code factory} is {@code null}
     * @throws NullPointerException     if any element in {@code elements} is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty
     * @throws IllegalArgumentException if {@code kmin} or {@code kmax} is not positive
     * @throws IllegalArgumentException if {@code kmin} or {@code kmax} is greater than the number of elements in
     *                                  {@code elements}
     * @throws IllegalArgumentException if {@code kmin} is greater than {@code kmax}
     */
    public static <T> Iterator<Partition<T>> reverseLexicographicEnumeration(Set<T> elements,
                                                                             int kmin,
                                                                             int kmax,
                                                                             BiFunction<Set<T>, Function<T, Object>, Partition<T>> factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (elements.size() < 1) {
            throw new IllegalArgumentException();
        }
        if (kmin < 1 || kmax < 1) {
            throw new IllegalArgumentException();
        }
        if (kmin > elements.size() || kmax > elements.size()) {
            throw new IllegalArgumentException();
        }
        if (kmin > kmax) {
            throw new IllegalArgumentException();
        }
        final PartitionsIteratorReverse pi = new PartitionsIteratorReverse(elements.size(), kmin, kmax);
        final Map<T, Integer> indices = new HashMap<>();
        int i = 0;
        for (T e : elements) {
            if (e == null) {
                throw new NullPointerException();
            }
            indices.put(e, i++);
        }
        return new Iterator<Partition<T>>() {
            private int[] nextList = pi.next();

            @Override
            public boolean hasNext() {
                return nextList != null;
            }

            @Override
            public Partition<T> next() {
                final Partition<T> newPartition = factory.apply(indices.keySet(), e -> nextList[indices.get(e)]);
                nextList = pi.next();
                return newPartition;
            }
        };
    }

    /**
     * Returns an iterator that iterates through all possible partitions of a set with the number of subsets in
     * {@code k} in reverse lexicographic order.
     * <p>
     * Specifically, the iterator will enumerate all possible partitions that can exist from the set {@code elements}
     * and contain {@code c} number of blocks, where {@code c} is in {@code k}. The {@code factory} function dictates
     * how the new {@link Partition} will be constructed and is invoked for every call of the {@link Iterator#next()}
     * method. Each {@code next()} call will return a new {@link Partition}. Changes to the {@code elements} set will
     * not reflect to the returned iterator after this method has been invoked.
     * <p>
     * The algorithm used in this method is a modification of the algorithm used in
     * {@link #lexicographicEnumeration(Set, int, int, BiFunction)}.
     * <p>
     * Typically, the implicit map constructor should be used as {@code factory}, for example:
     * <pre><code>
     * Partitions.reverseLexicographicEnumeration(set, k, UnionFindPartition::new);
     * </code></pre>
     * or
     * <pre><code>
     * Partitions.reverseLexicographicEnumeration(set, k, ImmutablePartition::new);
     * </code></pre>
     * The {@link Iterator#next()} method runs in constant amortized time but it will invoke the {@code factory} method
     * which, if implemented properly, should run in linear time in respect to the number of elements. The
     * {@link Iterator} returned by this method uses extra memory that is linear in size in respect to the size of
     * {@code elements}.
     *
     * @param elements the element set
     * @param k        the number of blocks that partitions can have
     * @param factory  a function that creates a {@link Partition} from an element set and a mapping function
     * @param <T>      the element type
     * @return an {@link Iterator} that iterates through all possible partitions of {@code elements} with the specified
     * number of blocks
     * @throws NullPointerException     if {@code elements} or {@code factory} is {@code null}
     * @throws NullPointerException     if any element in {@code elements} is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty
     * @throws IllegalArgumentException if {@code k} is empty
     * @throws IllegalArgumentException if any element in {@code k} is less than {@code 0} or greater than the size of
     *                                  {@code elements}
     */
    public static <T> Iterator<Partition<T>> reverseLexicographicEnumeration(Set<T> elements,
                                                                             int[] k,
                                                                             BiFunction<Set<T>, Function<T, Object>, Partition<T>> factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (elements.size() < 1) {
            throw new IllegalArgumentException();
        }
        if (k.length < 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < k.length; i++) {
            if (k[i] < 1 || k[i] > elements.size()) {
                throw new IllegalArgumentException();
            }
        }
        final PartitionsIteratorDiscrete pi = new PartitionsIteratorDiscrete(elements.size(), Arrays.copyOf(k, k.length));
        final Map<T, Integer> indices = new HashMap<>();
        int i = 0;
        for (T e : elements) {
            if (e == null) {
                throw new NullPointerException();
            }
            indices.put(e, i++);
        }
        return new Iterator<Partition<T>>() {
            private int[] nextList = pi.next();

            @Override
            public boolean hasNext() {
                return nextList != null;
            }

            @Override
            public Partition<T> next() {
                final Partition<T> newPartition = factory.apply(indices.keySet(), e -> nextList[indices.get(e)]);
                nextList = pi.next();
                return newPartition;
            }
        };
    }
}
