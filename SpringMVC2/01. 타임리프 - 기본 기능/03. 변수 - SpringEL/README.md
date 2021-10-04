# 변수 - SpringEL
- 타임리프에서 변수를 사용할 때는 변수 표현식을 사용한다.
- 변수 표현식 : `${...}`
- 그리고 이 변수 표현식에는 스프링 EL이라는 스프링이 제공하는 표현식을 사용할 수 있다.

## Object
- `user.username` : user의 username을 프로퍼티 접근 -> `user.getUsername()`
- `user['username']` : 위와 같음 -> `user.getUsername()`
- `user.getUsername()` : user의 `getUsername()`을 직접 호출

## List
- `users[0].username` : List에서 첫 번째 회원을 찾고 username 프로퍼티 접근 -> `list.get(0).getUsername()`
- `users[0]['username']` : 위와 같음
- `users[0].getUsername()` : List에서 첫 번째 회원을 찾고 메서드 직접 호출

## Map
- `userMap['userA'].username` : Map에서 userA를 찾고, username 프로퍼티 접근 -> `map.get("userA").getUsername()`
- `userMap['userA']['username']` : 위와 같음
- `userMap['userA'].getUsername()` : Map에서 userA를 찾고 메서드 직접 호출

## 지역 변수 선언
- `th:with`를 사용하면 지역 변수를 선언해서 사용할 수 있다. 지역 변수는 선언한 태그 안에서만 사용할 수 있다.
```html
<div th:with="first=${users[0]}">
    <p>처음 사람의 이름은 <span th:text="${first.username}"></span></p>
</div>
```
