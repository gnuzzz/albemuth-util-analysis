package ru.albemuth.util.analysis;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestMath {

    @Test
    public void testBrootForce() {
        try {
            int[][] numbers = {
                    {1, 5, 6, 7},
                    {1, 5, 7, 6},
                    {1, 7, 5, 6},
                    {1, 7, 6, 5},
                    {1, 6, 5, 7},
                    {1, 6, 7, 5},
                    {5, 1, 6, 7},
                    {5, 1, 7, 6},
                    {5, 7, 1, 6},
                    {5, 7, 6, 1},
                    {5, 6, 1, 7},
                    {5, 6, 7, 1},
                    {6, 1, 5, 7},
                    {6, 1, 7, 5},
                    {6, 5, 1, 7},
                    {6, 5, 7, 1},
                    {6, 7, 1, 5},
                    {6, 7, 5, 1},
                    {7, 1, 5, 6},
                    {7, 1, 6, 5},
                    {7, 6, 1, 5},
                    {7, 6, 5, 1},
                    {7, 5, 1, 6},
                    {7, 5, 6, 1}
            };
            char[] ops = {'+', '-', '*', '/'};
            char[][] operations = new char[64][];
            int index = 0;
            for (char op0 : ops) {
                for (char op1 : ops) {
                    for (char op2 : ops) {
                        operations[index++] = new char[]{op0, op1, op2};
                    }
                }
            }


            /*int numbersNumber = 0;
            int operationsNumber = 1;
            printNumbers(numbers[numbersNumber]);
            printOperations(operations[operationsNumber]);
            System.out.println(calculate(numbers[numbersNumber], operations[operationsNumber]));*/

            for (int[] number : numbers) {
                for (char[] operation : operations) {
                    int result = calculate(number, operation);
                    printNumbers(number);
                    printOperations(operation);
                    System.out.println(result);
                    System.out.println("--------------");
                    /*if (result == 21) {
                        printNumbers(number);
                        printOperations(operation);
                        System.out.println("------------");
                    }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test() {
        try {
            int[] numbers = {1, 5, 6, 7};
            int[] combination = new int[numbers.length];
            int set = (1 << numbers.length) - 1;
            int limit = (1 << numbers.length);
            List<int[]> combinations = new ArrayList<int[]>();
            while (set < limit) {
                combinations.add(getCombination(numbers, combination, set));

                // Gosper's hack:
                int c = set & -set;
                int r = set + c;
                set = (((r^set) >>> 2) / c) | r;
            }

            for (int[] c: combinations) {
                printNumbers(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetCombination() {
        try {
            int[] numbers = {1, 5, 6, 7};
            int buf1[] = new int[1];
            printNumbers(getCombination(numbers, buf1, 1));
            printNumbers(getCombination(numbers, buf1, 2));
            printNumbers(getCombination(numbers, buf1, 4));
            printNumbers(getCombination(numbers, buf1, 8));
            int buf2[] = new int[2];
            printNumbers(getCombination(numbers, buf2, 3));
            printNumbers(getCombination(numbers, buf2, 5));
            printNumbers(getCombination(numbers, buf2, 6));
            printNumbers(getCombination(numbers, buf2, 9));
            printNumbers(getCombination(numbers, buf2, 10));
            printNumbers(getCombination(numbers, buf2, 12));
            int buf3[] = new int[3];
            int buf4[] = new int[4];
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private int[] getCombination(int[] numbers, int[] combination, int combinationBitSet) {
        for (int i = 0, index = 0; i < numbers.length; i++, combinationBitSet >>= 1) {
            if ((combinationBitSet & 1) != 0) {
                combination[index++] = numbers[i];
            }
        }
        return combination;
    }

    private void printNumbers(int[] numbers) {
        for (int i: numbers) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    private void printOperations(char[] operations) {
        for (char c: operations) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    public int calculate(int[] numbers, char[] operations) {
        assert numbers.length == operations.length + 1;
        double result = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            char operation = operations[i - 1];
            if ('+' == operation) {
                result += (double)numbers[i];
            } else if ('-' == operation) {
                result -= (double)numbers[i];
            } else if ('*' == operation) {
                result *= (double)numbers[i];
            } else if ('/' == operation) {
                result /= (double)numbers[i];
            }
        }
        return (int)result;
    }

}
