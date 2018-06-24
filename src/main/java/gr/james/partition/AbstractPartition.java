package gr.james.partition;

abstract class AbstractPartition<T> implements Partition<T> {
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (T e : elements()) {
            hashCode += e.hashCode() ^ subset(e).hashCode();
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
