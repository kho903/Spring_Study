package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        /*int a = 10;
        int b = a;

        a = 20;
        System.out.println("a = " + a); // 20
        System.out.println("b = " + b); // 10

        Integer aa = new Integer(10);
        Integer bb = aa;
        // a.setValue(10);
        // 있다면 변경하겠지만 변경 자체를 불가능하게 만들었다.

        System.out.println("aa = " + aa); // 10
        System.out.println("bb = " + bb); // 10*/

        int a = 10;
        int b = 10;
        System.out.println("(a==b) = " + (a == b)); // true

        Address address1 = new Address("city", "street", "10000");
        Address address2 = new Address("city", "street", "10000");
        System.out.println("(address1 == address2) = " + (address1 == address2)); // false
        System.out.println("(address1 equals address2) = " + (address1.equals(address2))); // 기본 false
        // equals 를 오버라이드하면 true
    }
}
