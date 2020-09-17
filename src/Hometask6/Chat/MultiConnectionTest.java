package Hometask6.Chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiConnectionTest {
    public static void main(String[] args) throws InterruptedException {

        // Фабрика клиентов: создаем пул потоков с максмальным числом потоков = 10
        ExecutorService exec = Executors.newFixedThreadPool(10);
        int j = 0;

        // Запускаем 10 консольных клиентов. Т.к. на стороне сервера ExecutorService содержит пул
        // с макс числом потоков = 2, то в чат одновременно смогут зайти только 2.
        while (j < 10) {
            j++;
            exec.execute(new Client());
            Thread.sleep(200);
        }

        //закрываем фабрику
        exec.shutdown();
    }
}