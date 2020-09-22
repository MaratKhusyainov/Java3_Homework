package Hometask5.Race;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch countDownLatch;
    private static int CARS_COUNT;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, CyclicBarrier cb, CountDownLatch cd1) {
        countDownLatch = cd1;
        this.cyclicBarrier = cb;
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + "готовится");
            Thread.sleep(500 + (int) Math.random() * 800);
            System.out.println(this.name + "готов");
            cyclicBarrier.await(); //ждёт, пока все участники не будут готовы
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        countDownLatch.countDown(); // сообщаем потоку main о финишировании

        // Проверка на победу: если текущий счет countDownLatch меньше заданного на 1 ---> игрок финишировал первым:
        if (countDownLatch.getCount() == MainClass.CARS_COUNT - 1)
            System.out.println("******* " + this.name + " WIN *******");
    }
}
