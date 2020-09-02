package gr.james.partition;

import java.util.Arrays;

class PartitionsIteratorDiscrete {
    final int[] a;
    final int[] b;
    final int[] k;
    final int[] m;
    final int n;
    boolean isFirst = true;

    public PartitionsIteratorDiscrete(int n, int[] k) {
        assert n > 0;
        assert k.length > 0;
        assert Arrays.stream(k).distinct().count() == k.length;
        assert Arrays.stream(k).allMatch(x -> x <= n);
        this.n = n;
        this.k = k;
        this.a = new int[this.n];
        this.b = new int[this.n + 1];
        Arrays.sort(k);
        for (int i = this.n - 1; i > this.n - k[0]; i--) {
            a[i] = k[0] - this.n + i;
            b[i] = k[0] - this.n + i - 1;
        }
        m = new int[k[k.length - 1] + 1];
        int c = 0;
        for (int i = 0; i < k.length; i++) {
            for (; c < k[i] + 1; c++) {
                m[c] = k[i];
            }
        }
        assert m[0] == k[0];
        assert m[m.length - 1] == k[k.length - 1];
    }

    public static void main(String[] args) {
        PartitionsIteratorDiscrete pi = new PartitionsIteratorDiscrete(10, new int[]{2, 6, 6, 9, 5});
        int count = 1;
        for (int[] a = pi.next(); a != null; a = pi.next()) {
            System.out.printf("%d %s%n", count++, Arrays.toString(a));
        }
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

        /* Find which index to increment going backwards */
        int i = n;
        int tmpMax;
        do {
            i--;
            tmpMax = Math.max(a[i] + 1, b[i]);
        } while (a[i] == k[k.length - 1] - 1 || m[tmpMax + 1] - tmpMax - 1 > n - i - 1 || a[i] > b[i]);
        if (i == 0) return null;
        a[i]++;
        b[i + 1] = Math.max(a[i], b[i]);

        /* Start going forward putting zeroes for as long as we can */
        int zeroes = b[i + 1] + n - i - m[b[i + 1] + 1];
        assert zeroes >= 0;
        for (i = i + 1; zeroes > 0 && i < n; i++, zeroes--) {
            a[i] = 0;
            b[i + 1] = b[i];
        }

        /* Keep going forward putting the minimum values that we can */
        for (; i < n; i++) {
            a[i] = b[i] + 1;
            b[i + 1] = a[i];
        }

        /* Validate */
        assert validate();

        /* Return */
        return a;
    }
}
