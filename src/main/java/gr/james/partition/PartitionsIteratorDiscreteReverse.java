package gr.james.partition;

import java.util.Arrays;

class PartitionsIteratorDiscreteReverse {
    final int[] a;
    final int[] b;
    final int[] k;
    final int[] m;
    final int[] mr;
    final int n;
    boolean isFirst = true;

    public PartitionsIteratorDiscreteReverse(int n, int[] k) {
        assert n > 0;
        assert k.length > 0;
        assert Arrays.stream(k).allMatch(x -> x <= n);
        this.n = n;
        this.k = k;
        this.a = new int[this.n];
        this.b = new int[this.n + 1];
        Arrays.sort(k);
        for (int i = 0; i < this.n; i++) {
            a[i] = Math.min(i, k[k.length - 1] - 1);
            b[i + 1] = a[i];
        }
        m = new int[k[k.length - 1] + 1];
        mr = new int[k[k.length - 1] + 1];
        int c = 0;
        for (int i = 0; i < k.length; i++) {
            for (; c < k[i] + 1; c++) {
                m[c] = k[i];
            }
        }
        for (int i = 0; i < k.length - 1; i++) {
            for (int j = k[i]; j < k[i + 1]; j++) {
                mr[j] = k[i];
            }
        }
        mr[mr.length - 1] = k[k.length - 1];
        assert m[0] == k[0];
        assert m[m.length - 1] == k[k.length - 1];
        assert mr[0] == 0;
        assert mr[mr.length - 1] == mr.length - 1;
    }

    private boolean validate() {
        int max = 0;
        assert a[0] == 0;
        for (int i = 0; i < n; i++) {
            assert a[i] <= k[k.length - 1] - 1;
            assert a[i] <= max + 1;
            max = Math.max(a[i], max);
        }
        assert Arrays.binarySearch(k, max + 1) >= 0;
        return true;
    }

    public int[] next() {
        /* Return the first element */
        if (isFirst) {
            isFirst = false;
            return a;
        }

        /* Find which index to decrement going backwards */
        int i = n;
        int tmpMax;
        do {
            i--;
            tmpMax = Math.max(a[i] - 1, b[i]);
        } while (i > 0 && (a[i] == 0 || m[tmpMax + 1] - tmpMax - 1 > n - i - 1));
        if (i == 0) return null;
        a[i]--;
        b[i + 1] = Math.max(a[i], b[i]);

        /* Switch direction and place the maximum values possible */
        int maxPossible = b[i + 1] + n - i;
        int kmax = maxPossible >= mr.length ? k[k.length - 1] : mr[maxPossible];
        for (i = i + 1; b[i] < kmax - 1 && i < n; i++) {
            a[i] = b[i] + 1;
            b[i + 1] = a[i];
        }

        /* Maintain direction and fill the remaining places with kmax-1 */
        for (; i < n; i++) {
            a[i] = kmax - 1;
            b[i + 1] = kmax - 1;
        }

        /* Validate */
        assert validate();

        /* Return */
        return a;
    }
}
