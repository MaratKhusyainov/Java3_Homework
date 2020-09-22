package Hometask7;

public class ClassWithTests {

    @BeforeSuite
    public void methodBefore() {
        System.out.println("methodBefore");
    }

//    Вызовет RuntimeException:
//    @AfterSuite
//    private void methodAfter() {
//        System.out.println("methodAfter");
//    }

    @AfterSuite
    private void methodAfter02() {
        System.out.println("methodAfter02");
    }

    @Test(priority = 1)
    public void methodTest1() {
        System.out.println("methodTest1 with priority = 1");
    }

    @Test(priority = 10)
    public void methodTest2() {
        System.out.println("methodTest2 with priority = 10");
    }

    @Test(priority = 5)
    public void methodTest3() {
        System.out.println("methodTest3 with priority = 5");
    }

}