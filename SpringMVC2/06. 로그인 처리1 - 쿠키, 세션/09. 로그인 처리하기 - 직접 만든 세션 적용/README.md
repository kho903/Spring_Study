# 로그인 처리하기 - 직접 만든 세션 적용
## LogicController - loginV2()
```java
@PostMapping("/login")
public String loginV2(@Valid @ModelAttribute LoginForm form,BindingResult 
  bindingResult,HttpServletResponse response){
    if(bindingResult.hasErrors()){
        return"login/loginForm";
    }
    Member loginMember=loginService.login(form.getLoginId(),
    form.getPassword());
    log.info("login? {}",loginMember);
    
    if(loginMember==null){
      bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
      return"login/loginForm";
    }
    //로그인 성공 처리
    //세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
    sessionManager.createSession(loginMember,response);
    return"redirect:/";
}
```
- `private final SessionManager sessionManager;` 주입
- `sessionManager.createSession(loginMember, response);`
로그인 성공 시 세션을 등록한다. 세션에 `loginMember`를 저장해두고, 쿠키도 함께 발행한다.

## LoginController - logoutV2()
```java
@PostMapping("/logout")
public String logoutV2(HttpServletRequest request) {
   sessionManager.expire(request);
   return "redirect:/";
}
```
- 로그아웃 시 해당 세션의 정보를 제거한다.

## HomeController - homeLoginV2()
```java
@GetMapping("/")
public String homeLoginV2(HttpServletRequest request, Model model) {
   //세션 관리자에 저장된 회원 정보 조회
   Member member = (Member)sessionManager.getSession(request);
   if (member == null) {
     return "home";
   }
   //로그인
   model.addAttribute("member", member);
   return "loginHome";
}
```
- `private final SessionManager sessionManager;` 주입
- 세션 관리자에서 저장된 회원 정보를 조회한다. 만약 회원 정보가 없으면, 쿠키나 세션이 없는 것이므로 로그인되지 않은 것으로 처리한다.

### 정리
- 사실 세션이라는 것이 뭔가 특별한 것이 아니라 단지 쿠키를 사용하는데, 서버에서 데이터를 유지하는 방법일 뿐이다.
- 서블릿도 세션 개념을 지원한다.
- 서블릿이 공식 지원하는 세션은 우리가 직접 만든 세션과 동작 방식이 거의 같다.
- 사용하지마 않으면 해당 세션을 삭제하는 기능을 제공한다.
