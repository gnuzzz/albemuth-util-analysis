package ru.albemuth.util.analysis;

public interface ExtremumCalculator {

    public static final int ITERATIONS_NO_LIMIT                             = -1;
    public static final int INDEX_PRECISION                                 = 0;

    public Args calculateExtremum(Function function, Args start, Args options, int maxIterationsNumber) throws CannotFoundExtremumException;

    public Args calculateExtremum(Function function, Args start, Args options) throws CannotFoundExtremumException;

}
