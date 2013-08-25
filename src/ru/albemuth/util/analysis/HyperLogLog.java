package ru.albemuth.util.analysis;

public class HyperLogLog {

    public static final int POW_2_32                    = (int) Math.pow(2, 32);
    private final static int NEGATIVE_POW_2_32          = (int) Math.pow(-2, 32);

    private final int log2m;
    private final RegisterSet registerSet;
    private final double alphaMM;

    public HyperLogLog(int log2m) {
        this.log2m = log2m;
        int m = (int) Math.pow(2, log2m);
        this.registerSet = new RegisterSet(m);

        // See the paper.
        switch (log2m) {
            case 4:
                alphaMM = 0.673 * m * m;
                break;
            case 5:
                alphaMM = 0.697 * m * m;
                break;
            case 6:
                alphaMM = 0.709 * m * m;
                break;
            default:
                alphaMM = (0.7213 / (1 + 1.079 / m)) * m * m;
        }
    }

    public HyperLogLog(double stdError) {
        this(log2m(stdError));
    }

    public boolean offer(Object obj) {
        int hash = MurmurHash.hash32(obj.toString());
        // j becomes the binary address determined by the first b log2m of x
        // j will be between 0 and 2^log2m
        final int j = hash >>> (Integer.SIZE - log2m);
        final int r = Integer.numberOfLeadingZeros((hash << this.log2m) | (1 << (this.log2m - 1)) + 1) + 1;
        if (registerSet.get(j) < r) {
            registerSet.set(j, r);
            return true;
        } else {
            return false;
        }
    }

    public long cardinality() {
        double registerSum = 0;
        int count = registerSet.size();
        for (int i = 0; i < count; i++) {
            registerSum += Math.pow(2, (-1 * registerSet.get(i)));
        }

        double estimate = alphaMM * (1 / registerSum);

        if (estimate <= (5.0 / 2.0) * count) {
            // Small Range Estimate
            double zeros = 0.0;
            for (int z = 0; z < count; z++) {
                if (registerSet.get(z) == 0) {
                    zeros++;
                }
            }
            return Math.round(count * Math.log(count / zeros));
        } else if (estimate <= (1.0 / 30.0) * POW_2_32) {
            // Intermedia Range Estimate
            return Math.round(estimate);
        } else if (estimate > (1.0 / 30.0) * POW_2_32) {
            // Large Range Estimate
            return Math.round((NEGATIVE_POW_2_32 * Math.log(1 - (estimate / POW_2_32))));
        }
        return 0;
    }

    private static int log2m(double stdError) {
        return (int) (Math.log((1.106 / stdError) * (1.106 / stdError)) / Math.log(2));
    }

    private class RegisterSet {

        private int values[];

        public RegisterSet(int size) {
            this.values = new int[size];
        }

        public int size() {
            return values.length;
        }

        public int get(int index) {
            return values[index];
        }

        public void set(int index, int value) {
            values[index] = value;
        }

    }

    private static class MurmurHash {

        /** Generates 32 bit hash from byte array of the given length and
         * seed.
         *
         * @param data byte array to hash
         * @param length length of the array to hash
         * @param seed initial seed value
         * @return 32 bit hash of the given array
         */
        public static int hash32( final byte[] data, int length, int seed) {
            // 'm' and 'r' are mixing constants generated offline.
            // They're not really 'magic', they just happen to work well.
            final int m = 0x5bd1e995;
            final int r = 24;
            // Initialize the hash to a random value
            int h = seed^length;
            int length4 = length/4;

            for (int i=0; i<length4; i++) {
                final int i4 = i*4;
                int k = (data[i4+0]&0xff) +((data[i4+1]&0xff)<<8)
                        +((data[i4+2]&0xff)<<16) +((data[i4+3]&0xff)<<24);
                k *= m;
                k ^= k >>> r;
                k *= m;
                h *= m;
                h ^= k;
            }

            // Handle the last few bytes of the input array
            switch (length%4) {
                case 3: h ^= (data[(length&~3) +2]&0xff) << 16;
                case 2: h ^= (data[(length&~3) +1]&0xff) << 8;
                case 1: h ^= (data[length&~3]&0xff);
                    h *= m;
            }

            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        }

        /** Generates 32 bit hash from byte array with default seed value.
         *
         * @param data byte array to hash
         * @param length length of the array to hash
         * @return 32 bit hash of the given array
         */
        public static int hash32( final byte[] data, int length) {
            return hash32( data, length, 0x9747b28c);
        }

        /** Generates 32 bit hash from a string.
         *
         * @param text string to hash
         * @return 32 bit hash of the given string
         */
        public static int hash32( final String text) {
            final byte[] bytes = text.getBytes();
            return hash32( bytes, bytes.length);
        }

        /** Generates 32 bit hash from a substring.
         *
         * @param text string to hash
         * @param from starting index
         * @param length length of the substring to hash
         * @return 32 bit hash of the given string
         */
        public static int hash32( final String text, int from, int length) {
            return hash32( text.substring( from, from+length));
        }

    }


}
