package ru.albemuth.util.analysis;

import ru.albemuth.util.Statistics;

import java.util.ArrayList;
import java.util.List;

public class Cluster implements ClusterChild {

    private static int clusterCounter = 0;

    private int id;
    private Cluster parent;
    private List<ClusterChild> children = new ArrayList<ClusterChild>();
    private transient Statistics pointsDistanceStatistics = new Statistics("points distance");

    public Cluster(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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
        if (this.pointsDistanceStatistics.getValuesNumber() > 0) {
            parent.pointsDistanceStatistics.init(this.pointsDistanceStatistics);
        }
    }

    public List<ClusterChild> getChildren() {
        return children;
    }

    public List<Point> getPoints() {
        List<Point> points = new ArrayList<Point>();
        for (ClusterChild child: getChildren()) {
            points.addAll(child.getPoints());
        }
        return points;
    }

    public void addPoint(Point point, double distance) {
        point.setParent(this);
        pointAdded(distance);
    }

    public void pointAdded(double distance) {
        pointsDistanceStatistics.addValue(distance);
        if (parent != null) {
            parent.pointAdded(distance);
        }
    }

    public double getDistance() {
        return pointsDistanceStatistics.getMax();
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(Object o) {
        return o instanceof Cluster && ((Cluster)o).getId() == getId();
    }

    public String toString() {
        return "" + id;
    }

    public static Cluster createCluster() {
        return new Cluster(++clusterCounter);
    }
    
}
