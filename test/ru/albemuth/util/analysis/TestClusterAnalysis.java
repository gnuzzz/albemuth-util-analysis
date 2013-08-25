package ru.albemuth.util.analysis;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.junit.Assert.fail;

public class TestClusterAnalysis {

    @Test
    public void testNothing() {/* sdo nothing */}

    public void test5() {
        try {
            Args[] args = new Args[9 + 4*3 + 2*(11+13) + 2*(9+11)];
            int index = 0;

            //центральная область
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    args[index++] = new P(new double[]{i, j});
                }
            }

            //верхний соединитель
            for (int i = 2; i <= 4; i++) {
                args[index++] = new P(new double[]{0, i});
            }
            //левый соединитель
            for (int i = -4; i <= -2; i++) {
                args[index++] = new P(new double[]{i, 0});
            }
            //нижний соединитель
            for (int i = -4; i <= -2; i++) {
                args[index++] = new P(new double[]{0, i});
            }
            //правый соединитель
            for (int i = 2; i <= 4; i++) {
                args[index++] = new P(new double[]{i, 0});
            }

            //верхняя граница
            for (int i = -6; i <= 6; i++) {
                args[index++] = new P(new double[]{i, 6});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{i, 5});
            }
            //левая граница
            for (int i = -4; i <= 4; i++) {
                args[index++] = new P(new double[]{-5, i});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{-6, i});
            }
            //нижняя граница
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{i, -5});
            }
            for (int i = -6; i <= 6; i++) {
                args[index++] = new P(new double[]{i, -6});
            }
            //правая граница
            for (int i = -4; i <= 4; i++) {
                args[index++] = new P(new double[]{5, i});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{6, i});
            }

            /*for (Args a: args) {
                System.out.println(a + ": " + ClusterAnalysis.pointsDensityForPoint(a, Arrays.asList(args)));
            }*/
            double d = 0;
            List<Args> points = Arrays.asList(args);
            int n = 10000;
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                d += ClusterAnalysis.pointsDensityForPoint(args[0], points);
            }
            System.out.println(d + ": " + ((System.currentTimeMillis() - t1) /(double) n));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test4() {
        try {
            Args[] args = new Args[9 + 4*3 + 2*(11+13) + 2*(9+11)];
            int index = 0;

            //центральная область
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    args[index++] = new P(new double[]{i, j});
                }
            }

            //верхний соединитель
            for (int i = 2; i <= 4; i++) {
                args[index++] = new P(new double[]{0, i});
            }
            //левый соединитель
            for (int i = -4; i <= -2; i++) {
                args[index++] = new P(new double[]{i, 0});
            }
            //нижний соединитель
            for (int i = -4; i <= -2; i++) {
                args[index++] = new P(new double[]{0, i});
            }
            //правый соединитель
            for (int i = 2; i <= 4; i++) {
                args[index++] = new P(new double[]{i, 0});
            }

            //верхняя граница
            for (int i = -6; i <= 6; i++) {
                args[index++] = new P(new double[]{i, 6});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{i, 5});
            }
            //левая граница
            for (int i = -4; i <= 4; i++) {
                args[index++] = new P(new double[]{-5, i});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{-6, i});
            }
            //нижняя граница
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{i, -5});
            }
            for (int i = -6; i <= 6; i++) {
                args[index++] = new P(new double[]{i, -6});
            }
            //правая граница
            for (int i = -4; i <= 4; i++) {
                args[index++] = new P(new double[]{5, i});
            }
            for (int i = -5; i <= 5; i++) {
                args[index++] = new P(new double[]{6, i});
            }

            ClusterAnalysis ca = new ClusterAnalysis();
            Cluster root = ca.evaluateClusters(Arrays.asList(args), 0.25);
            printClusters(root);
            showResults(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test3() {
        try {
            Args[] args = Args.createArgs(new double[][]{
                    {1, 0},
                    {1, 1},
                    {1, 2.2},
                    {-0.44, 2.2},
                    {-2.168, 2.2},
                    {-4.2416, 2.2},
                    {-4.2416, -0.28832},

                    {-1, 0},
                    {-1, -1},
                    {-1, -2.2},
                    {0.44, -2.2},
                    {2.168, -2.2},
                    {4.2416, -2.2},
                    {4.2416, 0.28832}
            });

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

            ClusterAnalysis ca = new ClusterAnalysis();
            Cluster root = ca.evaluateClusters(Arrays.asList(args), 0.25);
            printClusters(root);
            showResults(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test2() {
        try {
            ClusterAnalysis ca = new ClusterAnalysis();
            Cluster root = ca.evaluateClusters(Arrays.asList(
                    new P(new double[]{0}),
                    new P(new double[]{1}),
                    new P(new double[]{10}),
                    new P(new double[]{11}),
                    new P(new double[]{20}),
                    new P(new double[]{21})
            ), 0.1);
            printClusters(root);
            showResults(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void test1() {
        try {
            ClusterAnalysis ca = new ClusterAnalysis();
            Cluster root = ca.evaluateClusters(Arrays.asList(
                    new P(new double[]{0}),
                    new P(new double[]{1}),
                    new P(new double[]{10}),
                    new P(new double[]{11})
            ), 0.1);
            printClusters(root);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private static void printClusters(ClusterChild... clusters) {
        for (ClusterChild cluster: clusters) {
            for (ClusterChild cc: cluster.getChildren()) {
                System.out.println(cluster + ": " + cc);
            }
        }
        for (ClusterChild cluster: clusters) {
            printClusters(cluster.getChildren().toArray(new ClusterChild[0]));
        }
    }

    static class P extends Args {
        P(double[] values) {
            super(values);
        }
        public double calculateFunctionValue() {
            return 0;
        }
    }

    private void showResults(Cluster root) {
        Set<Cluster> pointsParents = new HashSet<Cluster>();
        for (Point point: root.getPoints()) {
            pointsParents.add(point.getParent());
        }
        Args[][] series = new Args[pointsParents.size()][];
        int serieIndex = 0;
        for (Cluster cluster: pointsParents) {
            List<Point> points = cluster.getPoints();
            for (Iterator<Point> it = points.iterator(); it.hasNext(); ) {
                Point point = it.next();
                if (!point.getParent().equals(cluster)) {
                    it.remove();
                }
            }
            series[serieIndex] = new Args[points.size()];
            for (int i = 0; i < points.size(); i++) {
                series[serieIndex][i] = points.get(i).getArgs();
            }
            serieIndex++;
        }
        Graph graph = new Graph( "graph", "graph", series);
        graph.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                synchronized (TestClusterAnalysis.this) {
                    TestClusterAnalysis.this.notify();
                }
            }
        });
        graph.pack();
        graph.setVisible(true);
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                fail();
            }
        }
    }

}
