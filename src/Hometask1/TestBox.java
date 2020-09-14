package Hometask1;

public class TestBox {
    public static void main(String[] args) {

        Box<Orange> orangeBox = new Box<>(new Orange());
        Box<Apple> appleBox = new Box<>(new Apple());

        System.out.println("В коробке с апельсинами " +orangeBox.getFruitQuantity() + " фруктов.");
        orangeBox.addFruit(20);
        System.out.println("В коробке с апельсинами " +orangeBox.getFruitQuantity() + " фруктов.");
        System.out.println("Коробка с апельсинами весит: " + orangeBox.getWeight());

        System.out.println("В коробке с яблоками " +appleBox.getFruitQuantity() + " фруктов.");
        appleBox.addFruit(2);
        System.out.println("В коробке с яблоками " +appleBox.getFruitQuantity() + " фруктов.");
        appleBox.addFruit(28);
        System.out.println("В коробке с яблоками " +appleBox.getFruitQuantity() + " фруктов.");
        System.out.println("Коробка с яблоками весит: " + appleBox.getWeight());

        System.out.println("Коробка апельсинов весит столько же, сколько коробка яблок: " + orangeBox.compare(appleBox));

        Box<Orange> bigOrangeBox = new Box<>(new Orange());
        System.out.println("Фруктов в коробке orangeBox: " + orangeBox.getFruitQuantity());
        System.out.println("Фруктов в коробке bigOrangeBox: " + bigOrangeBox.getFruitQuantity());
        orangeBox.putInAnotheBox(bigOrangeBox);
        System.out.println("Фруктов в bigOrangeBox после пересыпания: " + bigOrangeBox.getFruitQuantity());
        System.out.println("Фруктов в orangeBox после пересыпания: " + orangeBox.getFruitQuantity());

//        bigOrangeBox.putInAnotheBox(appleBox); //ошибка компиляции при попытке пересыпать апельсины в коробку с яблоками

    }
}
