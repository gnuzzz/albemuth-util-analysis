package ru.albemuth.util.analysis;

import org.apache.log4j.Logger;
import ru.albemuth.util.*;

import java.util.*;

public class ClusterAnalyserKMeansImpl implements Configured, Closed {

    private static final Logger LOG                                             = Logger.getLogger(ClusterAnalyserKMeansImpl.class);

    public static final String PROPERTY_NAME_INCLUDE_SINGLE_POINT_CLUSTERS      = "include-single-point-clusters";

    private boolean includeSinglePointsClusters;
    private int clustersCreated = 0;

    public void configure(Configuration cfg) throws ConfigurationException {
        this.includeSinglePointsClusters = cfg.getBooleanValue(this, PROPERTY_NAME_INCLUDE_SINGLE_POINT_CLUSTERS, false);
    }

    public void close() throws CloseException {
        //do nothing
    }

    public Cluster analyse(List<Point> points) throws AnalysisException {
        double[][] m = distanceEstimateMatrix(points);
        double[][][] rtheta = relativeLikenessMeasureMatrix(m);
        double[][] theta = likenessMeasureMatrix(rtheta);

        double[][] r = theta;
        double[][] transposedTheta = transposeMatrix(theta);

        /*for (int q = 2; q < points.size(); q++) {
            r = matrixMultiplication(r, transposedTheta);
        }*/

        DataConvertor convertor = new DataConvertor(theta);
        int[][] rInt = convertor.getIntData(theta);
        int[][] transposedThetaInt = convertor.getIntData(transposedTheta);
        for (int q = 2; q < points.size(); q++) {
            rInt = matrixMultiplication(rInt, transposedThetaInt);
        }
        r = convertor.getDoubleData(rInt);

        return resolveClusters(r, points);
    }

    protected Cluster resolveClusters(double[][] r, List<Point> points) throws AnalysisException {
        List<KMeansPoint> ps = new ArrayList<KMeansPoint>(points.size());
        for (Point point: points) {
            KMeansPoint p = new KMeansPoint();
            p.point = point;
            ps.add(p);
        }

        double[] values = resolveMatrixValues(r, includeSinglePointsClusters);
        KMeansCluster cluster = null;
        for (double value: values) {//перебираем все уникальные значения матрицы отношений
            clearKMeanPointsFromPreviousCliusters(ps);
            for (int i = 0; i < r.length; i++) {//перебираем все точки множества
                KMeansPoint pointi = ps.get(i);
                for (int j = 0; j < r[i].length; j++) {//проверяем все точки на принадлежность к тому же кластеру, что и выбранная
                    if (r[i][j] >= value) {//если значение в матрице отношений превышает иди равно граничному, то точки i и j принадлежат к одному кластеру
                        KMeansPoint pointj = ps.get(j);
                        if (pointi.cluster != null && pointj.cluster != null) {
                            if (!pointi.cluster.equals(pointj.cluster)) {//у обеих точек есть кластеры и эти кластеры - разные - ошибка
                                throw new AnalysisException("Invalid state: point " + pointi + " point " + pointj + " should be in the same cluster, but it don't");
                            }
                        } else {
                            if (pointi.cluster != null && pointj.cluster == null) {//у точки i есть кластер, у точки j нет - добавляем j к кластер точки i
                                cluster = pointi.cluster;
                                addPointToCluster(cluster, pointj);
                            } else if (pointi.cluster == null && pointj.cluster == null) {//кластера пока нет ни у одной точки, создаем новый
                                cluster = createCluster(value);
                                addPointToCluster(cluster, pointi);
                                if (!pointi.equals(pointj)) {addPointToCluster(cluster, pointj);}//если точка та же самая, второй раз ее не добавляем
                            } else if (pointi.cluster == null && pointj.cluster != null) {//у точки i нет кластера, у точки j есть - добавляем i к кластер точки j
                                cluster = pointj.cluster;
                                addPointToCluster(cluster, pointi);
                            }
                        }
                    }
                }
            }
        }
        return cluster;
    }

    protected void addPointToCluster(KMeansCluster cluster, KMeansPoint point) {
        point.cluster = cluster;
        Cluster c = point.point.getParent();
        if (c == null) {
            cluster.addPoint(point.point, 0);//todo: distance
        } else {
            Cluster child = c;
            for (Cluster ch = c; ch != null; ch = ch.getParent()) {
                child = ch;
            }
            if (!child.equals(cluster)) {child.setParent(cluster);}
        }
    }

    protected void clearKMeanPointsFromPreviousCliusters(List<KMeansPoint> points) {
        for (KMeansPoint point: points) {
            point.cluster = null;
        }
    }

    protected KMeansCluster createCluster(double levelValue) {
        return new KMeansCluster(++clustersCreated, levelValue);
    }

    public static double[][] distanceEstimateMatrix(List<Point> points) {
        double[][] m = new double[points.size()][points.size()];
        for (int q = 0; q < points.size(); q++) {
            Point xq = points.get(q);
            double maxd = maxDistanceFromPoint(xq, points);
            for (int i = 0; i < points.size(); i++) {
                m[q][i] = 1 - xq.getArgs().distanceFrom(points.get(i).getArgs())/maxd;
            }
        }
        if (LOG.isDebugEnabled()) {LOG.debug("distanceEstimateMatrix done");}
        return m;
    }

    public static double maxDistanceFromPoint(Point point, List<Point> points) {
        double maxd = Double.MIN_VALUE;
        for (Point p: points) {
            double d = point.getArgs().distanceFrom(p.getArgs());
            if (d > maxd) {
                maxd = d;
            }
        }
        return maxd;
    }

