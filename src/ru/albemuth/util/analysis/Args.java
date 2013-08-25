package ru.albemuth.util.analysis;

import java.io.Serializable;
import java.util.Arrays;

public class Args implements Cloneable, Serializable {

    protected double[] values;

    public Args(double... values) {
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }

    public Args change(int index, double change) {
        values[index] += change;
        return this;
    }

    public Args set(int index, double value) {
        values[index] = value;
        return this;
    }

    public Args change(Args arguments) {
        System.arraycopy(arguments.values, 0, values, 0, arguments.values.length);
        return this;
    }

    public void swapValues(Args arguments) {
        double[] tmp = arguments.values;
        arguments.values = values;
        values = tmp;
    }

    //public abstract double calculateFunctionValue();

    public double distanceFrom(Args arguments) {
        assert arguments.values.length == values.length;
        double distance = 0;
        for (int i = 0; i < values.length; i++) {
            distance += (values[i] - arguments.values[i]) * (values[i] - arguments.values[i]);
        }
        return Math.sqrt(distance);
    }

    public double distanceFrom0() {
        double distance = 0;
        for (double value: values) {
            distance += value * value;
        }
        return Math.sqrt(distance);
    }

    public boolean is0() {
        boolean ret = true;
        for (double d: values) {
            if (d != 0) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    public Args clone() {
        try {
            Args arguments = (Args)super.clone();
            arguments.values = values.clone();
            return arguments;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("super.clone of Args.clone throw CloneNotSupportedException, but it sholdn't", e);
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (double value : values) {
            h += 31 * h + value;
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Args && Arrays.equals(values, ((Args)obj).values);
    }

    public String toString() {
        StringBuilder view = new StringBuilder();
        view.append("[");
        for (double value: values) {
            if (view.length() > 0) {
                view.append("; ");
            }
            view.append(value);
        }
        view.append("]");
        return view.toString();
    }

    public static Args[] createArgs(double[]... values) {
        Args[] args = new Args[values.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = new Args(values[i]);
        }
        return args;
    }

}
