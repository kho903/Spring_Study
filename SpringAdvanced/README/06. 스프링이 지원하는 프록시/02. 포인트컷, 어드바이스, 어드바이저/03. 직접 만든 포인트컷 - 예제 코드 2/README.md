# 직접 만든 포인트컷 - 예제 코드 2
- `save()` 메서드에는 어드바이스 로직을 적용하지만, `find()` 메서드에는 어드바이스 로직을
적용하지 않도록 해보자.
- 물론 과거에 했던 코드와 유사하게 어드바이스에 로직을 추가해서 메서드 이름을 보고 코드를 실행할지
말지 분기를 타도 된다. 하지만 이런 기능에 특화되어서 제공되는 것이 바로 포인트컷이다.
- 포인트컷 직접 구현.
## Pointcut 관련 인터페이스 - 스프링 제공
```java
public interface Pointcut {
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}

public interface ClassFilter {
    boolean matches(Class<?> clazz);
}

public interface MethodMatcher {
    boolean matches(Method method, Class<?> targetClass);
    //..
}
```
- 포인트컷은 크게 `ClassFilter`와 `MethodMatcher` 둘로 이루어진다. 이름 그대로 하나는
클래스가 맞는 지, 하나는 메서드가 맞는지 확인할 때 사용한다. 둘 다 `true`로 반환해야
어드바이스르 적용할 수 있다.
- 일반적으로 스프링이 이미 만들어둔 구현체를 사용, but, 구현해본다.

## AdvisorTest - advisorTest2 추가
```java
@Test
@DisplayName("직접 만든 포인트컷")
void advisorTest2() {
    ServiceImpl target = new ServiceImpl();
    ProxyFactory proxyFactory = new ProxyFactory(target);
    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new
            MyPointcut(), new TimeAdvice());
    proxyFactory.addAdvisor(advisor);
    ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
    proxy.save();
    proxy.find();
}

static class MyPointcut implements Pointcut {
    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MyMethodMatcher();
    }
}

static class MyMethodMatcher implements MethodMatcher {
    private String matchName = "save";

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        boolean result = method.getName().equals(matchName);
        log.info("포인트컷 호출 method={} targetClass={}", method.getName(),
                targetClass);
        log.info("포인트컷 결과 result={}", result);
        return result;
    }

    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        throw new UnsupportedOperationException();
    }
}
```

## MyPointcut
- 직접 구현한 포인트컷이다. `Pointcut` 인터페이스를 구현한다.
- 현재 메서드 기준으로 로직을 적용하면 된다. 클래스 필터는 항상 `true`를 반환하도록 했고, 메서드
비교 기능은 `MyMethodMatcher`를 사용한다.

## MyMethodMatcher
- 직접 구현한 `MethodMatcher`이다. `MethodMatcher` 인터페이스를 구현한다.
- `matches()` : 이 메서드에 `method`, `targetClass` 정보가 넘어온다. 이정보로
어드바이스를 적용할 지 적용하지 않을 지 판단할 수 있다.
- 여기서는 메서드 이름이 `"save"`인 경우에 `true`를 반환하도록 판단 로직을 적용했다.
- `isRuntime()`, `matches(... args)` : `isRuntime()`이 값이 참이면 
`matches(... args)` 메서드가 대신 호출된다. 동적으로 넘어오는 매개변수를 판단 로직으로 사용할 수 있다.
    - `isRuntime()`이 `false`인 경우 클래스의 정적 정보만 사용하기 때문에 스프링이 내부에서
    캐싱을 통해 성능 향상이 가능하지만, `isRuntime()`이 `true`인 경우 매개변수가 동적으로
    변경된다고 가정하기 때문에 캐싱을 하지 않는다.

### new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice())
- 어드바이저에 직접 구현한 포인트컷을 사용한다.

### 실행결과
```text
#save() 호출
AdvisorTest - 포인트컷 호출 method=save targetClass=class
hello.proxy.common.service.ServiceImpl
AdvisorTest - 포인트컷 결과 result=true
TimeAdvice - TimeProxy 실행
ServiceImpl - save 호출
TimeAdvice - TimeProxy 종료 resultTime=1ms

#find() 호출
AdvisorTest - 포인트컷 호출 method=find targetClass=class
hello.proxy.common.service.ServiceImpl
AdvisorTest - 포인트컷 결과 result=false
ServiceImpl - find 호출
```
- 실행 결과를 보면 기대한 것과 같이 `save()`를 호출할 때는 어드바이스가 적용되지만, `find()`를
호출할 때는 어드바이스가 적용되지 않는다.

## save() 호출 프로세스
1. 클라이언트가 프록시의 `save()`를 호출한다.
2. 포인트컷에 `Service` 클래스의 `save()` 메서드에 어드바이스를 적용해도 될 지 물어본다.
3. 포인트컷이 `true`를 반환한다. 따라서 어드바이스를 호출해서 부가 기능을 적용한다.
4. 이후 실제 인스턴스의 `save()`를 호출한다.

## find() 호출 프로세스
1. 클라이언트가 프록시의 `find()`를 호출한다.
2. 포인트컷에 `Service`클래스의 `find()` 메서드에 어드바이스를 적용해도 될 지 물어본다.
3. 포인트컷이 `false`를 반환한다. 따라서 어드바이스를 호출하지 않고, 부가 기능도 적용되지 않는다.
4. 실제 인스턴스를 호출한다.
