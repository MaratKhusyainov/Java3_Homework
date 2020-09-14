package Hometask4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main2 {
    public static void test() {
        Integer[] testArr = {4,9,7,8,1,7,3,2};
        String testString = "Получи!";
        String[] testWordArr = {"asd", "asdq", "def", "aqw", "qwevds"};

        System.out.println(Main2.getFirstDigit(7, testArr));
        System.out.println(Main2.getMax(testArr));
        System.out.println(Main2.reverseString(testString));
        System.out.println(Main2.getAverage(Arrays.asList(testArr)));
        System.out.println(String.join(" ", Main2.getStringAtBeginChar(Arrays.asList(testWordArr))));
    }


    private static GetFirstDigit gfd = (digit, array) -> {
        Integer myIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(digit)) {
                myIndex = i;
                break;
            }
        }
        return myIndex;
    };

    public static Integer getFirstDigit(Integer n, Integer[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        return gfd.search(n, arr);
    }

    public static Integer getMax(Integer[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        if (arr.length == 1) {
            return arr[0];
        }
        GetValue ti = Helper::getMax;
        return ti.test(arr);

    }

    public static String reverseString(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (str.length() == 1) {
            return str;
        }
        return Helper.reverse(1).doAction(str);
    }

    public static Double getAverage(List<Integer> list) {
        if (list == null) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0).doubleValue();
        }
        GenericInter<Integer> gi = (numberList) -> {
            Double sum = 0.00;
            for (Integer element : numberList) {
                sum += element;
            }
            List<Double> ld = new ArrayList<>(1);
            ld.add(sum / numberList.size());
            return ld;
        };
        return (Double) gi.getValue(list).get(0);
    }

    public static List<String> getStringAtBeginChar(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        GenericInter<String> gi = Helper::getStringList;
        return (List<String>) gi.getValue(list);
    }
}

class Helper {
    static Integer getMax(Integer[] arr) {
        Integer max = arr[0];
        for (Integer element : arr) {
            if (element > max) {
                max = element;
            }
        }
        return max;
    }

    static StringReverse reverse (int options) {
        switch (options) {
            case 1: {
                return str -> new StringBuilder(str).reverse().toString();
            }
            default: {
                return str -> {
                    char[] resultArr = new char[str.length()];
                    for (int i = str.length() - 1; i >= 0 ; i--) {
                        resultArr[str.length() - i - 1] = str.charAt(i);
                    }
                    return String.valueOf(resultArr);
                };
            }
        }
    }

    static List<String> getStringList(List<String> list) {
        List<String> result = new LinkedList<>();
        for (String element : list) {
            if (element.startsWith("a") && element.length() == 3) {
                result.add(element);
            }
        }
        return result;
    }
}
