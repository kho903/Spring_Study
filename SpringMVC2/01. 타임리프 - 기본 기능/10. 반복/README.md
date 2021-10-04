# 반복
- 타임리프에서 반복은 `th:each`를 사용한다. 추가로 반복에서 사용할 수 있는 여러 상태 값을 지원한다.

## 반복 기능
`<tr th:each="user : ${users}">`
- 반복 시 오른쪽 컬렉션(`${users}`)의 값을 하나씩 꺼내서 왼쪽 변수(`user`)에 담아서 태그를 반복 실행한다.
- `th:each`는 `List`뿐만 아니라 배열, `java.util.Iterable`, `java.util.Enumeration`을 구현한 모든 객체를 반복에 사용할 수 있다.
    - `Map`도 사용할 수 있는데, 이 경우 변수에 담기는 값은 `Map.Entry`이다.

## 반복 상태 유지
`<tr th:each="user, userStat : ${users}">`
- 반복의 두 번째 파라미터를 설정해서 반복의 상태를 확인할 수 있다.
- 두 번째 파라미터는 생략 가능한데, 생략하면 지정한 변수명(`user`) + `Stat`이 된다.

### 반복 상태 유지 기능
- `index` : 0부터 시작하는 값
- `count` : 1부터 시작하는 값
- `size` : 전체 사이즈
- `even`, `odd` : 홀수, 짝수 여부 (`boolean`)
- `first`, `last` : 처음, 마지막 여부 (`boolean`)
- `current` : 현재 객체
