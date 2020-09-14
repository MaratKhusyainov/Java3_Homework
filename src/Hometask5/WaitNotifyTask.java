
// 1. Создать три потока, каждый из которых выводит определенную букву (A, B и C) 5 раз
// (порядок – ABСABСABС). Используйте wait/notify/notifyAll.

package Hometask5;

public class WaitNotifyTask {
    private char nextLetter = 'A';

    public synchronized void printA() {
        try {
            //System.out.println("a-turn);
            while (nextLetter !='A') {
            //System.out.println("a-wait");
                wait();
            }
            System.out.println("A");
            nextLetter = 'B';
            //System.out.println("a-notify");
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printB() {
        try {
          //System.out.println("b-turn");
            while (nextLetter != 'B') {
          //System.out.println("b-wait");
                wait();
            }
           System.out.println("B");
            nextLetter = 'C';
         //System.out.println("b-notify");
            notifyAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printC() {
        try {
          //System.out.println("c-turn");
            while (nextLetter != 'C') {
         //System.out.println("c-wait");
                wait();
            }
            System.out.println("C");
            nextLetter = 'A';
         //System.out.println("c-notify");
            notifyAll();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        WaitNotifyTask wnt = new WaitNotifyTask();
        Thread t1 = new Thread(()->{for(int i = 0; i < 5; i++) wnt.printA();});
        Thread t2 = new Thread(()->{for(int i = 0; i < 5; i++) wnt.printB();});
        Thread t3 = new Thread(()->{for(int i = 0; i < 5; i++) wnt.printC();});

        t1.start();
        t2.start();
        t3.start();

    }
}

