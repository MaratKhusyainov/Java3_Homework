//1. Написать метод, который меняет два элемента массива местами (массив может быть любого ссылочного типа);
//2. Написать метод, который преобразует массив в ArrayList;

package Hometask1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenArray<T> {
    private T[] arr;

    GenArray(){ }

    public void swapElements(T[] arr, T a, T b){
        int indexA = Arrays.asList(arr).indexOf(a);
        int indexB = Arrays.asList(arr).indexOf(b);
        arr[indexA] = b;
        arr[indexB] = a;
    }
    public void swapElemWithIndex(T[] arr, int indexA, int indexB){
        T temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }
    public List<T> toArrayList(T[] array){
        List<T> arrayList = new ArrayList<>();
        for (int i = 0; i <array.length ; i++) {
            arrayList.add(array[i]);
        }
        return arrayList;
    }

}
