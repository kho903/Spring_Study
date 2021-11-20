# 홈 화면과 레이아웃
## 홈 컨트롤러 등록
```java
package jpabook.jpashop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
```
## 스프링 부트 타임리프 기본 설정
```yaml
spring:
  thymeleaf:
     prefix: classpath:/templates/
     suffix: .html
```
- 스프링 부트 타임리프 viewName 매핑
    - `resources:templates/` + {ViewName} + `.html`
    - `resources:templates/home.html`
- 반환한 문자(`home`)과 스프링 부트 설정 `prefix`, `suffix` 정보를 사용해서 렌더링할 뷰(`html`)를 찾는다.
