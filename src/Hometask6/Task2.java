// 2. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
// Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
// идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
// необходимо выбросить RuntimeException. Написать набор тестов для этого метода (по 3-4 варианта входных данных).
// Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].

package Hometask6;

import java.util.Arrays;

public class Task2 {
    public int[] getNumbersAfterLastFour(int[] numbers) {
        int startIndex;
        for (int i = numbers.length - 1; i >= 0; i--) {
            if (numbers[i] == 4) {
                startIndex = i + 1;
                return Arrays.copyOfRange(numbers, startIndex, numbers.length);
            }
            if (i == 0) {
                throw new RuntimeException("В массиве нет четвёрок.");
            }
        }
        if (numbers.length == 0) throw new RuntimeException("Массив пустой.");
        return null;
    }
}
