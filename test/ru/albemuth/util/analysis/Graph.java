package ru.albemuth.util.analysis;

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

public class Graph extends JFrame {

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
