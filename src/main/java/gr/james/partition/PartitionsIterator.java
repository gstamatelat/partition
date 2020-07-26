package gr.james.partition;

import java.util.ArrayList;
import java.util.List;

class PartitionsIterator {
    final List<Integer> indices;
    final List<Integer> m;
    final int base;
    boolean isFirst = true;

    public PartitionsIterator(int numberOfElements) {
        assert numberOfElements > 0;
        this.base = numberOfElements;
        this.indices = new ArrayList<>(this.base);
        this.m = new ArrayList<>(this.base);
        for (int i = 0; i < this.base; i++) {
            this.indices.add(0);
            this.m.add(0);
        }
    }

    public List<Integer> next() {
        if (isFirst) {
            isFirst = false;
            return this.indices;
        }
        boolean changed = false;
        int i;
        for (i = indices.size() - 1; i > 0; i--) {
            if (indices.get(i) < this.base - 1 && indices.get(i) <= m.get(i)) {
                indices.set(i, indices.get(i) + 1);
                changed = true;
                break;
            }
        }
        for (i = i + 1; i < this.base; i++) {
            this.indices.set(i, 0);
            this.m.set(i, Math.max(this.m.get(i - 1), this.indices.get(i - 1)));
        }
        if (changed) {
            return this.indices;
        } else {
            return null;
        }
    }
}
