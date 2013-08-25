package ru.albemuth.util.analysis;

import org.junit.Test;
import ru.albemuth.util.RandomGenerator;

import java.awt.datatransfer.StringSelection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestHyperLogLog {

    @Test
    public void test() {
        try {
            //Preparing data
            String[] ss = new String[10000];
            for (int i = 0; i < ss.length; i++) {
                ss[i] = RandomGenerator.randomString(RandomGenerator.randomInt(1, 10));
            }
            String[] strings = new String[ss.length];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = ss[RandomGenerator.randomInt(0, ss.length)];
            }

            //calculating
            double stdError = 0.065;
            HyperLogLog hll = new HyperLogLog(stdError);
            for (String s: strings) {
                hll.offer(s);
            }

            Set<String> stringSet = new HashSet<String>();
            Collections.addAll(stringSet, strings);

            long cardinality = hll.cardinality();
            //assertTrue((Math.abs(stringSet.size() - cardinality)/(double)stringSet.size()) < stdError);//???

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
