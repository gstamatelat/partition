package gr.james.partition;

import java.util.Set;

abstract class AbstractPartition<T> implements Partition<T> {
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Set<T> subset : subsets()) {
            final int subsetHashCode = subset.hashCode();
            for (T t : subset) {
                hashCode += subsetHashCode ^ t.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Partition)) {
            return false;
        }
        final Partition<?> that = (Partition<?>) obj;
        assert !subsets().equals(that.subsets()) ||
                (hashCode() == that.hashCode() &&
                        elements().equals(that.elements()) &&
                        size() == that.size() &&
                        subsetCount() == that.subsetCount());
        return subsets().equals(that.subsets());
    }

    @Override
    public String toString() {
        return subsets().toString();
    }
}
