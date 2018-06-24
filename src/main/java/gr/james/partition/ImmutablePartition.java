package gr.james.partition;

import java.util.*;

/**
 * Immutable implementation of the {@link Partition} interface.
 *
 * @param <T> the element type
 */
public final class ImmutablePartition<T> extends AbstractPartition<T> {
    private final Set<Set<T>> subsets;
    private final Map<T, Set<T>> map;

    /**
     * Constructs a new {@link ImmutablePartition} by copying the given {@link Partition}.
     *
     * @param source the source {@link Partition}
     */
    public ImmutablePartition(Partition<T> source) {
        final Set<Set<T>> subsetsBuilder = new HashSet<>();
        final Map<T, Set<T>> mapBuilder = new HashMap<>();

        for (Set<T> subset : source.subsets()) {
            final Set<T> immutableSubset = Collections.unmodifiableSet(new HashSet<>(subset));
            subsetsBuilder.add(immutableSubset);
            for (T t : subset) {
                mapBuilder.put(t, immutableSubset);
            }
        }

        this.subsets = Collections.unmodifiableSet(subsetsBuilder);
        this.map = Collections.unmodifiableMap(mapBuilder);

        assert this.equals(source);
        assert source.equals(this);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int subsetCount() {
        return subsets.size();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Set<T> elements() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean contains(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return map.containsKey(t);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Set<Set<T>> subsets() {
        return subsets;
    }

    /**
     * {@inheritDoc}
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public Set<T> subset(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        final Set<T> subset = map.get(t);
        if (subset == null) {
            throw new IllegalArgumentException();
        }
        return subset;
    }

    /**
     * {@inheritDoc}
     *
     * @param x {@inheritDoc}
     * @param y {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException     {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public boolean connected(T x, T y) {
        assert (subset(x) == subset(y)) == Objects.equals(subset(x), subset(y));
        return subset(x) == subset(y);
    }

    /**
     * This method is unsupported and will always throw {@link UnsupportedOperationException}.
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported and will always throw {@link UnsupportedOperationException}.
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean remove(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported and will always throw {@link UnsupportedOperationException}.
     *
     * @param subset {@inheritDoc}
     * @throws UnsupportedOperationException always
     */
    @Override
    public void addSubset(Set<T> subset) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported and will always throw {@link UnsupportedOperationException}.
     *
     * @param x {@inheritDoc}
     * @param y {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean union(T x, T y) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is unsupported and will always throw {@link UnsupportedOperationException}.
     *
     * @param t {@inheritDoc}
     * @return {@inheritDoc}
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean split(T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
