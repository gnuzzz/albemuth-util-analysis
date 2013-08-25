package ru.albemuth.util.analysis;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestCoin {

    @Test
    public void testFraction() {
        try {
            Fraction f = new Fraction();
            f.multiply(1, 5);
            f.divide(1, 4);
            System.out.println(f.calculate());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void tescCombination() {
        try {
            Combination c = new Combination(6, 2);
            System.out.println(c.calculate());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testBernully() {
        try {
            double p = 0;
            for (int k = 35; k <= 41; k++) {
                Combination c =  new Combination(400, k);
                p += c.calculate() * Math.pow(0.1, k) * Math.pow(0.9, 400 - k);
            }
            System.out.println(p);
            //0.42623218812390995
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void testModel() {
        try {
            Model model = new Model();
            int validSeries = 0;
            int n = 1000000;
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                int hCount = model.createSeries();
                if (35 <= hCount && hCount <= 41) {
                    validSeries++;
                }
            }
            System.out.println(validSeries + ", " + (validSeries/(double)n) + ", " + (System.currentTimeMillis() - t1));
            //425976, 0.425976, 27116
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}

class Fraction {

    private double value = 1;
    private List<Integer> numerator = new ArrayList<Integer>();
    private List<Integer> denominator = new ArrayList<Integer>();

    public void multiply(int value) {
        numerator.add(value);
    }

    public void multiply(int from, int to) {
        for (int i = from; i <= to; i++) {
            multiply(i);
        }
    }

    public void divide(int value) {
        denominator.add(value);
    }

    public void divide(int from, int to) {
        for (int i = from; i <= to; i++) {
            divide(i);
        }
    }

    public double calculate() {
        int length = numerator.size() < denominator.size() ? numerator.size() : denominator.size();
        int index;
        for (index = 0; index < length; index++) {
            value *= numerator.get(index);
            value /= denominator.get(index);
        }
        if (numerator.size() < denominator.size()) {
            for (; index < denominator.size(); index++) {
                value /= denominator.get(index);
            }
        } else if (numerator.size() > denominator.size()) {
            for (; index < numerator.size(); index++) {
                value *= numerator.get(index);
            }
        }
        return value;
    }

}

class Combination {

    private int n;
    private int k;

    Combination(int n, int k) {
        assert k <= n;
        this.n = n;
        this.k = k;
    }

    public int getN() {
        return n;
    }

    public int getK() {
        return k;
    }

    public double calculate() {
        Fraction f = new Fraction();
        f.multiply(n - k + 1, n);
        f.divide(1, k);
        return f.calculate();
    }
}

class Model {

    public int createSeries() {
        int hCount = 0;
        for (int i = 0; i < 400; i++) {
            double d = Math.random();
            if (d <= 0.1) {hCount++;}
        }
        return hCount;
    }

}