package gr.james.partition;

class PartitionsIteratorK {
    final int[] a;
    final int[] b;
    final int n;
    final int k;
    final PartitionsIteratorAtMostK it;
    boolean isFirst = true;

    public PartitionsIteratorK(int numberOfElements, int blocks) {
        assert numberOfElements > 0;
        assert blocks > 0;
        assert numberOfElements >= blocks;
        this.n = numberOfElements;
        this.k = blocks;
        this.a = new int[this.n];
        this.b = new int[this.n];
        this.it = new PartitionsIteratorAtMostK(numberOfElements, blocks);
        for (int i = n - 1; i > n - k; i--) {
            a[i] = k - n + i;
            b[i] = k - n + i - 1;
        }
    }

    public int[] next() {
        if (isFirst) {
            isFirst = false;
            return a;
        }
        do {
            int i = n - 1;
            while (a[i] == k - 1 || a[i] > b[i]) {
                i--;
            }
            if (i == 0) return null;
            a[i]++;
            for (i = i + 1; i < n; i++) {
                a[i] = 0;
                b[i] = Math.max(b[i - 1], a[i - 1]);
            }
        } while (Math.max(a[n - 1], b[n - 1]) != k - 1);
        return a;
    }
}
