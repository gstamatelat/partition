package gr.james.partition;

class PartitionsIteratorReverse {
    final int[] a;
    final int[] b;
    final int n;
    final int kmin;
    final int kmax;
    boolean isFirst = true;

    public PartitionsIteratorReverse(int n, int kmin, int kmax) {
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
        for (int i = 0; i < this.n; i++) {
            a[i] = Math.min(i, kmax - 1);
            b[i + 1] = a[i];
        }
    }

    private boolean validate() {
        int max = 0;
        assert a[0] == 0;
        for (int i = 0; i < n; i++) {
            assert a[i] <= kmax - 1;
            assert a[i] <= max + 1;
            max = Math.max(a[i], max);
        }
        assert max >= kmin - 1;
        return true;
    }

    public int[] next() {
        /* Return the first element */
        if (isFirst) {
            isFirst = false;
            return a;
        }

        /* Move backwards and find which index to decrement */
        int i = n - 1;
        while (i > 0 && (a[i] == 0 || kmin - b[i] > n - i)) {
            i = i - 1;
        }
        if (i == 0) return null;
        a[i] = a[i] - 1;
        b[i + 1] = Math.max(a[i], b[i]);

        /* Switch direction and place the maximum values possible */
        for (i = i + 1; b[i] < kmax - 1 && i < n; i++) {
            a[i] = b[i] + 1;
            b[i + 1] = a[i];
        }

        /* Maintain direction and fill the remaining places with kmin-1 */
        for (; i < n; i++) {
            a[i] = kmax - 1;
            b[i + 1] = kmax - 1;
        }

        assert validate();

        /* Return */
        return a;
    }
}
