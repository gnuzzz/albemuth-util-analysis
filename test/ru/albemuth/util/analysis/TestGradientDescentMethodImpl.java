package ru.albemuth.util.analysis;

import junit.framework.TestCase;

public class TestGradientDescentMethodImpl extends TestCase {

    public void testFindLineExtremumByGoldenSectionSearch() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.1;
        Args result;

        X2 fx2 = new X2();
        Args a = new X2Arguments(-1);
        Args b = new X2Arguments(1);
        Args x1 = a.clone();
        Args x2 = b.clone();
        Args gradient = new X2Arguments(-2);
        double gradientLengthToPhi = gradient.distanceFrom0() * GradientDescentMethodImpl.PHI;
        result = calculator.findLineExtremumByGoldenSectionSearch(fx2, a, b, x1, x2, gradient, gradientLengthToPhi, precision);
        assertEquals(0, result.getValues()[0], precision);

        X2Y2 fx2y2 = new X2Y2();
        a = new X2Y2Arguments(-1, -1);
        b = new X2Y2Arguments(1, 1);
        x1 = a.clone();
        x2 = b.clone();
        gradient = new X2Y2Arguments(-2, -2);
        gradientLengthToPhi = gradient.distanceFrom0() * GradientDescentMethodImpl.PHI;
        result = calculator.findLineExtremumByGoldenSectionSearch(fx2y2, a, b, x1, x2, gradient, gradientLengthToPhi, precision);
        assertEquals(0, result.getValues()[0], precision);
        assertEquals(0, result.getValues()[1], precision);
    }

    public void testFindSegmentWithExtremum() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.1;

        X2 fx2 = new X2();
        Args startPoint = new X2Arguments(-1);
        Args gradient = new X2Arguments(-2);
        Args a = startPoint.clone();
        Args b = startPoint.clone();
        Args tmp = startPoint.clone();
        calculator.findSegmentWithExtremum(fx2, gradient, precision, a, b, tmp);
        assertEquals(-0.7, a.getValues()[0], precision);
        assertEquals(0.5, b.getValues()[0], precision);

        X2Y2 fx2y2 = new X2Y2();
        startPoint = new X2Y2Arguments(-1, -1);
        gradient = new X2Y2Arguments(-2, -2);
        a = startPoint.clone();
        b = startPoint.clone();
        tmp = startPoint.clone();
        calculator.findSegmentWithExtremum(fx2y2, gradient, precision, a, b, tmp);
        assertEquals(-0.5, a.getValues()[0], precision);
        assertEquals(-0.5, a.getValues()[1], precision);
        assertEquals(1.19, b.getValues()[0], precision);
        assertEquals(1.19, b.getValues()[1], precision);
    }

    public void testCalculateLineExtremum() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.1;

        X2 fx2 = new X2();
        Args current = new X2Arguments(-1);
        Args extremum = current.clone();
        Args gradient = new X2Arguments(-2);
        Args buffer1 = current.clone();
        Args buffer2 = current.clone();
        Args buffer3 = current.clone();
        Args buffer4 = current.clone();
        extremum = calculator.calculateLineExtremum(fx2, current, extremum, gradient, precision, buffer1, buffer2, buffer3, buffer4);
        assertEquals(0, extremum.getValues()[0], precision);

        X2Y2 fx2y2 = new X2Y2();
        current = new X2Y2Arguments(-1, -1);
        extremum = current.clone();
        gradient = new X2Y2Arguments(-2, -2);
        buffer1 = current.clone();
        buffer2 = current.clone();
        buffer3 = current.clone();
        buffer4 = current.clone();
        extremum = calculator.calculateLineExtremum(fx2y2, current, extremum, gradient, precision, buffer1, buffer2, buffer3, buffer4);
        assertEquals(0, extremum.getValues()[0], precision);
        assertEquals(0, extremum.getValues()[1], precision);
    }

    public void testCalculateExtremum() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.1;
        double epsilon = 0.01;
        Args result;
        Args options = new Args(precision, epsilon);
        X2Y2 fx2y2 = new X2Y2();

        try {
            result = calculator.calculateExtremum(fx2y2, new Args(3, 3), options, 100);
            assertEquals(0, result.getValues()[0], precision);
            assertEquals(0, result.getValues()[1], precision);
        } catch (CannotFoundExtremumException e) {
            System.err.println(e.getStopValue() + ", " + e.getStopIterationNumber());
            e.printStackTrace();
            fail();
        }
    }

    public void testCalculateExtremumComplex() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.01;
        double epsilon = 0.001;
        Args result;
        Args options = new Args(precision, epsilon);

        try {
            Complex function = new Complex();
            result = calculator.calculateExtremum(function, new ComplexArgs(0.0, 0.5), options, 1000);
            System.out.println(result + ", " + function.calculate(result));
        } catch (CannotFoundExtremumException e) {
            System.err.println(e.getStopValue() + ", " + e.getStopIterationNumber());
            e.printStackTrace();
            fail();
        }
    }

    public void testCalculateExtremumComplex2() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.00001;
        double epsilon = 0.000001;
        Args result;
        Args options = new Args(precision, epsilon);

        try {
            result = calculator.calculateExtremum(new Function() {
                @Override
                public double calculate(Args args) {
                    return args.values[0]*args.values[0]*args.values[0]*args.values[0] - 3*args.values[0]*args.values[0]*args.values[0] + 2;
                }
            }, new Args(6), options, 1000);
            assertEquals(2.25, result.getValues()[0], precision);
        } catch (CannotFoundExtremumException e) {
            System.err.println(e.getStopValue() + ", " + e.getStopIterationNumber());
            e.printStackTrace();
            fail();
        }
    }

    public void testPriority() {
        GradientDescentMethodImpl calculator = new GradientDescentMethodImpl();
        double precision = 0.001;
        double epsilon = 0.00001;
        Args result;
        Args options = new Args(precision, epsilon);

        try {
            //result = calculator.calculateExtremum(new Args(new double[]{-1, 1, 0, 0}) {
            Function function = new Function() {
                @Override
                public double calculate(Args args) {
                    double f0 = 1 - f(0D, args.values);
                    double fMonthWithoutDay = 0.75 - f(29, args.values);
                    double fMonth = 0.5 - f(30, args.values);
                    double fMonthWithDay = 0.25 - f(31, args.values);
                    double f2Month = 0 - f(2*30, args.values);

                    /*double f0 = 1 - f(0D, values);
                    double fMonthWithoutDay = 0.88 - f(1, values);
                    double fMonth = 0.5 - f(2, values);
                    double fMonthWithDay = 0.12 - f(3, values);
                    double f2Month = 0 - f(4, values);*/

                    return Math.sqrt(f0*f0 + fMonthWithoutDay*fMonthWithoutDay + fMonth*fMonth + fMonthWithDay*fMonthWithDay + f2Month*f2Month);

                    /*double f0 = (-Math.tanh(2*0D + 3)) - f(0D, values[0], values[1], values[2]);
                    double f1 = (-Math.tanh(2*1D + 3)) - f(1D, values[0], values[1], values[2]);
                    double f5 = (-Math.tanh(2*5D + 3)) - f(5D, values[0], values[1], values[2]);
                    double f10 = (-Math.tanh(2*10D + 3)) - f(10D, values[0], values[1], values[2]);
                    double f20 = (-Math.tanh(2*20D + 3)) - f(20D, values[0], values[1], values[2]);

                    return Math.sqrt(f0*f0 + f1*f1 + f5*f5 + f10*f10 + f20*f20);*/
                }
            };

            result = calculator.calculateExtremum(function, new Args(-0.5, 0.066, -2, 0.5), options, 1000000);
            System.out.println(result.getValues()[0]);
            System.out.println(result.getValues()[1]);
            System.out.println(result.getValues()[2]);
            System.out.println(result.getValues()[3]);

            System.out.println("---------------");
            System.out.println(f(0, result.getValues()));
            System.out.println(f(29, result.getValues()));
            System.out.println(f(30, result.getValues()));
            System.out.println(f(31, result.getValues()));
            System.out.println(f(2*30, result.getValues()));

            /*System.out.println(f(0, result.getValues()));
            System.out.println(f(1, result.getValues()));
            System.out.println(f(2, result.getValues()));
            System.out.println(f(3, result.getValues()));
            System.out.println(f(4, result.getValues()));*/

            /*
            0.5     - 8
            1       - 4
            2       - 2
            4       - 1

            4/x = 60
            x = 4/60 = 1/15
             */
            //-0.5 0.066 -2 0.5
            System.out.println("---------------");
            System.out.println(function.calculate(result));
            System.out.println("---------------");
            double resultValue = function.calculate(result);
            double[] values = result.getValues();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            double[] v = new double[4];
                            v[0] = values[0] + i*0.001;
                            v[1] = values[1] + j*0.001;
                            v[2] = values[2] + k*0.001;
                            v[3] = values[3] + l*0.001;
                            result.values = v;
                            double d = function.calculate(result);
                            System.out.println(d + ", " + (d - resultValue));
                        }
                    }
                }
            }

            System.out.println("---------------");
            result.values = new double[]{-0.5, 0.066, -2, 0.5};
            System.out.println(function.calculate(result));
        } catch (CannotFoundExtremumException e) {
            System.err.println(e.getStopValue() + ", " + e.getStopIterationNumber());
            e.printStackTrace();
            fail();
        }
    }

    public static double f(double x, double[] parameters) {
        return parameters[0] * Math.tanh(parameters[1]*x + parameters[2]) + parameters[3];
    }

    class Complex extends Function {
        @Override
        public double calculate(Args args) {
            return Math.sin(0.5*args.values[0]*args.values[0] - 0.25*args.values[1]*args.values[1] + 3) * Math.cos(2*args.values[0] + 1 - Math.exp(args.values[1]));
        }
    }

    class ComplexArgs extends Args {

        ComplexArgs(double x, double y) {
            this(new double[]{x, y});
        }

        ComplexArgs(double[] values) {
            super(values);
        }

    }

}
