package ru.albemuth.util.analysis;

import junit.framework.TestCase;
import org.junit.Test;
import ru.albemuth.util.RandomGenerator;
import ru.albemuth.util.Statistics;

public class TestSimple extends TestCase {


    public void test() {/* do nothing */}

    //@Test
    public void aatest() {
        try {

            //int N = 628;
            int N = 650;

            double[][] m1 = new double[N][];
            for (int i = 0; i < N; i++) {
                m1[i] = new double[N];
                for (int j = 0; j < N; j++) {
                    m1[i][j] = RandomGenerator.randomDouble(0, 1);
                }
            }

            /*double[][] m3 = m1;
            for (int i = 0; i < 1000; i++) {
                long t1 = System.currentTimeMillis();
                m3 = ClusterAnalyserKMeansImpl.matrixMultiplication(m3, m1);
                System.out.println(m3.length + ", " + (System.currentTimeMillis() - t1));
            }*/
            ClusterAnalyserKMeansImpl.DataConvertor convertor = new ClusterAnalyserKMeansImpl.DataConvertor(m1);
            int[][] m1Int = convertor.getIntData(m1);
            int[][] m3Int = m1Int;
            for (int i = 0; i < 1000; i++) {
                long t1 = System.currentTimeMillis();
                m3Int = ClusterAnalyserKMeansImpl.matrixMultiplication(m3Int, m1Int);
                System.out.println(m3Int.length + ", " + (System.currentTimeMillis() - t1));
            }
            /*double count = 0;
            long t1 = System.currentTimeMillis();
            double[][] c = new double[628][628];
            for (int i = 0; i < 628; i++) {
                for (int j = 0; j < 628; j++) {
                    double max = Double.MIN_VALUE;
                    //long max = Long.MIN_VALUE;
                    for (int k = 0; k < 628; k++) {
                        double min = 0;
                        //long min = 0;
                        if (min != 0 && max != 0 & min > max) {
                            max = min;
                            //count++;
                        }
                        count++;
                        c[i][j] = max;
                    }
                }
            }
            System.out.println((System.currentTimeMillis() - t1));*/
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
}
