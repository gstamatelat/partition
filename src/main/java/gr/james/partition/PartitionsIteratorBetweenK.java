package gr.james.partition;

class PartitionsIteratorBetweenK {
    final int[] a;
    final int[] b;
    final int n;
    final int kmin;
    final int kmax;
    boolean isFirst = true;

    public PartitionsIteratorBetweenK(int n, int kmin, int kmax) {
        assert n > 0;
        assert kmin > 0;
        assert kmax > 0;
        assert n >= kmin;
        assert n >= kmax;
        assert kmax >= kmin;
        this.n = n;
        this.kmin = kmin;
        this.kmax = kmax;
        this.a = new int[this.n];
        this.b = new int[this.n];
        for (int i = this.n - 1; i > this.n - this.kmin; i--) {
            a[i] = this.kmin - this.n + i;
            b[i] = this.kmin - this.n + i - 1;
        }
    }

    public int[] next() {
        if (isFirst) {
            isFirst = false;
            return a;
        }
        int i = n - 1;
        while (a[i] == kmax - 1 || a[i] > b[i]) {
            i--;
        }
        if (i == 0) return null;
        a[i]++;
        int zeroes = Math.max(a[i], b[i]) + n - i - kmin;
        for (i = i + 1; zeroes > 0 && i < n; i++, zeroes--) {
            a[i] = 0;
            b[i] = Math.max(b[i - 1], a[i - 1]);
        }
        for (; i < n; i++) {
            a[i] = Math.max(b[i - 1], a[i - 1]) + 1;
            b[i] = Math.max(b[i - 1], a[i - 1]);
        }
        return a;
    }
}
