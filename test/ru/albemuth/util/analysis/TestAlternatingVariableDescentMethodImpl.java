package ru.albemuth.util.analysis;

import junit.framework.TestCase;

public class TestAlternatingVariableDescentMethodImpl extends TestCase {

    public void testFindOneArgumentExtremum() {
        AlternatingVariableDescentMethodImpl calculator = new AlternatingVariableDescentMethodImpl();
        X2Arguments arguments, a1, a2;
        double precision = 1.0;
        double change;
        X2 fx2 = new X2();

        arguments = new X2Arguments(10.5);
        change = -2.0;
        a1 = arguments.clone();
        a2 = arguments.clone();
        arguments = (X2Arguments)calculator.findOneArgumentExtremum(fx2, a1, a2, 0, change);
        assertEquals(0.5, arguments.getValues()[0], precision);

        arguments = new X2Arguments(-10.5);
        change = 2.0;
        a1 = arguments.clone();
        a2 = arguments.clone();
        arguments = (X2Arguments)calculator.findOneArgumentExtremum(fx2, a1, a2, 0, change);
        assertEquals(-0.5, arguments.getValues()[0], precision);
    }

    public void testCalculateOneArgumentExtremum() {
        AlternatingVariableDescentMethodImpl calculator = new AlternatingVariableDescentMethodImpl();
        X2Arguments arguments, a1, a2;
        double precision = 0.1;
        X2 fx2 = new X2();

        arguments = new X2Arguments(10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        arguments = (X2Arguments)calculator.calculateOneArgumentExtremum(fx2, a1, a2, 0, precision);
        assertEquals(0, arguments.getValues()[0], precision);

        arguments = new X2Arguments(-10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        arguments = (X2Arguments)calculator.calculateOneArgumentExtremum(fx2, a1, a2, 0, precision);
        assertEquals(0, arguments.getValues()[0], precision);
    }

    public void testCalculateExtremum1() {
        AlternatingVariableDescentMethodImpl calculator = new AlternatingVariableDescentMethodImpl();
        X2Arguments arguments, a1, a2, a3;
        double precision = 0.1;
        X2 fx2 = new X2();

        arguments = new X2Arguments(10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Arguments)calculator.calculateExtremum(fx2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);

        arguments = new X2Arguments(-10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Arguments)calculator.calculateExtremum(fx2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);
    }

    public void testCalculateExtremum2() {
        AlternatingVariableDescentMethodImpl calculator = new AlternatingVariableDescentMethodImpl();
        X2Y2Arguments arguments, a1, a2, a3;
        double precision = 0.1;
        X2Y2 fx2y2 = new X2Y2();

        arguments = new X2Y2Arguments(10.5, 10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Y2Arguments)calculator.calculateExtremum(fx2y2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);
        assertEquals(0, arguments.getValues()[1], precision);

        arguments = new X2Y2Arguments(10.5, -10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Y2Arguments)calculator.calculateExtremum(fx2y2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);
        assertEquals(0, arguments.getValues()[1], precision);

        arguments = new X2Y2Arguments(-10.5, 10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Y2Arguments)calculator.calculateExtremum(fx2y2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);
        assertEquals(0, arguments.getValues()[1], precision);

        arguments = new X2Y2Arguments(-10.5, -10.5);
        a1 = arguments.clone();
        a2 = arguments.clone();
        a3 = arguments.clone();
        arguments = (X2Y2Arguments)calculator.calculateExtremum(fx2y2, a1, a2, a3, precision);
        assertEquals(0, arguments.getValues()[0], precision);
        assertEquals(0, arguments.getValues()[1], precision);
    }

}

class X2 extends Function {

    @Override
    public double calculate(Args args) {
        return args.values[0] * args.values[0];
    }

}

class X2Arguments extends Args {

    X2Arguments(double value) {
        super(new double[]{value});
    }

    public X2Arguments clone() {
        return (X2Arguments)super.clone();
    }

}

class X2Y2 extends Function {

    @Override
    public double calculate(Args args) {
        return args.values[0] * args.values[0] + args.values[1] * args.values[1];
    }

}

class X2Y2Arguments extends Args {

    X2Y2Arguments(double valuex, double valuey) {
        super(new double[]{valuex, valuey});
    }

    public X2Y2Arguments clone() {
        return (X2Y2Arguments)super.clone();
    }

}