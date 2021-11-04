# 스프링에 Converter 적용하기
- 웹 애플리케이션에 `Converter`를 적용해보자.

## WebConfig - 컨버터 등록
```java
package hello.typeconverter;

import hello.typeconverter.converter.IntegerToStringConverter;
import hello.typeconverter.converter.IpPortToStringConverter;
import hello.typeconverter.converter.StringToIntegerConverter;
import hello.typeconverter.converter.StringToIpPortConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
    }
}
```
- 스프링은 내부에서 `ConversionService`를 제공한다. 우리는 `WebMvcConfigurer`가
제공하는 `addFormatters()`를 사용해서 추가하고 싶은 컨버터를 등록하면 된다. 이렇게 하면
스프링은 내부에서 사용하는 `ConversionService`에 컨버터를 추가해준다.

### HelloController - 기존 코드  
```java
@GetMapping("/hello-v2")
public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
}
```
## 실행 / 실행 로그
- http://localhost:8080/hello-v2?data=10
```text
StringToIntegerConverter : convert source=10
data = 10
```
- `?data=10`의 쿼리 파라미터는 문자이고 이것을 `Integer data`로 변환하는 과정이 필요하다.
- 실행해보면 직접 등록한 `StringToIntegerConverter`가 작동하는 로그를 확인할 수 있다.
- 그런데 생각해보면 `StringToIntegerConverter`를 등록하기 전에도 이 코드는 잘 수행되었다.
- 그것은 스프링이 내부에서 수 많은 기본 컨버터들을 제공하기 때문이다.
- 컨버터를 추가하면 추가한 컨버터가 기본 컨버터보다 높은 우선순위를 가진다.

## 직적 정의한 타입 `IpPort`
### HelloController - 추가
```java
@GetMapping("/ip-port")
public String ipPort(@RequestParam IpPort ipPort) {
     System.out.println("ipPort IP = " + ipPort.getIp());
     System.out.println("ipPort PORT = " + ipPort.getPort());
     return "ok";
}
```
### 실행 / 실행 로그
- http://localhost:8080/ip-port?ipPort=127.0.0.1:8080
```text
StringToIpPortConverter : convert source=127.0.0.1:8080
ipPort IP = 127.0.0.1
ipPort PORT = 8080
```
- `?ipPort=127.0.0.1:8080` 쿼리 스트링이 `@RequestParam IpPort ipPort`에서
객체 타입으로 잘 변환된 것을 확인할 수 있다.

### 처리 과정
- `@RequestParam`은 `@RequestParam`을 처리하는 `ArgumentResolver`인
`RequestParamMethodArgumentResolver`에서 `ConversionService`를 사용해서
타입을 변환한다. 부모 클래스와 다양한 외부 클래스를 호출하는 등 복잡한 내부 과정을 거치기 때문에
대략 이렇게 처리되는 것으로 이해해도 충분하다. 만약 더 깊이 있게 확인하고 싶으면
`IpPortConverter`에 디버그 브레이크 포인트를 걸어서 확인.
