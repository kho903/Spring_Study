package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        int a = 10;
        int b = a;

        a = 20;
        System.out.println("a = " + a); // 20
        System.out.println("b = " + b); // 10

        Integer aa = new Integer(10);
        Integer bb = aa;
        // a.setValue(10);
        // 있다면 변경하겠지만 변경 자체를 불가능하게 만들었다.

        System.out.println("aa = " + aa); // 10
        System.out.println("bb = " + bb); // 10
    }
}
