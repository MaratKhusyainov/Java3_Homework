//3. Написать метод, который проверяет состав массива из чисел 1 и 4.
// Если в нем нет хоть одной четверки или единицы, то метод вернет false;
// Написать набор тестов для этого метода (по 3-4 варианта входных данных).
// [ 1 1 1 4 4 1 4 4 ] -> true
// [ 1 1 1 1 1 1 ] -> false
// [ 4 4 4 4 ] -> false
// [ 1 4 4 1 1 4 3 ] -> false

package Hometask6;

public class Task3 {
    public boolean containsOnlyOneAndFour(int[] numbers){
        int countOne = 0;
        int countFour = 0;
        for (int number: numbers){
            if(number == 1) countOne++;
            if(number == 4) countFour++;
            if(number != 1 && number != 4) return false;
        }
        if(countOne == 0 || countFour == 0 ) return false;
        return true;
    }
}
