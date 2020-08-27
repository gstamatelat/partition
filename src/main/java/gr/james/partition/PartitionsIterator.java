package gr.james.partition;

class PartitionsIterator {
    final int[] a;
    final int[] b;
    final int n;
    final int kmin;
    final int kmax;
    boolean isFirst = true;

    public PartitionsIterator(int n, int kmin, int kmax) {
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
        this.b = new int[this.n + 1];
        for (int i = this.n - 1; i > this.n - this.kmin; i--) {
            a[i] = this.kmin - this.n + i;
            b[i] = this.kmin - this.n + i - 1;
        }
    }

    public int[] next() {
        /* Return the first element */
        if (isFirst) {
            isFirst = false;
            return a;
        }

        /* Find which index to increment going backwards */
        int i = n - 1;
        while (a[i] == kmax - 1 || a[i] > b[i]) {
            i--;
        }
        if (i == 0) return null;
        a[i]++;
        b[i + 1] = Math.max(a[i], b[i]);

        /* Start going forward putting zeroes for as long as we can */
        int zeroes = b[i + 1] + n - i - kmin;
        for (i = i + 1; zeroes > 0 && i < n; i++, zeroes--) {
            a[i] = 0;
            b[i + 1] = b[i];
        }

        /* Keep going forward putting the minimum values that we can */
        for (; i < n; i++) {
            a[i] = b[i] + 1;
            b[i + 1] = a[i];
        }

        /* Return */
        return a;
    }
}
