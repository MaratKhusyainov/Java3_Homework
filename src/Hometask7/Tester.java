// 1. Создать класс, который может выполнять «тесты», в качестве тестов выступают классы с наборами методов
// с аннотациями @Test. Для этого у него должен быть статический метод start(), которому в качестве
// параметра передается или объект типа Class, или имя класса. Из «класса-теста» вначале должен быть
// запущен метод с аннотацией @BeforeSuite, если такой имеется, далее запущены методы с аннотациями @Test,
// а по завершению всех тестов – метод с аннотацией @AfterSuite. К каждому тесту необходимо также добавить
// приоритеты (int числа от 1 до 10), в соответствии с которыми будет выбираться порядок их выполнения,
// если приоритет одинаковый, то порядок не имеет значения. Методы с аннотациями @BeforeSuite и @AfterSuite
// должны присутствовать в единственном экземпляре, иначе необходимо бросить RuntimeException при запуске «тестирования».
// Это домашнее задание никак не связано с темой тестирования через JUnit и использованием этой библиотеки,
// то есть проект пишется с нуля.


package Hometask7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class Tester {

    public static void start(String className) {
        int beforeMethodPriority = -1;
        int afterMethodPriority = 11;
        int beforeMethodCount = 0;
        int afterMethodCount = 0;
        int priority;
        Map<Integer, Method> classMethods = new TreeMap<>();

        try {
            Class clazz = Class.forName(className); // получаем передаваемый класс в виде обьекта типа Class
            Method[] methods = clazz.getDeclaredMethods(); // получаем массив, содержащий все методы класса
            for (Method o : methods) {
                if (o.getAnnotation(BeforeSuite.class) != null) {
                    beforeMethodCount++;
                    if (beforeMethodCount > 1)
                        throw new RuntimeException("В классе больше одного метода с аннотацией BeforeSuite");
                    classMethods.put(beforeMethodPriority, o);
                }
                if (o.getAnnotation(AfterSuite.class) != null) {
                    afterMethodCount++;
                    if (afterMethodCount > 1)
                        throw new RuntimeException("В классе больше одного метода с аннотацией AfterSuite");
                    classMethods.put(afterMethodPriority, o);
                }
                if (o.getAnnotation(Test.class) != null) {
                    priority = o.getAnnotation(Test.class).priority();
                    classMethods.put(priority, o);
                }
            }

            for (Map.Entry<Integer, Method> entry : classMethods.entrySet()) {
                Object classInstance = clazz.getDeclaredConstructor().newInstance();
                entry.getValue().setAccessible(true); // делаем private метды доступными для запуска
                entry.getValue().invoke(classInstance); // динамически вызываем методы класса
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    // Проверка последовательности выполнения методов:
    public static void main(String[] args) {
        Tester.start("Hometask7.ClassWithTests");
    }
}
