# 로그인 처리하기 - 서블릿 HTTP 세션 1
- 세션이라는 개념은 대부분의 웹 애플리케이션에 필요한 것이다. 어쩌면 웹이 등장하면서부터 나온 문제이다.
- 서블릿은 세션을 위해 `HttpSession`이라는 기능을 제공하는데, 지금까지 나온 문제들을 해결해준다.

## HttpSession 소개
- 서블릿이 제공하는 `HttpSession`도 결국 우리가 직접 만든 `SessionManager`와 같은 방식으로 동작한다.
- 서블릿을 통해 `HttpSession`을 생성하면 다음과 같은 쿠키를 생성한다. 쿠키이름이 `JSESSIONID`이고,
값은 추정 불가능한 랜덤 값이다.<br>
`Cookie: JSESSIONID=1R79E23B513F52388R6FDD8C97B0AD12`
  
## HttpSession 사용
### SessionConst
```java
package hello.login.web;

public class SessionConst {
    public static final String LOGIN_MEMBER = "loginMember";
}
```
- `HttpSession`에 데이터를 보관하고 조회할 때, 같은 이름이 중복되어 사용되므로, 상수를 하나 정의했다.

### LoginController - loginV3()
```java
@PostMapping("/login")
public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult
bindingResult, HttpServletRequest request) {
     if (bindingResult.hasErrors()) {
         return "login/loginForm";
     }
     Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
     log.info("login? {}", loginMember);
     if (loginMember == null) {
         bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
         return "login/loginForm";
     }
     //로그인 성공 처리
     //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
     HttpSession session = request.getSession();
     //세션에 로그인 회원 정보 보관
     session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
     return "redirect:/";
}
```
### 세션 생성과 조회
- 세션을 생성하려면 `request.getSession(true)`를 사용하면 된다.
- `public HttpSession getSession(boolean create);`

### 세션의 `create` 옵션
- `request.getSession(true)`
    - 아무것도 적지 않으면 true와 동일하다.
    - 세션이 있으면 기존 세션을 반환한다.
    - 세션이 없으면 새로운 세션을 생성해서 반환한다.
- `request.getSession(false)`
    - 세션이 있으면 기존 세션을 반환한다.
    - 세션이 없으면 새로운 세션을 생성하지 않는다. null을 반환한다.

### LoginController = logoutV3()
```java
@PostMapping("/logout")
public String logoutV3(HttpServletRequest request) {
     //세션을 삭제한다.
     HttpSession session = request.getSession(false);
     if (session != null) {
         session.invalidate();
     }
     return "redirect:/";
}
```
- `session.invalidate() : 세션을 제거한다.`

### HomeController - homeLoginV3()
```java
@GetMapping("/")
public String homeLoginV3(HttpServletRequest request, Model model) {
     //세션이 없으면 home
     HttpSession session = request.getSession(false);
     if (session == null) {
         return "home";
     }
     Member loginMember = (Member)
     session.getAttribute(SessionConst.LOGIN_MEMBER);
     //세션에 회원 데이터가 없으면 home
     if (loginMember == null) {
         return "home";
     }
     //세션이 유지되면 로그인으로 이동
     model.addAttribute("member", loginMember);
     return "loginHome";
}
```
- `request.getSession(false)` : `request.getSession()`를 사용하면 기본값이 `create: true`이므로,
로그인 하지 않을 사용자도 의미없는 세션이 만들어진다. 따라서 세션을 찾아서 사용하는 시점에는 
`create: false` 옵션을 사용해서 세션을 생성하지 않아야 한다.
- `session.getAttribute(SessionConst.LOGIN_MEMBER)` : 로그인 시점에 세션에 보관한 회원 객체를 찾는다.
