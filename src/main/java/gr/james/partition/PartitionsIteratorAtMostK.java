package gr.james.partition;

class PartitionsIteratorAtMostK {
    final int[] a;
    final int[] b;
    final int n;
    final int k;
    boolean isFirst = true;

    public PartitionsIteratorAtMostK(int numberOfElements, int maxBlocks) {
        assert numberOfElements > 0;
        assert maxBlocks > 0;
        assert numberOfElements >= maxBlocks;
        this.n = numberOfElements;
        this.k = maxBlocks;
        this.a = new int[this.n];
        this.b = new int[this.n];
    }

    public int[] next() {
        if (isFirst) {
            isFirst = false;
            return a;
        }
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
        return a;
    }
}