# 스프링이 제공하는 포인트컷 - 에제 코드 3
- 스프링은 우리가 필요한 포인트컷을 이미 대부분 제공한다.
- 이번에는 스프링이 제공하는 `NameMatchMethodPointcut`를 사용해서 구현.

## AdvisorTest - advisorTest3 추가
```java
@Test
@DisplayName("스프링이 제공하는 포인트컷")
void advisorTest3() {
    ServiceImpl target = new ServiceImpl();
    ProxyFactory proxyFactory = new ProxyFactory(target);
    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedNames("save");
    DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
    proxyFactory.addAdvisor(advisor);
    ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
    
    proxy.save();
    proxy.find();
}
```

## NameMatchMethodPointcut 사용 코드
```java
NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
pointcut.setMappedNames("save");
```
- `NameMatchMethodPointcut`을 생성하고 `setMappedNames(...)`으로 메서드 이름을
지정하면 포인트컷이 완성된다.

### 실행 결과
```text
#save() 호출
TimeAdvice - TimeProxy 실행
ServiceImpl - save 호출
TimeAdvice - TimeProxy 종료 resultTime=1ms
#find() 호출
ServiceImpl - find 호출
```
- 실행 결과를 보면 `save()`를 호출할 때는 어드바이스가 적용되지만, `find()`를
호출할 때는 어드바이스가 적용되지 않는다.

## 스프링이 제공하는 포인트컷
무수히 많다.
- `NameMatchMethodPointcut` : 메서드 이름을 기반으로 매칭한다. 내부에서는
`PatternMatchUtils`를 사용한다.
    - 예) `*xxx*` 허용
- `JdkRegexMethodPointcut` : JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
- `TruePointcut` : 항상 참을 반환한다.
- `AnnotationMatchingPointcut` : 애노테이션으로 매칭한다.
- `AspectJExpressPointcut` : aspectJ 표현식으로 매칭한다.

### 가장 중요한 것은 aspectJ 표현식
- 여기에서 사실 다른 것은 중요하지 않다. 실무에서는 사용하기도 편리하고 기능도 가장 많은
aspectJ 표현식을 기반으로 하는 `AspectJExpressPointcut`을 사용하게 된다.
