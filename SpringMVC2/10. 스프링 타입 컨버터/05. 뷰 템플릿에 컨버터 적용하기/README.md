# 뷰 템플릿에 컨버터 적용하기
- 타임리프는 렌더링 시에 컨버터를 적용해서 렌더링 하는 방법을 편리하게 지원한다.
- 이전까지는 문자를 객체로 변환했다면, 이번에는 그 반대로 객체를 문자로 변환하는 작업을 확인할 수 있다.

## ConverterController
```java
package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConverterController {
    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number", 10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter-view";
    }
}
```
- `Model`에 숫자 `10000`와 `ipPort` 객체를 담아서 뷰 템플릿에 전달한다.
- converter-view.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<ul>
    <li>${number}: <span th:text="${number}"></span></li>
    <li>${{number}}: <span th:text="${{number}}"></span></li>
    <li>${ipPort}: <span th:text="${ipPort}"></span></li>
    <li>${{ipPort}}: <span th:text="${{ipPort}}"></span></li>
</ul>
</body>
</html>
```
- 타임리프는 `${{...}}`를 사용하면 자동으로 컨버전 서비스를 사용해서 변환된 결과를 출력해준다.
- 물론 스프링과 통합되어서 스프링이 제공하는 컨버전 서비스를 사용하므로, 우리가 등록한 
컨버터들을 사용할 수 있다.
- 변수 표현식 : `${...}`
- 컨버전 서비스 적용 : `${{...}}`
### 실행 / 실행 결과
- http://localhost:8080/converter-view
```text
• ${number}: 10000
• ${{number}}: 10000
• ${ipPort}: hello.typeconverter.type.IpPort@59cb0946
• ${{ipPort}}: 127.0.0.1:8080
```
- 실행 결과 로그
```text
IntegerToStringConverter : convert source=10000
IpPortToStringConverter : convert
source=hello.typeconverter.type.IpPort@59cb0946
```
- `${{number}}` : 뷰 템플릿은 데이터를 문자로 출력한다. 따라서 컨버터를 적용하게 되면
`Integer` 타입인 `10000`을 `String` 타입으로 변환하는 컨버터인 `IntegerToStringConverter`를
  실행하게 된다. 이 부분은 컨버터를 실행하지 않아도 타임리프가 숫자를 문자로 자동으로
  변환하기 때문에 컨버터를 적용할 때와 하지 않을 때가 같다.
- `${{ipPort}}` : 뷰 템플릿은 데이터를 문자로 출력한다. 따라서 컨버터를 적용하게 되면
`ipPort` 타입을 `String` 타입으로 변환해야 하므로 `IpPortToStringConverter`가
  적용된다. 그 결과 `127.0.0.1:8080`가 출력된다.

## 폼에 적용하기
### ConverterController - 코드 추가
```java
package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConverterController {
    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number", 10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter-view";
    }

    @GetMapping("/converter/edit")
    public String converterForm(Model model) {
        IpPort ipPort = new IpPort("127.0.0.1", 8080);
        Form form = new Form(ipPort);
        model.addAttribute("form", form);
        return "converter-form";
    }

    @PostMapping("/converter/edit")
    public String converterEdit(@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";
    }

    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }
}
```
`Form` 객체를 데이터를 전달하는 폼 객체로 사용한다.
- `Get /converter/edit` : `IpPort`를 뷰 템플릿 폼에 출력한다.
- `Post /converter/edit` : 뷰 템플릿 폼의 `IpPort` 정보를 받아서 출력한다.

converter-form.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form th:object="${form}" th:method="post">
    th:field <input type="text" th:field="*{ipPort}"><br/>
    th:value <input type="text" th:value="*{ipPort}">(보여주기 용도)<br/>
    <input type="submit"/>
</form>
</body>
</html>
```
- 타임리프의 `th:field`는 앞서 설명했듯이 `id`, `name`를 출력하는 등 다양한 기능이 있는데,
여기에 컨버전 서비스도 함께 적용된다.

### 실행
http://localhost:8080
- `GET /converter/edit`
    - `th:field`가 자동으로 컨버전 서비스를 적용해주어서 `${{ipPort}}`처럼 적용이 되었ㄷ가.
    따라서 `IpPort` -> `String`으로 변환된다.
- `POST /converter/edit`
    - `@ModelAttribute`를 사용해서 `String` -> `IpPort`로 변환된다.

