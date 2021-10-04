# URL 링크
- 타임리프에서 URL을 생성할 때는 @{...} 문법을 사용하면 된다.

## 단순한 URL
- `@{/hello}` -> `/hello`

## 쿼리 파라미터
- `@{/hello{param1=${param1}, param2=${param2}`
    - -> `/hello?param1=data&param2=data2`
    - `()` 안에 있는 부분은 쿼리 파라미터로 처리된다.

## 경로 변수
- `@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}`
    - -> `/hello/data1/data2`
    - URL 경로상에 변수가 있으면 `()` 부분은 경로 변수로 처리된다.

## 경로 변수 + 쿼리 파라미터
- `@{/hello/{param1}(param1=${param1}, param2=${param2})}`
    - -> `/hello/data1?param2=data2`
    - 경로 변수와 쿼리 파라미터를 함께 사용할 수 있다.

### 참고
상대경로, 절대경로, 프로토콜 기준을 표현할 수도 있다.
- `/hello` : 절대 경로
- `hello` : 상대 경로
