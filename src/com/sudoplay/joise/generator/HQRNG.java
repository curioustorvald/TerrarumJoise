package com.sudoplay.joise.generator;

/**
 * Xoroshiro128+
 *
 * see https://github.com/SquidPony/SquidLib/blob/master/squidlib-util/src/main/java/squidpony/squidmath/XoRoRNG.java
 *
 * Created by minjaesong on 2018-10-26.
 */
public class HQRNG extends BasePRNG {

    private static final long DOUBLE_MASK = (1L << 53) - 1;
    private static final double NORM_53 = 1. / (1L << 53);

    private volatile long state0, state1;

    public HQRNG() {
        setSeed(10000L);
    }

    private long nextLong() {
        final long s0 = state0;
        long s1 = state1;
        final long result = s0 + s1;

        s1 ^= s0;
        state0 = (s0 << 55 | s0 >>> 9) ^ s1 ^ (s1 << 14); // a, b
        state1 = (s1 << 36 | s1 >>> 28); // c
        /*
        state0 = Long.rotateLeft(s0, 55) ^ s1 ^ (s1 << 14); // a, b
        state1 = Long.rotateLeft(s1, 36); // c
        */
        return result;
    }

    @Override
    public int get() {
        return (int) nextLong();
    }

    @Override
    public double get01() {
        return (nextLong() & DOUBLE_MASK) * NORM_53;
    }

    @Override
    public int getTarget(int t) {
        return (int) ((t * (nextLong() >>> 33)) >> 31);
    }

    @Override
    public int getRange(int low, int high) {
        return low + getTarget(high - low + 1);
    }

    @Override
    public void setSeed(long seed) {
        long state = seed + 0x9E3779B97F4A7C15L,
                z = state;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        state0 = z ^ (z >>> 31);
        state += 0x9E3779B97F4A7C15L;
        z = state;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        state1 = z ^ (z >>> 31);
    }

}
