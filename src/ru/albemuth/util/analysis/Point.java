package ru.albemuth.util.analysis;

import java.util.Arrays;
import java.util.List;

public class Point implements ClusterChild {

    private Args args;
    private Cluster parent;

    public Point(double... args) {
        this.args = new Args(args);
    }

    public Point(Args args) {
        this.args = args;
    }

    public Args getArgs() {
        return args;
    }

    public Cluster getParent() {
        return parent;
    }

    public void setParent(Cluster parent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;
        this.parent.getChildren().add(this);
    }

    public List<ClusterChild> getChildren() {
        return EMPTY;
    }

    public List<Point> getPoints() {
        return Arrays.asList(this);
    }

    public int hashCode() {
        return getArgs().hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof Point && ((Point)o).getArgs().equals(getArgs());
    }

    public String toString() {
        return getArgs().toString();
    }
}
