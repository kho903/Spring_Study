# 로그인 처리하기 - 쿠키 사용
## 로그인 상태 유지하기
- 로그인의 상태를 어떻게 유지할 수 있을까?
    - 쿼리 파라미터를 계속 유지하면서 보내는 것은 매우 어렵고 번거로운 작업이다.
    - 쿠키를 사용하자.

## 쿠키
- 서버에서 로그인에 성공하면 HTTP 응답에 쿠키를 담아서 브라우저에 전달하자. 그러면 브라우저는 앞으로 해당 쿠키를 지속해서 보내준다.
- 모든 요청에 쿠키 정보 자동 포함

### 쿠키에는 영속 쿠키와 세션 쿠키가 있다.
- 영속 쿠키 : 만료 날짜를 입력하면 해당 날짜까지 유지
- 세션 쿠키 : 만료 날짜를 생략하면 브라우저 종료시 까지만 유지

## LoginController - login()
- 로그인 성공시 세션 쿠키를 생성하자.
```java
@PostMapping("/login")
public String login(@Valid @ModelAttribute LoginForm form,BindingResult
        bindingResult,HttpServletResponse response){
        if(bindingResult.hasErrors()) {
          return"login/loginForm";
        }
        Member loginMember=loginService.login(form.getLoginId(),
        form.getPassword());
        log.info("login? {}",loginMember);
        if(loginMember==null) {
          bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
          return"login/loginForm";
        }
        //로그인 성공 처리
        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        Cookie idCookie=new Cookie("memberId",
        String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);
        return"redirect:/";
}
```
### 쿠키 생성 로직
```java
Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
response.addCookie(idCookie);
```
- 로그인에 성공하면 쿠키를 생성하고 `HttpServletResponse`에 담는다. 쿠키 이름은 `memberId`이고, 값은 회원의 `id`를 담아둔다.
- 웹 브라우저는 종료 전까지 회원의 `id`를 서버에 계속 보내줄 것이다.

## 홈 - 로그인 처리
```java
package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

  private final MemberRepository memberRepository;

  // @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/")
  public String homeLogin(
          @CookieValue(name = "memberId", required = false) Long memberId,
          Model model) {
    if (memberId == null) {
      return "home";
    }
    //로그인
    Member loginMember = memberRepository.findById(memberId);
    if (loginMember == null) {
      return "home";
    }
    model.addAttribute("member", loginMember);
    return "loginHome";
  }
}
```
- `@CookieValue`를 사용하면 편리하게 쿠키를 조회할 수 있다.
- 로그인 하지 않은 사용자도 홈에 접근할 수 있기 때문에 `required = false`를 사용한다.

### 로직 분석
- 로그인 쿠키 (`memberId`)가 없는 사용자는 기존 `home`으로 보낸다. 추가로 로그인 쿠키가 있어도 회원이 없으면 `home`으로 보낸다.
- 로그인 쿠키 (`memberId`)가 있는 사용자는 로그인 사용자 전용 홈 화면인 `loginHome`으로 보낸다. 
  추가로 홈 화면에 회원 관련 정보도 출력해야 해서 `member` 데이터도 모델에 담아서 전달한다.

### 홈 - 로그인 사용자 전용
- loginHome.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8">
  <link th:href="@{/css/bootstrap.min.css}"
        href="../css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container" style="max-width: 600px">
  <div class="py-5 text-center">
    <h2>홈 화면</h2>
  </div>
  <h4 class="mb-3" th:text="|로그인: ${member.name}|">로그인 사용자 이름</h4>
  <hr class="my-4">
  <div class="row">
    <div class="col">
      <button class="w-100 btn btn-secondary btn-lg" type="button"
              th:onclick="|location.href='@{/items}'|">
        상품 관리
      </button>
    </div>
    <div class="col">
      <form th:action="@{/logout}" method="post">
        <button class="w-100 btn btn-dark btn-lg"
                onclick="location.href='items.html'" type="submit">
          로그아웃
        </button>
      </form>
    </div>
  </div>
  <hr class="my-4">
</div> <!-- /container -->
</body>
</html>
```
- `th:text="|로그인: ${member.name}|"` : 로그인에 성공한 사용자 이름을 출력한다.
- 상품 관리, 로그아웃 버튼을 노출한다.

### 실행
- 로그인에 성공하면 사용자 이름이 출력되면서 상품 관리, 로그아웃 버튼을 확인할 수 있다. 로그인에 성공시 세션 쿠키가 지속해서 유지되고, 웹 브라우저에서 서버에 요청 시 `memberId` 쿠키를 계속 보내준다.

## 로그아웃 기능
- 세션 쿠키 이므로 웹 브라우저 종료시
- 서버에서 해당 쿠키의 종료 날짜를 0으로 지정
### LoginController - logout 기능 추가
```java
@PostMapping("/logout")
public String logout(HttpServletResponse response){
        expireCookie(response,"memberId");
        return"redirect:/";
}
private void expireCookie(HttpServletResponse response,String cookieName){
        Cookie cookie=new Cookie(cookieName,null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
}
```

