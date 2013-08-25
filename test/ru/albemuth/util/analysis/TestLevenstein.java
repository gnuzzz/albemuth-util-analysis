package ru.albemuth.util.analysis;

import junit.framework.TestCase;
import ru.albemuth.util.RandomGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestLevenstein extends TestCase {

    public void _test() {
        long t1, t2;

        String[] ss = new String[3000];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = i + "" + i + "" + i + "" + i;
        }

        double d = 0;
        t1 = System.currentTimeMillis();
        for (int i = 0; i < ss.length; i++) {
            for (int j = 0; j < ss.length; j++) {
                d += Levenstein.difference(ss[i], ss[j]);
            }
        }
        t2 = System.currentTimeMillis();
        System.out.println(d + ", " + (t2 - t1)+ ", " + (t2 - t1)/(double)(ss.length * ss.length));

//todo        
        /*d = 0;
        t1 = System.currentTimeMillis();
        for (int i = 0; i < ss.length; i++) {
            for (int j = 0; j < ss.length; j++) {
                d += Levenstein.fastDifference(ss[i], ss[j]);
            }
        }
        t2 = System.currentTimeMillis();
        System.out.println(d + ", " + (t2 - t1)+ ", " + (t2 - t1)/(double)(ss.length * ss.length));*/
    }

    public void testMaxDifferenceCollection() {
        List<String> cs1 = Arrays.asList("aaa", "bbb", "ccc");
        List<String> cs2 = Arrays.asList("ddd", "eee");
        List<String> cs3 = Arrays.asList("aaa", "bbb", "ccc");

        assertEquals(Math.sqrt(cs1.size() * cs1.size() + cs2.size() * cs2.size()), Levenstein.maxDifference(cs1, cs2), 0.001);
        assertEquals(Math.sqrt(cs1.size() * cs1.size() + cs3.size() * cs3.size()), Levenstein.maxDifference(cs1, cs3), 0.001);
    }

    public void testDifferenceSet() {
        Set<String> ss1 = new HashSet<String>(Arrays.asList("a", "b", "c", "d"));
        Set<String> ss2 = new HashSet<String>(Arrays.asList("e", "f", "g", "h", "i", "j", "k"));
        Set<String> ss3 = new HashSet<String>(Arrays.asList("a", "b", "c", "d"));
        Set<String> ss4 = new HashSet<String>(Arrays.asList("x", "b", "c", "y", "z"));

        assertEquals(Math.sqrt(4*4 + 7*7), Levenstein.difference(ss1, ss2), 0.001);
        assertEquals(0, Levenstein.difference(ss1, ss3), 0.001);
        assertEquals(Math.sqrt(2*2 + 3*3), Levenstein.difference(ss1, ss4), 0.001);
    }

    public void testDifferenceList() {
        List<String> ls1 = Arrays.asList("a", "b", "c", "d");
        List<String> ls2 = Arrays.asList("e", "f", "g", "h", "i", "j", "k");
        List<String> ls3 = Arrays.asList("a", "b", "c", "d");
        List<String> ls4 = Arrays.asList("a", "b");
        List<String> ls5 = Arrays.asList("a", "c");
        List<String> ls6 = Arrays.asList("a", "b", "c", "c");

        assertEquals(Math.sqrt(4*4 + 7*7), Levenstein.difference(ls1, ls2), 0.001);
        assertEquals(0, Levenstein.difference(ls1, ls3), 0.001);
        assertEquals(Math.sqrt(1*1 + 1*1), Levenstein.difference(ls4, ls5), 0.001);
        assertEquals(Math.sqrt(0*0 + 2*2), Levenstein.difference(ls4, ls6), 0.001);
    }

    public void testMaxDifferenceString() {
        String s1 = "abcd";
        String s2 = "efghijk";
        String s3 = "abcd";

        assertEquals(Math.sqrt(s1.length() * s1.length() + s2.length() * s2.length()), Levenstein.maxDifference(s1, s2), 0.001);
        assertEquals(Math.sqrt(s1.length() * s1.length() + s3.length() * s3.length()), Levenstein.maxDifference(s1, s3), 0.001);
    }

    public void testDifferenceString() {
        String s1 = "abcd";
        String s2 = "efghijk";
        String s3 = "abcd";
        String s4 = "ab";
        String s5 = "ac";
        String s6 = "abcc";

        assertEquals(0, Levenstein.difference("ab", "ab"), 0.001);
        assertEquals(0, Levenstein.difference(s1, s1), 0.001);
        assertEquals(Math.sqrt(4*4 + 7*7), Levenstein.difference(s1, s2), 0.001);
        assertEquals(Levenstein.difference(s1, s2), Levenstein.difference(s2, s1), 0.001);
        assertEquals(0, Levenstein.difference(s1, s3), 0.001);
        assertEquals(Levenstein.difference(s1, s3), Levenstein.difference(s3, s1), 0.001);
        assertEquals(Math.sqrt(1*1 + 1*1), Levenstein.difference(s4, s5), 0.001);
        assertEquals(Levenstein.difference(s4, s5), Levenstein.difference(s5, s4), 0.001);
        assertEquals(Math.sqrt(0*0 + 2*2), Levenstein.difference(s4, s6), 0.001);
        assertEquals(Levenstein.difference(s4, s6), Levenstein.difference(s6, s4), 0.001);
    }

    public void testDifferenceStringStress() {
        String[] ss = new String[250];
        for (int i = 0; i < ss.length; i++) {
            ss[i] = RandomGenerator.randomString(70);
        }

        double counter = 0;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < ss.length; i++) {
            for (int j = i + 1; j < ss.length; j++) {
                Levenstein.difference(ss[i], ss[j]);
            }
        }
        System.out.println((System.currentTimeMillis() - t1) + counter);

    }

}
