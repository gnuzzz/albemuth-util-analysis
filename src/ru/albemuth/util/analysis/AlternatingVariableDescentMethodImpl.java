package ru.albemuth.util.analysis;

public class AlternatingVariableDescentMethodImpl implements ExtremumCalculator {

    public Args calculateExtremum(Function function, Args initArguments, Args options) throws CannotFoundExtremumException {
        return calculateExtremum(function, initArguments, options, ITERATIONS_NO_LIMIT);
    }

    public Args calculateExtremum(Function function, Args initArguments, Args options, int maxIterationsNumber) throws CannotFoundExtremumException {
        Args currentArguments = initArguments.clone();
        Args nextArguments = initArguments.clone();
        Args nextArguments2 = initArguments.clone();
        return calculateExtremum(function, currentArguments, nextArguments, nextArguments2, options.getValues()[INDEX_PRECISION]);
    }

    protected Args calculateExtremum(Function function, Args currentArguments, Args nextArguments, Args nextArguments2, double precision) {
        do {
            currentArguments.change(nextArguments);
            for (int i = 0; i < currentArguments.getValues().length; i++) {
                nextArguments = calculateOneArgumentExtremum(function, nextArguments, nextArguments2, i, precision);
            }
        } while (nextArguments.distanceFrom(currentArguments) > precision);
        return nextArguments;
    }

    protected Args calculateOneArgumentExtremum(Function function, Args currentArguments, Args nextArguments, int argumentIndex, double precision) {
        double functionValueCurrent = function.calculate(currentArguments);
        double functionValueNext = function.calculate(nextArguments.change(currentArguments).change(argumentIndex, precision));
        double change = precision;
        if (functionValueNext < functionValueCurrent) {
            currentArguments.set(argumentIndex, nextArguments.getValues()[argumentIndex]);
            change = 4 * precision;
        } else {
            functionValueNext = function.calculate(nextArguments.change(argumentIndex, -2 * precision));
            if (functionValueNext < functionValueCurrent) {
                currentArguments.set(argumentIndex, nextArguments.getValues()[argumentIndex]);
                change = -4 * precision;
            }
        }
        while (Math.abs(change) > 0.5 * precision) {
            currentArguments = findOneArgumentExtremum(function, currentArguments, nextArguments, argumentIndex, change);
            change *= -0.5;
        }
        return currentArguments;
    }

    protected Args findOneArgumentExtremum(Function function, Args currentArguments, Args nextArguments, int argumentIndex, double change) {
        for (double functionValueCurrent = function.calculate(currentArguments), functionValueNext = function.calculate(nextArguments.change(currentArguments).change(argumentIndex, change)); functionValueNext < functionValueCurrent; functionValueCurrent = functionValueNext, functionValueNext = function.calculate(nextArguments.change(argumentIndex, change))) {
            currentArguments.set(argumentIndex, nextArguments.getValues()[argumentIndex]);
        }
        return currentArguments;
    }

}
