package gr.james.partition;

class PartitionsIterator {
    final int[] indices;
    final int[] m;
    final int base;
    boolean isFirst = true;

    public PartitionsIterator(int numberOfElements) {
        assert numberOfElements > 0;
        this.base = numberOfElements;
        this.indices = new int[this.base];
        this.m = new int[this.base];
    }

    public int[] next() {
        if (isFirst) {
            isFirst = false;
            return this.indices;
        }
        boolean changed = false;
        int i;
        for (i = indices.length - 1; i > 0; i--) {
            if (indices[i] < this.base - 1 && indices[i] <= m[i]) {
                indices[i]++;
                changed = true;
                break;
            }
        }
        for (i = i + 1; i < this.base; i++) {
            indices[i] = 0;
            m[i] = Math.max(m[i - 1], indices[i - 1]);
        }
        if (changed) {
            return this.indices;
        } else {
            return null;
        }
    }
}
