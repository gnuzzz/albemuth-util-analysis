package ru.albemuth.util.analysis;

import java.util.*;

public class ClusterAnalysis {

    public Cluster evaluateClusters(Collection<? extends Args> points, double distancePartLevel) {
        Cluster root = null;
        List<Point> undefinedPoints = getPoints(points);
        List<Point> definedPoints = new ArrayList<Point>();
        if (undefinedPoints.size() > 0) {
            Point firstPoint = undefinedPoints.get(0);
            undefinedPoints.remove(firstPoint);
            definedPoints.add(firstPoint);
            while (undefinedPoints.size() > 0) {
                Segment segment = findNextSegment(definedPoints, undefinedPoints);
                addSegment(segment, distancePartLevel);
                undefinedPoints.remove(segment.to);
                definedPoints.add(segment.to);
            }
            root = firstPoint.getParent();
        }
        for (; root != null && root.getParent() != null; root = root.getParent()) {}
        if (root != null) {normalize(root);}
        return root;
    }

    protected Segment findNextSegment(List<Point> definedPoints, List<Point> undefinedPoints) {
        Segment minSegment = null;
        for (Point from: definedPoints) {
            Segment segment = findMinSegment(from, undefinedPoints);
            if (minSegment == null || segment.length < minSegment.length) {
                minSegment = segment;
            }
        }
        return minSegment;
    }

    protected Segment findMinSegment(Point from, List<Point> points) {
        Segment segment = new Segment();
        segment.from = from;
        for (Point point: points) {
            double distance = from.getArgs().distanceFrom(point.getArgs());
            if (segment.to == null || distance < segment.length) {
                segment.to = point;
                segment.length = distance;
            }
        }
        return segment;
    }

    protected static List<Point> getPoints(Collection<? extends Args> points) {
        List<Point> clusterPoints = new ArrayList<Point>();
        for (Args point: points) {
            double[] values = new double[point.getValues().length + 1];
            System.arraycopy(point.getValues(), 0, values, 0, values.length - 1);
            values[values.length - 1] = pointsDensityForPoint(point, points);
            clusterPoints.add(new Point(new P(values)));
        }
        return clusterPoints;
    }

    protected void addSegment(Segment segment, double distancePartLevel) {
        Cluster pointCluster = segment.from.getParent();
        ClusterChild top = segment.from;
        for (; pointCluster != null; top = pointCluster, pointCluster = pointCluster.getParent()) {
            if (isPartOfCluster(pointCluster.getDistance(), distancePartLevel, segment.length)) {
                break;
            }
        }
        if (pointCluster == null) {
            Cluster cluster = Cluster.createCluster();
            top.setParent(cluster);
            pointCluster = cluster;
        } else if (!isPartOfSameLevelCluster(pointCluster.getDistance(), distancePartLevel, segment.length)) {
            Cluster cluster = Cluster.createCluster();
            top.setParent(cluster);
            cluster.setParent(pointCluster);
            pointCluster = cluster;
        }
        pointCluster.addPoint(segment.to, segment.length);
    }

    protected boolean isPartOfCluster(double average, double averagePartLevel, double distance) {
        return distance <= average * (1 + averagePartLevel);
    }

    protected boolean isPartOfSameLevelCluster(double average, double averagePartLevel, double distance) {
        //return average <= distance / (1 - averagePartLevel);
        return distance >= average * (1 - averagePartLevel);//this is equivalent to expression above, but 5 times faster
    }

    protected void normalize(Cluster cluster) {
        List<Point> points = new ArrayList<Point>();
        boolean hasSubclusters = false;
        for (ClusterChild child: cluster.getChildren()) {
            if (child instanceof Point) {
                points.add((Point) child);
            } else if (child instanceof Cluster) {
                hasSubclusters = true;
            }
        }
        Cluster pointsCluster = null;
        if (points.size() > 0 && hasSubclusters) {
            pointsCluster = Cluster.createCluster();
            for (Point point: points) {
                point.setParent(pointsCluster);
            }
            pointsCluster.setParent(cluster);
        }
        for (ClusterChild child: cluster.getChildren()) {
            if (child instanceof Cluster && !child.equals(pointsCluster)) {
                normalize((Cluster) child);
            }
        }
    }

