
//Задача:
//   Даны классы Fruit, Apple extends Fruit, Orange extends Fruit;
//   Класс Box, в который можно складывать фрукты. Коробки условно сортируются по типу фрукта,
//   поэтому в одну коробку нельзя сложить и яблоки, и апельсины;
//   Для хранения фруктов внутри коробки можно использовать ArrayList;
//   Сделать метод getWeight(), который высчитывает вес коробки. Задать вес одного фрукта и их количество:
//   вес яблока – 1.0f, апельсина – 1.5f (единицы измерения не важны);
//   Внутри класса Box сделать метод compare(), который позволяет сравнить текущую коробку с той,
//   которую подадут в compare() в качестве параметра. true – если их массы равны, false в противоположном случае.
//   Можно сравнивать коробки с яблоками и апельсинами;
//   Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую. Помним про сортировку фруктов:
//   нельзя яблоки высыпать в коробку с апельсинами. Соответственно, в текущей коробке фруктов не остается, а в другую
//   перекидываются объекты, которые были в первой;
//   Не забываем про метод добавления фрукта в коробку.

package Hometask1;

import java.util.ArrayList;

public class Box<T extends Fruit> {

    int fruitQuantity;
    ArrayList<T> fruitBox;
    T typeFruit;

    public Box(T typeFruit){
        fruitBox = new ArrayList<>();
        this.typeFruit = typeFruit;
    }

    public int getFruitQuantity(){
        return fruitBox.size();
    }

    public float getWeight(){
        fruitQuantity = fruitBox.size();
        return fruitQuantity * typeFruit.getW();
    }

    public boolean compare(Box<? extends Fruit> anotherBox){
        //Чтобы не столкнуться с ошибкой округления при сравнении двух дробный чисел, мы сравниваем в пределах дельты 0.0001
        return Math.abs(this.getWeight() - anotherBox.getWeight()) < 0.0001;
    }

    public void addFruit(int quantity){
        for (int i = 0; i < quantity; i++) {
            this.fruitBox.add(typeFruit);
        }
    }

    public void putInAnotheBox(Box<T> boxTo){
        boxTo.addFruit(this.getFruitQuantity());
        fruitBox.clear();

    }
}
