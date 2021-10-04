# 리터럴
## Literals
- 리터럴은 소스 코드상에 고정된 값을 말하는 용어이다.
- 예를 들어서 `String a = "Hello";`에서 `"HELLO"`는 문자 리터럴,
`int a = 10 * 20`에서 `10`, `20`은 숫자 리터럴 이다.

## 타임리프의 리터럴 종류
- 문자 : `'hello'`
- 숫자 : `10`
- 불린 : `true`
- null : `null`

## 타임리프의 리터럴
- 타임리프에서 문자 리터럴은 항상 `'`(작은 따옴표)로 감싸야 한다.
    - `<span th:text="'hello'">`
- 공백 없이 쭉 이어진다면 하나의 토큰으로 인지해서 작은 따옴표 생략 가능
- 오류 :
    - `<span th:text="hello world!"></span>`
    - 문자 리터럴은 원칙상 `'`로 감싸야 한다. 중간에 공백이 있어서 하나의 의미있는 토큰으로도 인식되지 않는다.
- 수정 :
    - `<span th:text="'hello world!'"></span>`
    - 이렇게 `'`로 감싸면 정상 동작한다.
- 리터럴 대체 (Literal substitutions)
    - `<span th:text="|hello ${data}|">`
