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
     * Returns an iterator that iterates through all possible partitions of a set.
     * <p>
     * Specifically, the iterator will enumerate all possible partitions that can exist from the set {@code of}. The
     * {@code factory} function dictates how the new {@link Partition} will be constructed and is invoked for every call
     * the the {@link Iterator#next()} method. Each {@code next()} call will return a new {@link Partition}.
     * <p>
     * The algorithm used in this method is mentioned in <a href="https://stackoverflow.com/a/30898130/296631">this
     * answer in StackOverflow.com</a>. Specifically the pseudocode is as follows:
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
     * <p>
     * The {@link Iterator#next()} method runs in constant amortized time but it will invoke the {@code factory} method
     * which, if implemented properly, should run in linear time in respect to the number of elements.
     *
     * @param of      the element set
     * @param factory a function that creates a {@link Partition} from an element set and a mapping function
     * @param <T>     the element type
     * @return an {@link Iterator} that iterates through all possible partitions of the set {@code of}
     * @throws NullPointerException     if {@code of} or {@code factory} is {@code null}
     * @throws NullPointerException     if any element in {@code of} is {@code null}
     * @throws IllegalArgumentException if {@code of} is empty
     */
    public static <T> Iterator<Partition<T>> partitions(Set<T> of, BiFunction<Set<T>, Function<T, Object>, Partition<T>> factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (of.size() < 1) {
            throw new IllegalArgumentException();
        }
        final PartitionsIterator pi = new PartitionsIterator(of.size());
        final Map<T, Integer> indices = new HashMap<>();
        int i = 0;
        for (T e : of) {
            if (e == null) {
                throw new NullPointerException();
            }
            indices.put(e, i++);
        }
        return new Iterator<Partition<T>>() {
            private List<Integer> nextList = pi.next();

            @Override
            public boolean hasNext() {
                return nextList != null;
            }

            @Override
            public Partition<T> next() {
                final Partition<T> newPartition = factory.apply(indices.keySet(), e -> nextList.get(indices.get(e)));
                nextList = pi.next();
                return newPartition;
            }
        };
    }
}
