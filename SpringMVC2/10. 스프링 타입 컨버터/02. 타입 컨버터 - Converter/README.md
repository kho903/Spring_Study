# 타입 컨버터 - Converter
- 타입 컨버터를 사용하려면 `org.springframework.core.convert.converter.Converter`
인터페이스를 구현하면된다.
- Converter라는 이름의 인터페이스가 많으니 조심해야 한다.

## 컨버터 인터페이스 
```java
package org.springframework.core.convert.converter;

public interface Converter<S, T> {
    T convert(S source);
}
```
- 먼저 가장 단순한 형태인 문자를 숫자로 바꾸는 타입 컨버터를 만들어보자.

## StringToIntegerConverter - 문자를 숫자로 변환하는 타입 컨버터
```java
package hello.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) {
        log.info("convert source={}", source);
        return Integer.valueOf(source);
    }
}
```
- `String` -> `Integer`로 변환하기 때문에 소스가 `String`이 된다.
- 이 문자를 `Integer.valueOf(source)`를 사용해서 숫자로 변경한 다음에 변경된 숫자를 반환하면 된다.

## IntegerToStringConverter - 숫자를 문자로 변환하는 타입 컨버터
```java
package hello.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String> {
    @Override
    public String convert(Integer source) {
        log.info("convert source={}", source);
        return String.valueOf(source);
    }
}
```
- 숫자 -> 문자 타입 컨버터
- 이번에는 숫자가 입력되기 때문에 소스가 Integer가 된다. String.valueOf(source) 를 사용해서 문자로
변경한 다음 변경된 문자를 반환하면 된다.

### ConverterTest - 타입 컨버터 테스트 코드
```java
package hello.typeconverter.converter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ConverterTest {
    @Test
    void stringToInteger() {
        StringToIntegerConverter converter = new StringToIntegerConverter();
        Integer result = converter.convert("10");
        assertThat(result).isEqualTo(10);
    }

    @Test
    void integerToString() {
        IntegerToStringConverter converter = new IntegerToStringConverter();
        String result = converter.convert(10);
        assertThat(result).isEqualTo("10");
    }
}
```
## 사용자 정의 컨버터
- 127.0.0.1:8080 과 같은 IP, PORT를 입력하면 IpPort 객체로 변환하는 컨버터를 만들어보자.

### IpPort
```java
package hello.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
```
- 롬복의 `@EqualsAndHashCode`를 넣으면 모든 필드를 사용해서 `equals()`, `hashcode()`를 생성한다.
- 따라서 모든 필드의 값이 같다면 `a.equals(b)`의 결과가 참이 된다.

### StringToIpPortConverter - 컨버터
```java
package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {
    @Override
    public IpPort convert(String source) {
        log.info("convert source={}", source);
        String[] split = source.split(":");
        String ip = split[0];
        int port = Integer.parseInt(split[1]);
        return new IpPort(ip, port);
    }
}
```
- `127.0.0.1:8080` 같은 문자를 입력하면 `IpPort` 객체를 만들어 반환한다

### IpPortToStringConverter
```java
package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class IpPortToStringConverter implements Converter<IpPort, String> {
    @Override
    public String convert(IpPort source) {
        log.info("convert source={}", source);
        return source.getIp() + ":" + source.getPort();
    }
}
```
- `IpPort` 객체를 입력하면 `127.0.0.1:8080`같은 문자를 반환한다.

### ConverterTest - IpPort 컨버터 테스트 추가
```java
@Test
void stringToIpPort() {
     StringToIpPortConverter converter = new StringToIpPortConverter();
     String source = "127.0.0.1:8080";
     IpPort result = converter.convert(source);
     assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));
}
@Test
void ipPortToString() {
     IpPortToStringConverter converter = new IpPortToStringConverter();
     IpPort source = new IpPort("127.0.0.1", 8080);
     String result = converter.convert(source);
     assertThat(result).isEqualTo("127.0.0.1:8080");
}
```
- 타입 컨버터 인터페이스가 단순해서 이해하기 어렵지 않을 것이다. 
- 그런데 이렇게 타입 컨버터를 하나하나 직접 사용하면, 개발자가 직접 컨버팅 하는 것과 큰 차이가 없다.
- 타입 컨버터를 등록하고 관리하면서 편리하게 변환 기능을 제공하는 역할을 하는 무언가가 필요하다.

### 참고
- 스프링은 용도에 따라 다양한 방식의 타입 컨버터를 제공한다.
    - `Converter` -> 기본 타입 컨버터
    - `ConverterFactory` -> 전체 클래스 계층 구조가 필요할 때
    - `GenericConverter` -> 정교한 구현, 대상 필드의 애노테이션 정보 사용 가능
    - `ConditionalGenericConverter` -> 특정한 조건이 참인 경우에만 실행

### 참고
- 스프링은 문자, 숫자, 불린, Enum 등 일반적인 타입에 대한 대부분의 컨버터를 기본으로 제공한다.
- IDE에서 `Converter` , `ConverterFactory` , `GenericConverter` 의 구현체를 찾아보면 수 많은 컨버터를 확인할 수 있다.