    public static double pointsDensityForPoint(final Args point, Collection<? extends Args> points) {
        double closestPointDistance = closestDistance(point, points);
        Args min = point.clone();
        Args max = point.clone();
        for (int i = 0; i < point.getValues().length; i++) {
            min.set(i, point.getValues()[i] - 1.25 * closestPointDistance);
            max.set(i, point.getValues()[i] + 1.25 * closestPointDistance);
        }
        double density = 0;
        for (Args a: points) {
            if (isInArea(min, max, a)) {
                density++;
            }
        }
        density = density / Math.pow(4 * closestPointDistance, point.getValues().length);
        return density;
    }

    public static double closestDistance(Args point, Collection<? extends Args> points) {
        /*
        Метод должен вычислять радиус минимальной ограничивающей сферы
        http://ru.wikipedia.org/wiki/%D0%9E%D0%B3%D1%80%D0%B0%D0%BD%D0%B8%D1%87%D0%B8%D0%B2%D0%B0%D1%8E%D1%89%D0%B0%D1%8F_%D1%81%D1%84%D0%B5%D1%80%D0%B0
         */
        Args[] sortedPoints = getSortedPoints(point, points);
        HyperSphere sphere = new HyperSphere(point);
        for (int i = 1; !sphere.isDefined() || sortedPoints[i].distanceFrom(point) < 2*sphere.getRadius(); i++) {

        }

        for (int i = 1; !sphere.isDefined() && i < sortedPoints.length; i++) {
            if (sphere.canAddPoint(sortedPoints[i], sortedPoints)) {
                sphere.add(sortedPoints[i]);
            }
        }
        return sphere.getRadius();
        //for ()


        /*double distance = Double.MAX_VALUE;
        for (Args a: points) {
            if (!a.equals(point)) {
                double d = point.distanceFrom(a);
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return distance;*/
    }

    public static Args[] getSortedPoints(Args point, Collection<? extends Args> points) {
        Args[] sortedPoints = new Args[points.size()];
        Iterator<? extends Args> it = points.iterator();
        for (int i = 0; it.hasNext(); i++) {
            Args a = it.next();
            double[] tmp = new double[a.getValues().length + 1];
            System.arraycopy(a.getValues(), 0, tmp, 0, a.getValues().length);
            tmp[tmp.length - 1] = point.distanceFrom(a);
            sortedPoints[i] = new P(tmp);
        }
        Arrays.sort(sortedPoints, new Comparator<Args>() {
            public int compare(Args a1, Args a2) {
                int index = a1.values.length - 1;
                double diff = a1.values[index] - a2.values[index];
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return sortedPoints;
    }

    public static boolean isInArea(Args min, Args max, Args point) {
        boolean ret = true;
        for (int i = 0; i < point.getValues().length; i++) {
            if (point.getValues()[i] < min.getValues()[i] || max.getValues()[i] < point.getValues()[i]) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    static class P extends Args {
        P(double[] values) {
            super(values);
        }
        public double calculateFunctionValue() {
            return 0;
        }
    }

    static class HyperSphere {

        private Args point;
        private List<Args> otherPoints = new ArrayList<Args>();
        private boolean defined;
        private double radius = Double.MAX_VALUE;

        HyperSphere(Args point) {
            this.point = point;
        }

        public double getRadius() {
            return radius;
        }

        public boolean isDefined() {
            return defined;
        }

        public boolean canAddPoint(Args newPoint, Args[] points) {
            return false;//todo
        }

        public void add(Args newPoint) {
            otherPoints.add(newPoint);
            defined = calculateDefined(point, otherPoints);
            if (defined) {
                radius = calculateRadius(point, otherPoints);
            }
        }

        private boolean calculateDefined(Args point, List<Args> otherPoints){
            return false;//todo
        }

        private double calculateRadius(Args point, List<Args> otherPoints) {
            return 0;//todo
        }

    }

}
