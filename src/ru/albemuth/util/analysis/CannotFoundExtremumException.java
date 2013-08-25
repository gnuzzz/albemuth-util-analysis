package ru.albemuth.util.analysis;

public class CannotFoundExtremumException extends Exception {

    private Args stopValue;
    private int stopIterationNumber;

    public CannotFoundExtremumException(Args stopValue, int stopIterationNumber, String s) {
        super(s);
        this.stopValue = stopValue;
        this.stopIterationNumber = stopIterationNumber;
    }

    public CannotFoundExtremumException(Args stopValue, int stopIterationNumber, String s, Throwable throwable) {
        super(s, throwable);
        this.stopValue = stopValue;
        this.stopIterationNumber = stopIterationNumber;
    }

    public CannotFoundExtremumException(Args stopValue, int stopIterationNumber, Throwable throwable) {
        super(throwable);this.stopValue = stopValue;
        this.stopIterationNumber = stopIterationNumber;
    }

    public Args getStopValue() {
        return stopValue;
    }

    public int getStopIterationNumber() {
        return stopIterationNumber;
    }

}
