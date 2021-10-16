# 로그인 기능
## LoginService
```java
package hello.login.domain.login;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class LoginService {
 private final MemberRepository memberRepository;
 /**
 * @return null이면 로그인 실패
 */
 public Member login(String loginId, String password) {
 return memberRepository.findByLoginId(loginId)
 .filter(m -> m.getPassword().equals(password))
 .orElse(null);
 }
}
```
- 로그인의 핵심 비즈니스 로직은 회원을 조회한 다음에 파라미터로 넘어온 password와 비교해서 
같으면 회원을 반환하고, 만약 password가 다르면 null을 반환한다.
  
## LoginForm
```java
package hello.login.web.login;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
@Data
public class LoginForm {
 @NotEmpty
 private String loginId;
 @NotEmpty
 private String password;
}
```
## LoginController
```java
package hello.login.web.login;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Objects;
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
 private final LoginService loginService;
 @GetMapping("/login")
 public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
 return "login/loginForm";
 }
 @PostMapping("/login")
 public String login(@Valid @ModelAttribute LoginForm form, BindingResult
bindingResult) {
 if (bindingResult.hasErrors()) {
 return "login/loginForm";
 }
 Member loginMember = loginService.login(form.getLoginId(),
form.getPassword());
 log.info("login? {}", loginMember);
 if (loginMember == null) {
 bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
 return "login/loginForm";
 }
 //로그인 성공 처리 TODO
 return "redirect:/";
 }
}
```
- 로그인 컨트롤러는 로그인 서비스를 호출해서 로그인에 성공하면 홈 화면으로 이동하고, 로그인에 실패하면
`bindingResult.reject()`를 사용해서 글로벌 오류 (`ObjectError`)를 생성한다.
  그리고 정보를 다시 입력하도록 로그인 폼을 뷰 템플릿으로 사용한다.
  
## 실행
- 실행해보면 로그인이 성공하면 홈으로 이동하고, 로그인에 실패하면 "아이디 또는 비밀번호가 맞지 않습니다."
라는 경고와 함께 로그인 폼이 나타난다.
- 그런데 아직 로그인이 되면 홈 화면에 고객이름이 보여야 한다는 요구사항 만족 X
