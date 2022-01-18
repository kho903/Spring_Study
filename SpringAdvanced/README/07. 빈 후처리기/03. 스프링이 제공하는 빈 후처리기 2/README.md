# 스프링이 제공하는 빈 후처리기 2
## 애플리케이션 로딩 로그
```text
EnableWebMvcConfiguration.requestMappingHandlerAdapter()
EnableWebMvcConfiguration.requestMappingHandlerAdapter() time=63ms
```
- 애플리케이션 서버를 실행해보면, 스프링이 초기화되면서 기대하지 않은 이러한 로그들이 올라온다. 그
이유는 지금 사용한 포인트컷이 단순히 메서드 이름에 `"request*", "order*", "save*"`만 포함되어
있으면 매칭 된다고 판단하기 때문이다.
- 결국 스프링이 내부에서 사용하는 빈에도 메서드 이름에 `request`라는 단어만 들어가 있으면 프록시가
만들어지게 되고, 어드바이스도 적용되는 것이다.
- 결론적으로 패키지에 메서드 이름까지 함께 지정할 수 있는 매우 정밀한 포인트컷이 필요하다.

## AspectJExpressionPointcut
- AspectJ라는 AOP에 특화된 포인트컷 표현식을 적용할 수 있다. 

## AutoProxyConfig - advisor2 추가
```java
@Bean
public Advisor advisor2(LogTrace logTrace) {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(* hello.proxy.app..*(..))");
    LogTraceAdvice advice = new LogTraceAdvice(logTrace);
    //advisor = pointcut + advice
    return new DefaultPointcutAdvisor(pointcut, advice);
}
```
- `AspectJExpressionPointcut` : AspectJ 포인트컷 표현식을 적용할 수 있다.
- `execution(* hello.proxy.app..*(..))` : AspectJ가 제공하는 포인트컷 표현식이다.
    - * : 모든 반환 타입
    - hello.proxy.app.. : 해당 패키지와 그 하위 패키지
    - *(..) : * 모든 메서드 이름, (..) 파라미터는 상관없음
- 쉽게 이야기해서 `hello.proxy.app` 패키지와 그 하위 패키지의 모든 메서드는 포인트컷의 매칭 대상이 된다.

### 실행
- http://localhost:8080/v1/request?itemId=hello
- http://localhost:8080/v2/request?itemId=hello
- http://localhost:8080/v3/request?itemId=hello
> 모두 동일 결과
- http://localhost:8080/v1/no-log
> 문제는 이 부분에 로그가 출력된다. advisor2 에서는 단순히 package를 기준으로 포인트컷 매칭을 했기 때문이다.

## AutoProxyConfig advisor3 추가
```java
@Bean
public Advisor advisor3(LogTrace logTrace) {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
    LogTraceAdvice advice = new LogTraceAdvice(logTrace);
    //advisor = pointcut + advice
    return new DefaultPointcutAdvisor(pointcut, advice);
}
```
표현식을 다음과 같이 수정했다.
- execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))
    - && : 두 조건을 모두 만족해야 함
    - ! : 반대
- hello.proxy.app 패키지와 하위 패키지의 모든 메서드는 포인트컷의 매칭하되, noLog() 메서드는 제외하라는 뜻

### 실행하면 로그가 나오면 안됨
- http://localhost:8080/v1/no-log
