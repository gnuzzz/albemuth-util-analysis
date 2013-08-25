package ru.albemuth.util.analysis;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vovan
 * Date: 17.08.2007
 * Time: 19:26:32
 */
public class Levenstein {

    private static ThreadLocal<int[][][]> table = new ThreadLocal<int[][][]>();
    private static ThreadLocal<int[][]> table1 = new ThreadLocal<int[][]>();
    private static ThreadLocal<int[][]> table2 = new ThreadLocal<int[][]>();

    public static <T> double minDifference(Collection<? extends T> coll1, Collection<? extends T> coll2) {
        return Math.abs(coll1.size() - coll2.size());
    }

    public static <T> double maxDifference(Collection<? extends T> coll1, Collection<? extends T> coll2) {
        return Math.sqrt(coll1.size() * coll1.size() + coll2.size() * coll2.size());
    }

    public static <T> double difference(Set<? extends T> set1, Set<? extends T> set2) {
        Set<T> s1 = new HashSet<T>(set1);
        Set<T> s2 = new HashSet<T>(set2);
        double s1size = s1.size();
        s1.retainAll(s2);
        return Math.sqrt((s1size - s1.size()) * (s1size - s1.size()) + (s2.size() - s1.size()) * (s2.size() - s1.size()));
    }

    public static <T> double relativeDifference(Set<? extends T> set1, Set<? extends T> set2) {
        return relativeDifference(set1, set2, difference(set1, set2));
    }

    public static <T> double relativeDifference(Set<? extends T> set1, Set<? extends T> set2, double difference) {
        return difference / maxDifference(set1, set2);
    }

    public static <T> double difference(List<? extends T> list1, List<? extends T> list2) {
        Set<T> itemsSet = new HashSet<T>(list1);
        itemsSet.addAll(list2);
        if (itemsSet.size() > 65536) {
            throw new IndexOutOfBoundsException("List parameters contains more than 65536 different objects");
        }
        HashMap<T, Character> itemsMap = new HashMap<T, Character>();
        Iterator<T> it = itemsSet.iterator();
        for (char c = 0; it.hasNext(); c++) {
            itemsMap.put(it.next(), c);
        }
        return difference(createStringFromList(list1, itemsMap), createStringFromList(list2, itemsMap));
    }

    protected static <T> String createStringFromList(List<? extends T> list, Map<T, Character> itemsMap) {
        StringBuffer sb = new StringBuffer();
        for (T t: list) {
            sb.append(itemsMap.get(t));
        }
        return sb.toString();
    }

    public static <T> double relativeDifference(List<? extends T> list1, List<? extends T> list2) {
        return relativeDifference(list1, list2, difference(list1, list2));
    }

    public static <T> double relativeDifference(List<? extends T> list1, List<? extends T> list2, double difference) {
        return difference / maxDifference(list1, list2);
    }

    public static double difference(String s1, String s2) {
        return directedDifference(s1, s2);
    }

    protected static double directedDifference(String s1, String s2) {
        //return Math.sqrt(length(levensteinVector(s1, s2)));
        return Math.sqrt(length(levensteinVectorFast(s1, s2)));
        //return Math.sqrt(length(levensteinVectorQuick(s1, s2)));
    }

    public static double minDifference(String s1, String s2) {
        return Math.abs(s1.length() - s2.length());
    }

    public static double maxDifference(String s1, String s2) {
        return Math.sqrt(s1.length() * s1.length() + s2.length() * s2.length());
    }

    public static double relativeDifference(String s1, String s2) {
        return relativeDifference(s1, s2, difference(s1, s2));
    }

    public static double relativeDifference(String s1, String s2, double difference) {
        return difference / maxDifference(s1, s2);
    }

    protected static int[] levensteinVectorQuick(String s1, String s2) {
        initArrays2(s2.length());
        int[][] tabi_1 = table1.get();
        int[][] tabi = table2.get();

        for(int j = 0; j <= s2.length(); j++ ) {
            tabi_1[j][0] = 0;
            tabi_1[j][1] = j;
        }

        int tabi_1j_10,tabi_1j_11,tabi_1j0,tabi_1j1,tabij_10,tabij_11,tabij0,tabij1;
        int ins0, ins1, del0, del1, upd0, upd1;
        int insLength, delLength, updLength;

        for(int i = 1; i <= s1.length(); i++ ) {
            tabi[0][0] = i;
            tabi[0][1] = 0;
            tabi_1j_10 = tabi_1[0][0];
            tabi_1j_11 = tabi_1[0][1];
            tabij_10 = tabi[0][0];
            tabij_11 = tabi[0][1];
            for(int j = 1; j <= s2.length(); j++ ) {
                tabi_1j0 = tabi_1[j][0];
                tabi_1j1 = tabi_1[j][1];

                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    upd0 = tabi_1j_10;
                    upd1 = tabi_1j_11;
                } else {
                    upd0 = tabi_1j_10 + 1;
                    upd1 = tabi_1j_11 + 1;
                }

                ins0 = tabi_1j0 + 1;
                ins1 = tabi_1j1;
                del0 = tabij_10;
                del1 = tabij_11 + 1;

                insLength = ins0 * ins0 + ins1 * ins1;
                delLength = del0 * del0 + del1 * del1;
                updLength = upd0 * upd0 + upd1 * upd1;

                if (insLength < delLength && insLength < updLength) {
                    tabij0 = ins0;
                    tabij1 = ins1;
                } else if (delLength < updLength) {
                    tabij0 = del0;
                    tabij1 = del1;
                } else {
                    tabij0 = upd0;
                    tabij1 = upd1;
                }

                tabi[j][0] = tabij0;
                tabi[j][1] = tabij1;
                tabi_1j_10 = tabi_1j0;
                tabi_1j_11 = tabi_1j1;
                tabij_10 = tabij0;
                tabij_11 = tabij1;
            }
            int[][] tmp = tabi_1;
            tabi_1 = tabi;
            tabi = tmp;
        }