    public static double[][][] relativeLikenessMeasureMatrix(double[][] dem) {
        int length = dem.length;
        double[][][] rlmm = new double[length][length][length];
        for (int q = 0; q < length; q++) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    rlmm[q][i][j] = 1 - Math.abs(dem[q][i] - dem[q][j]);
                }
            }
        }
        if (LOG.isDebugEnabled()) {LOG.debug("relativeLikenessMeasureMatrix done");}
        return rlmm;
    }

    public static double[][] likenessMeasureMatrix(double[][][] rlmm) {
        int length = rlmm.length;
        double[][] lmm = new double[length][length];
        for (int a = 0; a < length; a++) {
            for (int b = 0; b < length; b++) {
                double min = Double.MAX_VALUE;
                for (int q = 0; q < length; q++) {
                    if (rlmm[q][a][b] < min) {
                        min = rlmm[q][a][b];
                    }
                }
                lmm[a][b] = min;
            }
        }
        if (LOG.isDebugEnabled()) {LOG.debug("relativeLikenessMeasureMatrix done");}
        return lmm;
    }

    public static double[][] transposeMatrix(double[][] m) {
        int N = m.length;
        int M = m[0].length;
        double[][] transposed = new double[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                transposed[i][j] = m[j][i];
            }
        }
        return transposed;
    }

    public static double[][] matrixMultiplication(double[][] a, double[][] b) {
        int N = a.length;
        int M = b.length;
        int Q = a[0].length;
        double[][] c = new double[N][M];
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                double max = 0;//there are only positive values or zero
                for (int q = 0; q < Q; q++) {
                    //double min = Math.min(a[i][q], b[q][j]);
                    double min = Math.min(a[i][q], b[j][q]);
                    if (min > max) {
                        max = min;
                    }
                    //max = Math.max(max, Math.min(a[i][q], b[j][q]));
                }
                c[i][j] = max;
            }
        }
        if (LOG.isDebugEnabled()) {LOG.debug("matrixMultiplication double done: " + N + ", " + M + ", " + Q + ", " + (System.currentTimeMillis() - t1));}
        return c;
    }

    public static int[][] matrixMultiplication(int[][] a, int[][] b) {
        int N = a.length;
        int M = b.length;
        int Q = a[0].length;
        int[][] c = new int[N][M];
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                int max = 0;//there are only positive values or zero
                for (int q = 0; q < Q; q++) {
                    int min = Math.min(a[i][q], b[j][q]);
                    //int min = Math.min(a[i][q], b[q][j]);
                    if (min > max) {
                        max = min;
                    }
                    //max = Math.max(max, Math.min(a[i][q], b[j][q]));
                }
                c[i][j] = max;
            }
        }
        if (LOG.isDebugEnabled()) {LOG.debug("matrixMultiplication int done: " + N + ", " + M + ", " + Q + ", " + (System.currentTimeMillis() - t1));}
        return c;
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] line: matrix) {
            for (double d: line) {
                System.out.print(d + " ");
            }
            System.out.println();
        }
    }

    public static double[] resolveMatrixValues(double[][] matrix, boolean includeOnes) {
        Set<Double> valuesSet = new HashSet<Double>();
        for (double[] vs: matrix) {
            for (double d: vs) {
                if (includeOnes || d != 1) {
                    valuesSet.add(d);
                }
            }
        }
        double[] values = new double[valuesSet.size()];
        Iterator<Double> vit = valuesSet.iterator();
        for (int i = 0; vit.hasNext(); i++) {
            values[i] = vit.next();
        }
        Arrays.sort(values);
        double tmp;
        for (int i = 0; i < values.length/2; i++) {
            tmp = values[i];
            values[i] = values[values.length - i - 1];
            values[values.length - i - 1] = tmp;
        }
        return values;
    }

    public static ClusterAnalyserKMeansImpl createAnalyser() throws ConfigurationException {
        ClusterAnalyserKMeansImpl analyserKMeans = new ClusterAnalyserKMeansImpl();
        Properties props = new Properties();
        props.put(ClusterAnalyserKMeansImpl.PROPERTY_NAME_INCLUDE_SINGLE_POINT_CLUSTERS, "false");
        analyserKMeans.configure(new Configuration(props));
        return analyserKMeans;
    }

    static class KMeansPoint {
        Point point;
        KMeansCluster cluster;
    }

    static class KMeansCluster extends Cluster {

        private double level;

        KMeansCluster(int id, double level) {
            super(id);
            this.level = level;
        }

        public double getLevel() {
            return level;
        }

    }

    public static class DataConvertor {

        private double[] values;
        private Map<Double, Integer> valuesMap;

        public DataConvertor(double[][] data) {
            Set<Double> dataSet = new HashSet<Double>();
            for (double[] doubles: data) {
                for (double d: doubles) {
                    dataSet.add(d);
                }
            }
            ArrayList<Double> dataList = new ArrayList<Double>(dataSet);
            Collections.sort(dataList);
            values = new double[dataList.size()];
            valuesMap = new HashMap<Double, Integer>();
            int i = 0;
            for (Double d: dataList) {
                values[i] = d;
                valuesMap.put(d, i);
                i++;
            }
        }

        public int getIntValue(double value) {
            return valuesMap.get(value);
        }

        public double getDoubleValue(int value) {
            return values[value];
        }

        public int[][] getIntData(double[][] data) {
            int[][] ret = new int[data.length][data[0].length];//matrix is square!
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    ret[i][j] = getIntValue(data[i][j]);
                }
            }
            return ret;
        }

        public double[][] getDoubleData(int[][] data) {
            double[][] ret = new double[data.length][data[0].length];//matrix is square!
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    ret[i][j] = getDoubleValue(data[i][j]);
                }
            }
            return ret;
        }

    }

}
