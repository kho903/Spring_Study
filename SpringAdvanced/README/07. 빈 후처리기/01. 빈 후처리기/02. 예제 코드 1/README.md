# 빈 후처리기 - 예제 코드 1
- 빈 후처리기를 학습하기 전에 먼저 일반적인 스프링 빈 등록 과정 코드

## BasicTest
```java
package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import
        org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BasicTest {
    @Test
    void basicConfig() {
        ApplicationContext applicationContext = new
                AnnotationConfigApplicationContext(BasicConfig.class);
        //A는 빈으로 등록된다.
        A a = applicationContext.getBean("beanA", A.class);
        a.helloA();
        //B는 빈으로 등록되지 않는다.
        Assertions.assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean(B.class));
    }

    @Slf4j
    @Configuration
    static class BasicConfig {
        @Bean(name = "beanA")
        public A a() {
            return new A();
        }
    }

    @Slf4j
    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }
}
```
- `new AnnotationConfigApplicationContext(BasicConfig.class)`
- 스프링 컨테이너를 생성하면서 `BasicConfig.class`를 넘겨주었다. `BasicConfig.class`
설정 파일은 스프링 빈으로 등록된다.

## 등록
- BasicConfig.class
```java
@Bean(name = "beanA")
public A a() {
    return new A();
}
```
- `beanA`라는 이름으로 `A` 객체를 스프링 빈으로 등록했다.

## 조회
- `A a = applicationContext.getBean("beanA", A.class)`
    - `beanA`라는 이름으로 `A` 타입의 스프링 빈을 찾을 수 있다.
- `applcationContext.getBean(B.class)`
    - `B` 타입의 객체는 스프링 빈으로 등록한 적이 없기 때문에 스프링 컨테이너에서
    찾을 수 없다.
