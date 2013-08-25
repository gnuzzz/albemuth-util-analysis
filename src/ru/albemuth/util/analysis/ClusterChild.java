package ru.albemuth.util.analysis;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public interface ClusterChild extends Serializable {

    public static final List<ClusterChild> EMPTY = new ArrayList<ClusterChild>();

    public Cluster getParent();

    public void setParent(Cluster parent);

    public List<ClusterChild> getChildren();

    public List<Point> getPoints();

}
