package ru.albemuth.util.analysis;

public class GradientDescentMethodImpl implements ExtremumCalculator {

    public static final int INDEX_EPSILON                           = 1;
    public static final double PHI                                  = 1.618;

    public Args calculateExtremum(Function function, Args start, Args options) throws CannotFoundExtremumException {
        return calculateExtremum(function, start, options, ITERATIONS_NO_LIMIT);
    }

    public Args calculateExtremum(Function function, Args start, Args options, int maxIterationsNumber) throws CannotFoundExtremumException {
        Args current = start.clone();
        Args next = start.clone();
        Args gradient = start.clone();
        Args buffer1 = current.clone();
        Args buffer2 = current.clone();
        Args buffer3 = current.clone();
        Args buffer4 = current.clone();
        double precision = options.getValues()[INDEX_PRECISION];
        double epsilon = options.getValues()[INDEX_EPSILON];
        int iterationNumber = 0;
        do {
            iterationNumber++;
            if (maxIterationsNumber != ITERATIONS_NO_LIMIT && iterationNumber > maxIterationsNumber) {
                throw new CannotFoundExtremumException(current, iterationNumber, "Can't find extremum - max iterations number reached");
            }

            current.swapValues(next);

            calculateGradient(function, current, epsilon, gradient);
            if (gradient.is0()) {
                break;
            }
            
            next = calculateNext(function, current, next, gradient, precision, buffer1, buffer2, buffer3, buffer4);
        } while (next.distanceFrom(current) > precision);
        return next;
    }

    protected void calculateGradient(Function function, Args args, double epsilon, Args result) {
        double arg;
        double f1, f2;
        double[] values = args.getValues();
        for (int i = 0; i < values.length; i++) {
            arg = values[i];

            values[i] = arg - epsilon;
            f1 = function.calculate(args);
            values[i] = arg + epsilon;
            f2 = function.calculate(args);

            result.getValues()[i] = (f2 - f1)/(2 * epsilon);
            values[i] = arg;
        }
    }

    protected Args calculateNext(Function function, Args current, Args next, Args gradient, double precision, Args buffer1, Args buffer2, Args buffer3, Args buffer4) {
        return calculateLineExtremum(function, current, next, gradient, precision, buffer1, buffer2, buffer3, buffer4);
    }

    protected Args calculateArgs(Function function, Args args, Args gradient, double lambda, Args result) {
        for (int i = 0; i < result.getValues().length; i++) {
            result.set(i, args.getValues()[i] - lambda * gradient.getValues()[i]);
        }
        return result;
    }

    protected Args calculateLineExtremum(Function function, Args current, Args extremum, Args gradient, double precision, Args buffer1, Args buffer2, Args buffer3, Args buffer4) {
        buffer1.change(current);
        findSegmentWithExtremum(function, gradient, precision, buffer1, buffer2, buffer3);
        Args result = findLineExtremumByGoldenSectionSearch(function, buffer1, buffer2, buffer3, buffer4, gradient, gradient.distanceFrom0() * PHI, precision);
        extremum.swapValues(result);
        return extremum;
    }

    protected void findSegmentWithExtremum(Function function, Args gradient, double precison, Args a, Args b, Args tmp) {
        double lambda = precison / gradient.distanceFrom0();
        double prevValue = function.calculate(a);
        tmp = calculateArgs(function, a, gradient, lambda, tmp);
        double currentValue = function.calculate(tmp);
        if (currentValue >= prevValue) {
            b.swapValues(tmp);
        } else {
            lambda *= 2;
            b = calculateArgs(function, tmp, gradient, lambda, b);
            double nextValue = function.calculate(b);
            for (; nextValue < currentValue; ) {
                a.swapValues(tmp);
                tmp.swapValues(b);
                currentValue = nextValue;
                lambda *= 2;
                b = calculateArgs(function, tmp, gradient, lambda, b);
                nextValue = function.calculate(b);
            }
        }
    }

    protected Args findLineExtremumByGoldenSectionSearch(Function function, Args a, Args b, Args x1, Args x2, final Args gradient, final double gradientLengthToPhi, final double precision) {
        double a2bLength = a.distanceFrom(b);
        x1 = calculateArgs(function, b, gradient, -a2bLength/gradientLengthToPhi, x1);
        x2 = calculateArgs(function, a, gradient, a2bLength/gradientLengthToPhi, x2);
        double y1 = function.calculate(x1);
        double y2 = function.calculate(x2);
        return findLineExtremumByGoldenSectionSearch(function, a, b, x1, y1, x2, y2, gradient, gradientLengthToPhi, precision, a2bLength);
    }

    protected Args findLineExtremumByGoldenSectionSearch(Function function, Args a, Args b, Args x1, double y1, Args x2, double y2, final Args gradient, final double gradientLengthToPhi, final double precision, double a2bLength) {
        /*
        if (a2bLength < precision) {
            return y2 < y1 ? x2 : x1;
        }
        if (y1 <= y2) {
            b.swapValues(x2);
            a2bLength = a.distanceFrom(b);
            x2.swapValues(x1);
            x1 = calculateArgs(b, gradient, -a2bLength/gradientLengthToPhi, x1);
            y2 = y1;
            y1 = x1.calculateFunctionValue();
        } else {
            a.swapValues(x1);
            a2bLength = a.distanceFrom(b);
            x1.swapValues(x2);
            x2 = calculateArgs(a, gradient, a2bLength/gradientLengthToPhi, x2);
            y1 = y2;
            y2 = x2.calculateFunctionValue();
        }
        return findLineExtremumByGoldenSectionSearch(a, b, x1, y1, x2, y2, gradient, gradientLengthToPhi, precision, a2bLength);
        */
        //Code above isn't used because javac (or HotSpot compiler) doesn't support tail recursion
        Args result;
        for ( ; ; ) {
            if (a2bLength < precision) {
                result = y2 < y1 ? x2 : x1;
                break;
            }
            if (y1 <= y2) {
                b.swapValues(x2);
                a2bLength = a.distanceFrom(b);
                x2.swapValues(x1);
                x1 = calculateArgs(function, b, gradient, -a2bLength/gradientLengthToPhi, x1);
                y2 = y1;
                y1 = function.calculate(x1);
            } else {
                a.swapValues(x1);
                a2bLength = a.distanceFrom(b);
                x1.swapValues(x2);
                x2 = calculateArgs(function, a, gradient, a2bLength/gradientLengthToPhi, x2);
                y1 = y2;
                y2 = function.calculate(x2);
            }
        }
        return result;
    }
    
}
