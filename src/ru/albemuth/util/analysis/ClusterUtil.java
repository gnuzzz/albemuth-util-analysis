package ru.albemuth.util.analysis;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class ClusterUtil {

    private static final Logger LOG                         = Logger.getLogger(ClusterUtil.class);
    
    public static void printClusters(ClusterChild... clusters) {
        for (ClusterChild cluster: clusters) {
            for (ClusterChild cc: cluster.getChildren()) {
                LOG.info(cluster + ": " + cc);
            }
        }
        for (ClusterChild cluster: clusters) {
            printClusters(cluster.getChildren().toArray(new ClusterChild[0]));
        }
    }

    public static void printClusters(Cluster root, int level) {
        List<List<Point>> seriesList = new ArrayList<List<Point>>();
        if (level >= 0) {
            resolveSeries(seriesList, level, root);
        } else {
            resolveSeries(seriesList, root);
        }
        for (List<Point> series: seriesList) {
            String s = "series:";
            for (Point point: series) {
                s += "\n" + point;
            }
            LOG.info(s);
        }
    }

    public static void showResults(Cluster root) {
        showResults(root, -1);
    }

    public static void showResults(Cluster root, int level) {
        /*Set<Cluster> pointsParents = new HashSet<Cluster>();
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
        }*/
        List<List<Point>> seriesList = new ArrayList<List<Point>>();
        if (level >= 0) {
            resolveSeries(seriesList, level, root);
        } else {
            resolveSeries(seriesList, root);
        }
        Args[][] series = new Args[seriesList.size()][];
        int serieIndex = 0;
        for (List<Point> points: seriesList) {
            series[serieIndex] = new Args[points.size()];
            for (int i = 0; i < points.size(); i++) {
                series[serieIndex][i] = points.get(i).getArgs();
            }
            serieIndex++;
        }
        final Graph graph = new Graph( "graph", "graph", series);
        graph.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                synchronized (graph) {
                    graph.notify();
                }
            }
        });
        graph.pack();
        graph.setVisible(true);
        synchronized (graph) {
            try {
                graph.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resolveSeries(List<List<Point>> series, int level, Cluster cluster) {
        List<Point> points = null;
        if (level > 0) {
            for (ClusterChild child: cluster.getChildren()) {
                if (child instanceof Point) {
                    if (points == null) {points = new ArrayList<Point>();}
                    points.add((Point)child);
                } else if (child instanceof Cluster) {
                    resolveSeries(series, level - 1, (Cluster)child);
                }
            }
        } else {
            points = cluster.getPoints();
        }
        if (points != null && points.size() > 0) {series.add(points);}
    }

    public static void resolveSeries(List<List<Point>> series, Cluster cluster) {
        List<Point> points = null;
        for (ClusterChild child: cluster.getChildren()) {
            if (child instanceof Point) {
                if (points == null) {points = new ArrayList<Point>();}
                points.add((Point)child);
            } else if (child instanceof Cluster) {
                resolveSeries(series, (Cluster)child);
            }
        }
        if (points != null && points.size() > 0) {series.add(points);}
    }

    public static class Graph extends JFrame {

        public Graph(String title, String graphTitle, Args[][] args) {
            super(title);
            JFreeChart chart = createChart(new GraphDataSet(args), graphTitle);
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 800));
            setContentPane(chartPanel);
        }

        private static JFreeChart createChart(XYDataset xydataset, String graphTitle) {
            JFreeChart jfreechart = ChartFactory.createScatterPlot(graphTitle, "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
            XYPlot xyplot = (XYPlot)jfreechart.getPlot();
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairLockedOnData(true);
            xyplot.setRangeCrosshairVisible(true);
            xyplot.setRangeCrosshairLockedOnData(true);
            xyplot.setDomainZeroBaselineVisible(true);
            xyplot.setRangeZeroBaselineVisible(true);
            XYDotRenderer xydotrenderer = new XYDotRenderer();
            xydotrenderer.setDotWidth(5);
            xydotrenderer.setDotHeight(5);
            xyplot.setRenderer(xydotrenderer);
            NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
            numberaxis.setAutoRangeIncludesZero(false);
            return jfreechart;
        }

        public static class GraphDataSet extends AbstractXYZDataset implements XYZDataset {

            private Args[][] args;

            public GraphDataSet(Args[][] args) {
                this.args = args;
            }

            public int getSeriesCount() {
                return args.length;
            }

            public Comparable getSeriesKey(int i) {
                return "" + i;
            }

            public int getItemCount(int i) {
                return args[i].length;
            }

            public Number getX(int series, int item) {
                return args[series][item].getValues()[0];
            }

            public Number getY(int series, int item) {
                return args[series][item].getValues().length > 1 ? args[series][item].getValues()[1] : 0;
            }

            public Number getZ(int series, int item) {
                return 0;
            }

        }

    }

}