package su.nightexpress.nightcore.util.random;

import java.util.Random;

public class MTRandom extends Random {

    private final static int UPPER_MASK = 0x80000000;
    private final static int LOWER_MASK = 0x7fffffff;

    // @formatter:off
    private final static int   N             = 624;
    private final static int   M             = 397;
    private final static int[] MAGIC         = {0x0, 0x9908b0df};
    private final static int   MAGIC_FACTOR1 = 1812433253;
    private final static int   MAGIC_FACTOR2 = 1664525;
    private final static int   MAGIC_FACTOR3 = 1566083941;
    private final static int   MAGIC_MASK1   = 0x9d2c5680;
    private final static int   MAGIC_MASK2   = 0xefc60000;
    private final static int   MAGIC_SEED    = 19650218;
    @SuppressWarnings("unused")
    private final static long  DEFAULT_SEED  = 5489L;
    // @formatter:on

    // Internal state
    private transient int[] mt;
    private transient int mti;

    // Temporary buffer used during setSeed(long)
    private transient int[] ibuf;

    public MTRandom() {

    }

    private void setSeed() {
        if (this.mt == null)
            this.mt = new int[MTRandom.N];

        // ---- Begin Mersenne Twister Algorithm ----
        this.mt[0] = MTRandom.MAGIC_SEED;
        for (this.mti = 1; this.mti < MTRandom.N; this.mti++) {
            this.mt[this.mti] = (MTRandom.MAGIC_FACTOR1 * (this.mt[this.mti - 1] ^ (this.mt[this.mti - 1] >>> 30)) + this.mti);
        }
        // ---- End Mersenne Twister Algorithm ----
    }

    @Override
    public final synchronized void setSeed(final long seed) {
        if (this.ibuf == null)
            this.ibuf = new int[2];

        this.ibuf[0] = (int) seed;
        this.ibuf[1] = (int) (seed >>> 32);
        this.setSeed(this.ibuf);
    }

    @Override
    protected final synchronized int next(final int bits) {
        // ---- Begin Mersenne Twister Algorithm ----
        int y, kk;
        if (this.mti >= MTRandom.N) {

            for (kk = 0; kk < MTRandom.N - MTRandom.M; kk++) {
                y = (this.mt[kk] & MTRandom.UPPER_MASK) | (this.mt[kk + 1] & MTRandom.LOWER_MASK);
                this.mt[kk] = this.mt[kk + MTRandom.M] ^ (y >>> 1) ^ MTRandom.MAGIC[y & 0x1];
            }
            for (; kk < MTRandom.N - 1; kk++) {
                y = (this.mt[kk] & MTRandom.UPPER_MASK) | (this.mt[kk + 1] & MTRandom.LOWER_MASK);
                this.mt[kk] = this.mt[kk + (MTRandom.M - MTRandom.N)] ^ (y >>> 1) ^ MTRandom.MAGIC[y & 0x1];
            }
            y = (this.mt[MTRandom.N - 1] & MTRandom.UPPER_MASK) | (this.mt[0] & MTRandom.LOWER_MASK);
            this.mt[MTRandom.N - 1] = this.mt[MTRandom.M - 1] ^ (y >>> 1) ^ MTRandom.MAGIC[y & 0x1];

            this.mti = 0;
        }

        y = this.mt[this.mti++];

        // Tempering
        y ^= (y >>> 11);
        y ^= (y << 7) & MTRandom.MAGIC_MASK1;
        y ^= (y << 15) & MTRandom.MAGIC_MASK2;
        y ^= (y >>> 18);
        // ---- End Mersenne Twister Algorithm ----
        return (y >>> (32 - bits));
    }

    // This is a fairly obscure little code section to pack a
    // byte[] into an int[] in little endian ordering.

    public final synchronized void setSeed(final int[] buf) {
        final int length = buf.length;
        if (length == 0)
            throw new IllegalArgumentException("Seed buffer may not be empty");
        // ---- Begin Mersenne Twister Algorithm ----
        int i = 1, j = 0, k = (Math.max(MTRandom.N, length));
        this.setSeed();
        for (; k > 0; k--) {
            this.mt[i] = (this.mt[i] ^ ((this.mt[i - 1] ^ (this.mt[i - 1] >>> 30)) * MTRandom.MAGIC_FACTOR2)) + buf[j] + j;
            i++;
            j++;
            if (i >= MTRandom.N) {
                this.mt[0] = this.mt[MTRandom.N - 1];
                i = 1;
            }
            if (j >= length)
                j = 0;
        }
        for (k = MTRandom.N - 1; k > 0; k--) {
            this.mt[i] = (this.mt[i] ^ ((this.mt[i - 1] ^ (this.mt[i - 1] >>> 30)) * MTRandom.MAGIC_FACTOR3)) - i;
            i++;
            if (i >= MTRandom.N) {
                this.mt[0] = this.mt[MTRandom.N - 1];
                i = 1;
            }
        }
        this.mt[0] = MTRandom.UPPER_MASK; // MSB is 1; assuring non-zero initial array
        // ---- End Mersenne Twister Algorithm ----
    }
}