        return tabi_1[s2.length()];
    }

    protected static int[] levensteinVectorFast(String s1, String s2) {
        initArrays(s1, s2);
        int[][][] tab = table.get();
        for(int i = 0; i <= s1.length(); i++ ) {
            tab[i][0][0] = i;
        }
        for(int j = 0; j <= s2.length(); j++ ) {
            tab[0][j][1] = j;
        }

        int tabi_1j_10,tabi_1j_11,tabi_1j0,tabi_1j1,tabij_10,tabij_11,tabij0 = 0,tabij1 = 0;
        int ins0, ins1, del0, del1, upd0, upd1;
        int insLength, delLength, updLength;

        for(int i = 1; i <= s1.length(); i++ ) {
            tabi_1j_10 = tab[i - 1][0][0];
            tabi_1j_11 = tab[i - 1][0][1];
            tabij_10 = tab[i][0][0];
            tabij_11 = tab[i][0][1];
            for(int j = 1; j <= s2.length(); j++ ) {
                tabi_1j0 = tab[i - 1][j][0];
                tabi_1j1 = tab[i - 1][j][1];

                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    upd0 = tabi_1j_10;
                    upd1 = tabi_1j_11;
                } else {
                    upd0 = tabi_1j_10 + 1;
                    upd1 = tabi_1j_11 + 1;
                }

                ins0 = tabi_1j0 + 1;
                ins1 = tabi_1j1;
                del0 = tabij_10;
                del1 = tabij_11 + 1;

                insLength = ins0 * ins0 + ins1 * ins1;
                delLength = del0 * del0 + del1 * del1;
                updLength = upd0 * upd0 + upd1 * upd1;

                if (insLength < delLength && insLength < updLength) {
                    tabij0 = ins0;
                    tabij1 = ins1;
                } else if (delLength < updLength) {
                    tabij0 = del0;
                    tabij1 = del1;
                } else {
                    tabij0 = upd0;
                    tabij1 = upd1;
                }

                tab[i][j][0] = tabij0;
                tab[i][j][1] = tabij1;
                tabi_1j_10 = tabi_1j0;
                tabi_1j_11 = tabi_1j1;
                tabij_10 = tabij0;
                tabij_11 = tabij1;
            }
        }
        tab[s1.length()][s2.length()][0] = tabij0;
        tab[s1.length()][s2.length()][1] = tabij1;

        return tab[s1.length()][s2.length()];
    }

    protected static int[] levensteinVector(String s1, String s2) {
        initArrays(s1, s2);
        int[][][] tab = table.get();
        for(int i = 0; i <= s1.length(); i++ ) {
            tab[i][0][0] = i;
        }
        for(int j = 0; j <= s2.length(); j++ ) {
            tab[0][j][1] = j;
        }
        int ins0, ins1, del0, del1, upd0, upd1;


        int insLength, delLength, updLength;
        for(int i = 1; i <= s1.length(); i++ ) {
            for(int j = 1; j <= s2.length(); j++ ) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    upd0 = tab[i-1][j-1][0];
                    upd1 = tab[i-1][j-1][1];
                } else {
                    upd0 = tab[i-1][j-1][0] + 1;
                    upd1 = tab[i-1][j-1][1] + 1;
                }
                ins0 = tab[i-1][j][0] + 1;
                ins1 = tab[i-1][j][1];
                del0 = tab[i][j-1][0];
                del1 = tab[i][j-1][1] + 1;

                insLength = ins0 * ins0 + ins1 * ins1;
                delLength = del0 * del0 + del1 * del1;
                updLength = upd0 * upd0 + upd1 * upd1;

                if (insLength < delLength && insLength < updLength) {
                    tab[i][j][0] = ins0;
                    tab[i][j][1] = ins1;
                } else if (delLength < updLength) {
                    tab[i][j][0] = del0;
                    tab[i][j][1] = del1;
                } else {
                    tab[i][j][0] = upd0;
                    tab[i][j][1] = upd1;
                }
            }
        }
        return tab[s1.length()][s2.length()];
    }

    private static void initArrays(String s1, String s2) {
        if (table.get() == null) {
            table.set(new int[s1.length() + 1][s2.length() + 1][2]);
        } else if (table.get().length < s1.length() + 1 || table.get()[0].length < s2.length() + 1) {
            table.set(new int[s1.length() + 1][s2.length() + 1][2]);
        }
    }

    private static void initArrays2(int length2) {
        if (table1.get() == null) {
            table1.set(new int[length2 + 1][2]);
        } else if (table1.get().length < length2 + 1) {
            table1.set(new int[length2 + 1][2]);
        }
        if (table2.get() == null) {
            table2.set(new int[length2 + 1][2]);
        } else if (table2.get().length < length2 + 1) {
            table2.set(new int[length2 + 1][2]);
        }
    }

    public static int length(int[] vector) {
        int ret = 0;
        for (int i: vector) {
            ret += i * i;
        }
        return ret;
    }

    public static double length(double[] vector) {
        double ret = 0;
        for (double d: vector) {
            ret += d * d;
        }
        return ret;
    }

}