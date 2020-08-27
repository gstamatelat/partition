package gr.james.partition;

class PartitionsIteratorK {
    final int[] a;
    final int[] b;
    final int n;
    final int k;
    boolean isFirst = true;

    public PartitionsIteratorK(int n, int k) {
        assert n > 0;
        assert k > 0;
        assert n >= k;
        this.n = n;
        this.k = k;
        this.a = new int[this.n];
        this.b = new int[this.n];
        for (int i = this.n - 1; i > this.n - this.k; i--) {
            a[i] = this.k - this.n + i;
            b[i] = this.k - this.n + i - 1;
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
