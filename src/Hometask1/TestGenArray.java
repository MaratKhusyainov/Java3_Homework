package Hometask1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestGenArray {
    public static void main(String[] args) {
        Integer[] intArr = {1,2,3,4,5};
        String[] strArr = {"one", "two", "three"};
        System.out.println("First array 'intArr' : " + Arrays.toString(intArr));
        System.out.println("Second array 'strArr' : " + Arrays.toString(strArr));

        GenArray<Integer> intGenArr = new GenArray<>();
        intGenArr.swapElements(intArr,2,5);
        intGenArr.swapElemWithIndex(intArr,0,2);

        GenArray<String> stringGenArr = new GenArray<>();
        stringGenArr.swapElements(strArr,"one", "two");

        System.out.println("'intArr' after swapping some elements: " + Arrays.toString(intArr));
        System.out.println("'strArr' after swapping some elements: " + Arrays.toString(strArr));

        List<Integer> intArrayList =  intGenArr.toArrayList(intArr);
        System.out.println("ArayList from 'intArr' array: " + intArrayList.toString());

        List<String> arrayList =  stringGenArr.toArrayList(strArr);
        System.out.println("ArayList from 'strArr' array: " + arrayList.toString());
    }
}
