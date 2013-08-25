package ru.albemuth.util.analysis;

import org.junit.Test;
import ru.albemuth.util.Configuration;
import ru.albemuth.util.ResultsStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestClusterAnalysisKMeansImpl {

    @Test
    public void testSimpleSeries() {
        try {
            List<Args> argsList = Arrays.asList(Args.createArgs(new double[][] {
                    {1, 1}, {2, 2}, {3, 3}, {4, 4}
            }));
            Cluster cluster;
            List<Point> points;
            ClusterAnalyserKMeansImpl ca;
            Properties props;


            points = new ArrayList<Point>();
            for (Args args: argsList) {
                points.add(new Point(args));
            }
            ca = new ClusterAnalyserKMeansImpl();
            props = new Properties();
            props.put(ClusterAnalyserKMeansImpl.PROPERTY_NAME_INCLUDE_SINGLE_POINT_CLUSTERS, "true");
            ca.configure(new Configuration(props));

            cluster = ca.analyse(points);
            assertEquals(5, cluster.getId());
            assertEquals(4, cluster.getChildren().size());
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);
                assertEquals(i + 1, p.getParent().getId());
            }


            points = new ArrayList<Point>();
            for (Args args: argsList) {
                points.add(new Point(args));
            }
            ca = ClusterAnalyserKMeansImpl.createAnalyser();

            cluster = ca.analyse(points);
            assertEquals(1, cluster.getId());
            assertEquals(4, cluster.getChildren().size());
            for (Point p : points) {
                assertEquals(1, p.getParent().getId());
            }

            ca = ClusterAnalyserKMeansImpl.createAnalyser();
            cluster = ca.analyse(Arrays.asList(
                    new Point(0),
                    new Point(1),
                    new Point(10),
                    new Point(11)
            ));
            assertEquals(3, cluster.getId());
            assertEquals(2, cluster.getChildren().size());
            assertEquals(2, cluster.getChildren().get(0).getChildren().size());
            assertEquals(0, ((Point)cluster.getChildren().get(0).getChildren().get(0)).getArgs().getValues()[0], 0.1);
            assertEquals(1, ((Point)cluster.getChildren().get(0).getChildren().get(1)).getArgs().getValues()[0], 0.1);
            assertEquals(2, cluster.getChildren().get(1).getChildren().size());
            assertEquals(10, ((Point)cluster.getChildren().get(1).getChildren().get(0)).getArgs().getValues()[0], 0.1);
            assertEquals(11, ((Point)cluster.getChildren().get(1).getChildren().get(1)).getArgs().getValues()[0], 0.1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResolveMatrixValues() {
        try {
            double[][] matrix = {
                    {0, 1, 1.5},
                    {0.11, 2, 0.5},
                    {3, 2, 1}
            };
            double[] values = ClusterAnalyserKMeansImpl.resolveMatrixValues(matrix, true);
            assertArrayEquals(new double[]{3, 2, 1.5, 1, 0.5, 0.11, 0}, values, 0.001);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test1() {
        try {
            ClusterAnalyserKMeansImpl analyser = ClusterAnalyserKMeansImpl.createAnalyser();
            Cluster root = analyser.analyse(Arrays.asList(
                    new Point(0),
                    new Point(1),
                    new Point(10),
                    new Point(11)
            ));
            ClusterUtil.printClusters(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void test2() {
        try {
            ClusterAnalyserKMeansImpl analyser = ClusterAnalyserKMeansImpl.createAnalyser();
            Cluster root = analyser.analyse(Arrays.asList(
                    new Point(0),
                    new Point(1),
                    new Point(10),
                    new Point(11),
                    new Point(20),
                    new Point(21)
            ));
            ClusterUtil.printClusters(root);
            ClusterUtil.showResults(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void test3() {
        try {
            List<Point> points = Arrays.asList(
                    new Point(1, 0),
                    new Point(1, 1),
                    new Point(1, 2.2),
                    new Point(-0.44, 2.2),
                    new Point(-2.168, 2.2),
                    new Point(-4.2416, 2.2),
                    new Point(-4.2416, -0.28832),

                    new Point(-1, 0),
                    new Point(-1, -1),
                    new Point(-1, -2.2),
                    new Point(0.44, -2.2),
                    new Point(2.168, -2.2),
                    new Point(4.2416, -2.2),
                    new Point(4.2416, 0.28832)
            );

            /*Args[] args = {
                    new P(new double[]{1, 0}),
                    new P(new double[]{1, 1}),
                    new P(new double[]{1, 2.2}),
                    new P(new double[]{-0.44, 2.2}),
                    new P(new double[]{-2.168, 2.2}),
                    new P(new double[]{-4.2416, 2.2}),
                    new P(new double[]{-4.2416, -0.28832}),

                    new P(new double[]{-1, 0}),
                    new P(new double[]{-1, -1}),
                    new P(new double[]{-1, -2.2}),
                    new P(new double[]{0.44, -2.2}),
                    new P(new double[]{2.168, -2.2}),
                    new P(new double[]{4.2416, -2.2}),
                    new P(new double[]{4.2416, 0.28832})
            };*/

            ClusterAnalyserKMeansImpl analyser = ClusterAnalyserKMeansImpl.createAnalyser();
            Cluster root = analyser.analyse(points);
            ClusterUtil.printClusters(root);
            ClusterUtil.showResults(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void test4() {
        try {
            Point[] points = new Point[9 + 4*3 + 2*(11+13) + 2*(9+11)];
            int index = 0;

            //центральная область
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    points[index++] = new Point(i, j);
                }
            }

            //верхний соединитель
            for (int i = 2; i <= 4; i++) {
                points[index++] = new Point(0, i);
            }
            //левый соединитель
            for (int i = -4; i <= -2; i++) {
                points[index++] = new Point(i, 0);
            }
            //нижний соединитель
            for (int i = -4; i <= -2; i++) {
                points[index++] = new Point(0, i);
            }
            //правый соединитель
            for (int i = 2; i <= 4; i++) {
                points[index++] = new Point(i, 0);
            }

            //верхняя граница
            for (int i = -6; i <= 6; i++) {
                points[index++] = new Point(i, 6);
            }
            for (int i = -5; i <= 5; i++) {
                points[index++] = new Point(i, 5);
            }
            //левая граница
            for (int i = -4; i <= 4; i++) {
                points[index++] = new Point(-5, i);
            }
            for (int i = -5; i <= 5; i++) {
                points[index++] = new Point(-6, i);
            }
            //нижняя граница
            for (int i = -5; i <= 5; i++) {
                points[index++] = new Point(i, -5);
            }
            for (int i = -6; i <= 6; i++) {
                points[index++] = new Point(i, -6);
            }
            //правая граница
            for (int i = -4; i <= 4; i++) {
                points[index++] = new Point(5, i);
            }
            for (int i = -5; i <= 5; i++) {
                points[index++] = new Point(6, i);
            }

            ClusterAnalyserKMeansImpl analyser = ClusterAnalyserKMeansImpl.createAnalyser();
            Cluster root = analyser.analyse(Arrays.asList(points));
            ClusterUtil.printClusters(root);
            ClusterUtil.showResults(root, 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void test111() {
        try {
            {
                Cluster c1 = new Cluster(1);
                c1.addPoint(new Point(1), 0);
                c1.addPoint(new Point(2), 0);
                Cluster c2 = new Cluster(2);
                c2.addPoint(new Point(3), 0);
                c2.addPoint(new Point(4), 0);
                Cluster c3 = new Cluster(3);
                c1.setParent(c3);
                c2.setParent(c3);
                ClusterUtil.showResults(c3, 0);
                ClusterUtil.printClusters(c3, 1);
                ClusterUtil.showResults(c3, 1);
                ClusterUtil.showResults(c3, 2);
            }

            {
                Cluster c1 = new Cluster(1);
                c1.addPoint(new Point(1), 0);
                c1.addPoint(new Point(2), 0);
                Cluster c2 = new Cluster(2);
                c2.addPoint(new Point(3), 0);
                c2.addPoint(new Point(4), 0);
                c1.setParent(c2);
                ClusterUtil.showResults(c2, 0);
                ClusterUtil.showResults(c2, 1);
                ClusterUtil.showResults(c2, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //@Test
    public void test222() {
        try {
            Cluster c1 = new Cluster(1);
            c1.addPoint(new Point(1), 0);
            c1.addPoint(new Point(2), 0);
            Cluster c2 = new Cluster(2);
            c2.addPoint(new Point(3), 0);
            c2.addPoint(new Point(4), 0);
            Cluster c3 = new Cluster(3);
            c1.setParent(c3);
            c2.setParent(c3);

            ResultsStorage rs = new ResultsStorage("D:/Temp/results.obj");
            rs.store(c3);
            Cluster c4 = rs.load();
            ClusterUtil.showResults(c3, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
