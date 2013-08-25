package ru.albemuth.util.analysis;

import junit.framework.TestCase;
import org.junit.Test;
import ru.albemuth.util.RandomGenerator;
import ru.albemuth.util.Statistics;

public class TestCharsValues {

    @Test
    public void testNothing() {/* sdo nothing */}

    //@Test
    public void test() {
        try {
            Statistics stats = new Statistics("char[100]");
            for (int i = 0; i < 1000; i++) {
                double[] chars = calculate();
                stats.addValue(chars[100]);
            }
            System.out.println(stats.getAverage() + ", " + stats.getDispersion());
        } catch (Exception e) {
            e.printStackTrace();
            TestCase.fail();
        }
    }


    public double[] calculate() throws CannotFoundExtremumException {
        double[] chars = new double[256];
            for (int i = 0; i < chars.length; i++) {
            chars[i] = i;
        }
        final String standard = RandomGenerator.randomString(10);
        final String[] strings = new String[10];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = RandomGenerator.randomString(10);
        }

        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.1;
        double epsilon = 0.01;

        Args options = new Args(precision, epsilon);

        Args charsArgs = new Args(chars);

        Function minDifference = new Function() {
            @Override
            public double calculate(Args args) {
                return minDifference(standard, strings, args.values);
            }
        };

        Args result = calculator.calculateExtremum(minDifference, charsArgs, options, 10000);

        return result.getValues();

    }

    protected double minDifference(String standard, String[] strings, double[] chars) {
        double difference = Double.MAX_VALUE;
        for (String s: strings) {
            double dl = Levenstein.relativeDifference(standard, s);
            double dc = difference(standard, s, chars);
            double d = Math.abs(dl - dc);
            if (d < difference) {
                difference = d;
            }
        }
        return difference;
    }

    public static double difference(String s1, String s2, double[] chars) {
        assert s1.length() == s2.length();
        double d = 0;
        double l = 0;
        for (int i = 0; i < s1.length(); i++) {
            d += (chars[s1.charAt(i)] - chars[s2.charAt(i)]) * (chars[s1.charAt(i)] - chars[s2.charAt(i)]);
            l += chars[s1.charAt(i)] * chars[s1.charAt(i)];
        }
        return Math.sqrt(d/l);
    }

}
