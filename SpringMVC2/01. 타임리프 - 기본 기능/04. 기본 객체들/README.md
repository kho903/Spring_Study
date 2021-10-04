# 기본 객체들
타임리프는 기본 객체들을 제공한다.
- `${#request}`
- `${#response}`
- `${#session}`
- `${#servletContext}`
- `${#locale}`
- 그런데 `#request`는 `HttpServletRequest` 객체가 그대로 제공되기 때문에 데이터를 조회하려면
`request.getParameter("data")`처럼 불편하게 접근해야 한다.
- 이런 점을 해결하기위해 편의 객체도 제공한다.
- HTTP 요청 파라미터 접근 : `param`
    - 예) `${param.paramData}`
- HTTP 세션 접근 : `session`
    - 예) `${session.sessionData`
- 스프링 빈 접근 : `@`
    - 예) `${@helloBean.hello('Spring!')}`
