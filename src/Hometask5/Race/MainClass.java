//   Организуем гонки:
//   Все участники должны стартовать одновременно, несмотря на то, что на подготовку у каждого из них уходит разное время.
//   В туннель не может заехать одновременно больше половины участников (условность).
//   Попробуйте всё это синхронизировать.
//   Только после того как все завершат гонку, нужно выдать объявление об окончании.
//   Можете корректировать классы (в т.ч. конструктор машин) и добавлять объекты классов из пакета util.concurrent.

package Hometask5.Race;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MainClass {
    public static final int CARS_COUNT = 4;
    private static long countDownLatchCount;

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        // CyclicBarrier задерживает старт машин до готовности всех участников (CARS_COUNT) и поток main (+ 1) до обьявления о начале гонки:
        CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT + 1);

        // Semaphore ограничивает одновременный доступ в туннель для половины машин:
        Semaphore semaphore = new Semaphore(CARS_COUNT / 2);

        // CountDownLatch заставляет main поток ждать, пока все потоки машин не завершатся:
        CountDownLatch countDownLatch = new CountDownLatch(CARS_COUNT);

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(semaphore), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), cyclicBarrier, countDownLatch);
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        cyclicBarrier.await(); // main поток ждет, пока все машины не будут готовы
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        countDownLatch.await(); // main поток ждет, пока все машины не финишируют
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");


    }
}

